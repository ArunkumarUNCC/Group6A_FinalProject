package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPhoto extends AppCompatActivity {

    final int fSELECT_PICTURE = 1;
    final String fNEW_PHOTONAME_EXTRA = "NEW_PHOTO";

    String fAlbumName;
    boolean fIsSharedUser;

    ParseImageView fPhotoThumb;
    EditText fPhotoName;

    ParseFile fImageFile;
    ParseUser fCurrentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        fImageFile = null;

        getItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_photo, menu);
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

    public void savePictureOnClick (View aView){
        final String lPhotoName = fPhotoName.getText().toString();
        if(fImageFile==null || lPhotoName.isEmpty()) {
            if(fImageFile==null)
                makeToast("Choose photo");
            if (lPhotoName.isEmpty())
                fPhotoName.setError("Photo name is empty");
        }
        else{
            ParseQuery<ParseObject> lGetAlbumObj = ParseQuery.getQuery("Album");
            lGetAlbumObj.whereEqualTo("name",fAlbumName);
            lGetAlbumObj.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        ParseObject lValidObject = objects.get(0);
                        if(fIsSharedUser){
                            putIntoNotifications(lValidObject,lPhotoName);
                        }
                        else {
                            putIntoPhotos(lValidObject,lPhotoName);
                        }
                    }else e.printStackTrace();
                }
            });

        }
    }

    public void getItems (){
        Bundle lIntentBundle = getIntent().getExtras();
        fAlbumName = lIntentBundle.getString("ALBUM_NAME");
        fIsSharedUser = lIntentBundle.getBoolean("IS_SHARED_USER");

        fPhotoName = (EditText) findViewById(R.id.editTextAddPictureName);
        fPhotoThumb = (ParseImageView) findViewById(R.id.imageViewAddPhotoThumb);
    }

    public void addPhotoCancelOnClick (View aView){
        finish();
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void insertPhotoOnClick(View aView){
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

                        fImageFile = new ParseFile("default.png", imageBytes);
                        fImageFile.saveInBackground();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fPhotoThumb.setImageURI(lSelectedImgUri);
                    fPhotoThumb.setScaleType(ParseImageView.ScaleType.FIT_XY);

            }
        }
    }

    public void sendActivityResult(String aPhotoName){
        //ParseProxyObject lParseObject = new ParseProxyObject(aPhotoName);

        Intent lIntent = new Intent();
        lIntent.putExtra(fNEW_PHOTONAME_EXTRA, aPhotoName);
        setResult(RESULT_OK,lIntent);
        finish();

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

    private void putIntoNotifications(ParseObject object,String aPhotoName){
        ParseUser lUserObject = null;
        String lOwnerName = null;
        try {
            lUserObject = object.getParseUser("owner").fetchIfNeeded();
            lOwnerName = lUserObject.getString("name");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final ParseObject lNewPhoto = new ParseObject("Notifications");
        lNewPhoto.put("album", object);
        lNewPhoto.put("name", aPhotoName);
        lNewPhoto.put("fromUser", fCurrentUser);
        lNewPhoto.put("toUser",lUserObject);
        lNewPhoto.put("thumbnail", fImageFile);
        final ParseUser finalLUserObject = lUserObject;
        lNewPhoto.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    if(finalLUserObject.getBoolean("getNotification")){
                        Map<String, String> lNotificationMap = new HashMap<String, String>();
                        lNotificationMap.put("toUser", finalLUserObject.getObjectId());
                        lNotificationMap.put("fromUser",fCurrentUser.getString("name"));
                        lNotificationMap.put("message"," created a new photo in your album.");
                        lNotificationMap.put("photoId",lNewPhoto.getObjectId());
                        lNotificationMap.put("type","New Photo");
                        ParseCloud.callFunctionInBackground("notifyPushForPhoto", lNotificationMap, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if (e == null) {

                                } else e.printStackTrace();
                            }
                        });
                    }
                    makeToast("New photo sent to the owner for verification");
                    sendActivityResult(lNewPhoto.getObjectId());
                    finish();
                }
            }
        });
    }

    private  void putIntoPhotos(ParseObject object,String aPhotoName){
        final ParseObject lNewPhoto = new ParseObject("Photos");
        lNewPhoto.put("name", aPhotoName);
        lNewPhoto.put("album", object);
        lNewPhoto.put("uploadedBy", fCurrentUser);
        lNewPhoto.put("thumbnail", fImageFile);
        lNewPhoto.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    makeToast("New photo created");
                    sendActivityResult(lNewPhoto.getObjectId());
                    finish();
                }
            }
        });
    }
}
