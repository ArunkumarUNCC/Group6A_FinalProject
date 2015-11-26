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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class AddPhoto extends AppCompatActivity {

    final int fSELECT_PICTURE = 1;
    final String fNEW_PHOTONAME_EXTRA = "NEW_PHOTO";

    String fAlbumName;

    ImageView fPhotoThumb;
    EditText fPhotoName;

    Bitmap fImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);

        fImageBitmap = null;

        fAlbumName = getIntent().getExtras().getString("ALBUM_NAME");

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
        if(fImageBitmap==null || lPhotoName.isEmpty()) {
            if(fImageBitmap==null)
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

                        final ParseObject lNewPhoto = new ParseObject("Photos");
                        lNewPhoto.put("name",lPhotoName);
                        lNewPhoto.put("album",objects.get(0));
                        lNewPhoto.put("uploadedBy", ParseUser.getCurrentUser());

                        ByteArrayOutputStream lStream = new ByteArrayOutputStream();
                        fImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, lStream);
                        byte[] lImageToUpload = lStream.toByteArray();

                        final ParseFile lImageFile = new ParseFile("default.png", lImageToUpload);
                        lImageFile.saveInBackground();
                        lNewPhoto.put("thumbnail", lImageFile);
                        lNewPhoto.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null){
                                    makeToast("New photo created");
                                    sendActivityResult(lNewPhoto.getObjectId());
                                    finish();
                                }
                            }
                        });

                    }else e.printStackTrace();
                }
            });

        }
    }

    public void getItems (){
        fPhotoName = (EditText) findViewById(R.id.editTextAddPictureName);
        fPhotoThumb = (ImageView) findViewById(R.id.imageViewAddPhotoThumb);
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
                        fImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),lSelectedImgUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fPhotoThumb.setImageURI(lSelectedImgUri);


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
}
