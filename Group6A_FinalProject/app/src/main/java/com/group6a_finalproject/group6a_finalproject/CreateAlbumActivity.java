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
import java.util.List;

public class CreateAlbumActivity extends AppCompatActivity {

    final String fGOTO_ALBUM_VIEW = "android.intent.action.ALBUM_VIEW";
    final int fSELECT_PICTURE = 1;

    EditText fAlbumName;
    ImageView fAlbumThumb;
    Switch fPrivate;

    Bitmap fImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        fImageBitmap = null;
        getItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_album, menu);
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

    public void getItems (){
        fAlbumName = (EditText) findViewById(R.id.editTextCreateAlbumName);
        fAlbumThumb = (ImageView) findViewById(R.id.imageViewCreateAlbumThumb);
        fPrivate = (Switch) findViewById(R.id.switchAlbumPrivacy);
    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void toActivity(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("ALBUM_TITLE", aExtra);
        startActivity(lIntent);
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void addAlbumThumbOnClick (View aView){
        Intent lPictureIntent = new Intent(Intent.ACTION_PICK);
        lPictureIntent.setType("image/");
        startActivityForResult(lPictureIntent, fSELECT_PICTURE);
    }

    public void createAlbumOnClick (View aView){
        final String lAlbumName = fAlbumName.getText().toString();
        final String lAlbumPrivacy = fPrivate.isChecked()?"Public":"Private";
        if(!lAlbumName.isEmpty()) {

            ParseQuery<ParseObject> checkAlbumName = ParseQuery.getQuery("Album");
            checkAlbumName.whereEqualTo("name", lAlbumName);
            checkAlbumName.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.isEmpty()) {
                        ParseUser lCurrentUser = ParseUser.getCurrentUser();
                        ParseObject lSaveAlbum = new ParseObject("Album");
                        lSaveAlbum.put("name", lAlbumName);
                        lSaveAlbum.put("privacy", lAlbumPrivacy);
                        lSaveAlbum.put("owner", lCurrentUser);

                        if (fImageBitmap != null) {
                            ByteArrayOutputStream lStream = new ByteArrayOutputStream();
                            fImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, lStream);
                            byte[] lImageToUpload = lStream.toByteArray();

                            final ParseFile lImageFile = new ParseFile("thumbnail.png", lImageToUpload);
                            lImageFile.saveInBackground();
                            lSaveAlbum.put("thumbnail", lImageFile);
                        }

                        lSaveAlbum.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    makeToast("Album Successfully created!");
                                    toActivity(fGOTO_ALBUM_VIEW, fAlbumName.getText().toString());
                                } else {
                                    makeToast("Upload Error");
                                }
                            }
                        });
                    }
                    else fAlbumName.setError("Album name already exists!");
                }
            });


        }else fAlbumName.setError("Empty Album name!");

    }

    public void cancelOnClick (View aView){
        finish();
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
                    fAlbumThumb.setImageURI(lSelectedImgUri);


            }
        }
    }
}
