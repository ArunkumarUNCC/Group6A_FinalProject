package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSharedUserPhoto extends AppCompatActivity {

    final String fNOTIFICATIONS = "Notifications";
    final String fFROMUSER = "fromUser";
    final String fALBUM_COLUMN = "album";
    final String fNAME = "name";
    final String fOWNER = "uploadedBy";
    final String fTHUMBNAIL = "thumbnail";
    final int fSELECT_PICTURE = 1;

    EditText fSharedPhotoTitle;
    ParseImageView fSharedPhotoThumbnail;
    TextView fAddedBy,fAddedTo;
    Button fButtonDelete,fButtonSave,fButtonCancel;

    String fPhotoId,fPhotoTitle;
    ParseFile fPhotoParseFile;
    ParseObject fAlbumObject;
    ParseUser fPhotoOwner;
    int fPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared_user_photo);

        getItems();
        setItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_shared_user_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getItems() {
        fSharedPhotoTitle = (EditText) findViewById(R.id.editTextSharedPhotoTitle);
        fSharedPhotoThumbnail = (ParseImageView) findViewById(R.id.imageViewSharedPhotoThumbnail);
        fAddedBy = (TextView) findViewById(R.id.textViewAddedBy);
        fAddedTo = (TextView) findViewById(R.id.textViewAddedTo);
        fButtonDelete = (Button) findViewById(R.id.buttonDeleteSharedPhoto);
        fButtonSave = (Button) findViewById(R.id.buttonSaveSharedPhoto);
        fButtonCancel = (Button) findViewById(R.id.buttonCancelSharing);

        fPhotoId = getIntent().getStringExtra("photoId");
        fPosition = getIntent().getExtras().getInt("photoPosition");
    }

    private void setItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> lGetNotifications = ParseQuery.getQuery(fNOTIFICATIONS);
                lGetNotifications.include(fFROMUSER);
                lGetNotifications.include(fALBUM_COLUMN);
                lGetNotifications.include(fALBUM_COLUMN);
                lGetNotifications.whereEqualTo("objectId", fPhotoId);
                lGetNotifications.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null){
                            final ParseObject lObject = objects.get(0);
                            fPhotoParseFile = lObject.getParseFile(fTHUMBNAIL);
                            fAlbumObject = lObject.getParseObject(fALBUM_COLUMN);
                            fPhotoOwner = lObject.getParseUser(fFROMUSER);

                            fPhotoTitle = lObject.getString(fNAME);
                            final String lPhotoOwner = fPhotoOwner.getString(fNAME);

                            fSharedPhotoTitle.setText(fPhotoTitle);
                            if(fPhotoParseFile != null){
                                loadImage();
                            }
                            fAddedBy.setText("Added By:" + lPhotoOwner);
                            fAddedTo.setText("Added To:"+fAlbumObject.getString(fNAME));

                        }
                    }
                });
            }
        }).run();
    }

    public void deleteSharedOnClick(View aView){
        ParseObject.createWithoutData("Notifications",fPhotoId).deleteEventually();

        Intent lIntent = new Intent();
        lIntent.putExtra("photoPosition", fPosition);
        setResult(RESULT_OK, lIntent);

        makeToast("Photo Edited Succesffully");
        finish();
    }

    public void saveSharedOnClick(View aView){
        ParseObject lInsertPhoto = new ParseObject("Photos");
        lInsertPhoto.put(fALBUM_COLUMN,fAlbumObject);
        lInsertPhoto.put(fTHUMBNAIL, fPhotoParseFile);
        lInsertPhoto.put(fNAME,fPhotoTitle);
        lInsertPhoto.put(fOWNER,fPhotoOwner);
        lInsertPhoto.saveInBackground();

        deleteSharedOnClick(aView);

        if (fPhotoOwner.getBoolean("getNotification")){
            Map<String, String> lNotificationMap = new HashMap<String, String>();
            lNotificationMap.put("toUser", fPhotoOwner.getObjectId());
            lNotificationMap.put("fromUser", ParseUser.getCurrentUser().getString("name"));
            lNotificationMap.put("message"," accepted your photo.");
            lNotificationMap.put("type","Photo Accept");
            lNotificationMap.put("albumObjectId",fAlbumObject.getObjectId());

            ParseCloud.callFunctionInBackground("notifyPushForPhotoAccept", lNotificationMap, new FunctionCallback<Object>() {
                @Override
                public void done(Object object, ParseException e) {
                    if (e == null){

                    }else e.printStackTrace();
                }
            });
        }
    }

    public void cancelSharedOnClick(View aView){
        finish();
    }

    public void sharedPhotoOnClick(View aView){
        Intent lPictureIntent = new Intent(Intent.ACTION_PICK);
        lPictureIntent.setType("image/");
        startActivityForResult(lPictureIntent, fSELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case fSELECT_PICTURE:
                    Uri lSelectedImgUri;
                    lSelectedImgUri = data.getData();
                    try {
                        InputStream lStream =   getContentResolver().openInputStream(lSelectedImgUri);
                        byte[] imageBytes = getBytes(lStream);

                        fPhotoParseFile = new ParseFile("default.png", imageBytes);
                        fPhotoParseFile.saveInBackground();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    loadImage();

            }
        }
    }

    private byte[] getBytes(InputStream lStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = lStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void loadImage(){
        fSharedPhotoThumbnail.setParseFile(fPhotoParseFile);
        fSharedPhotoThumbnail.loadInBackground();
        fSharedPhotoThumbnail.setScaleType(ParseImageView.ScaleType.FIT_XY);
    }

    private void makeToast(String s) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
