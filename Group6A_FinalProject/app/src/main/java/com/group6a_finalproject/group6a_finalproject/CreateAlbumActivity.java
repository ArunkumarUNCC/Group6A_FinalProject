package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
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
    Button fSaveAlbum;

    Bitmap fImageBitmap;

    int fWhichTask;
    String fAlbumString,fOldAlbumName;

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
        fSaveAlbum = (Button) findViewById(R.id.buttonCreateAlbum);

        fWhichTask = getIntent().getExtras().getInt("taskToPerform");
        fOldAlbumName = getIntent().getExtras().getString("albumName");
        switch (fWhichTask){
            case 2:
                setItems();
                break;
            default:
                break;
        }
    }

    public void setItems(){
        fSaveAlbum.setText("Save");
        fAlbumString = getIntent().getStringExtra("albumName");

        fAlbumName.setText(fAlbumString);

        ParseQuery<ParseObject> lGetAlbum = ParseQuery.getQuery("Album");
        lGetAlbum.whereEqualTo("name",fAlbumString);
        lGetAlbum.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject lObject = objects.get(0);
                    String lIsPrivate = lObject.getString("privacy");
                    if (lIsPrivate.equals("Public"))
                        fPrivate.setChecked(true);

                    ParseFile lPhotoImage = lObject.getParseFile("thumbnail");
                    if (lPhotoImage != null) {
                        lPhotoImage.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                fAlbumThumb.setImageBitmap(bitmap);
                                fImageBitmap = bitmap;

                            }
                        });
                    } else {

                        fAlbumThumb.setImageResource(R.drawable.no_mage);
                    }
                }
            }
        });
    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void toActivity(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("ALBUM_TITLE", aExtra);
        lIntent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
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
        final String lAlbumName;


            if(fWhichTask == 2){
                lAlbumName = fOldAlbumName;
            } else{lAlbumName = fAlbumName.getText().toString();}


        if(!lAlbumName.isEmpty()) {
            ParseQuery<ParseObject> checkAlbumName = ParseQuery.getQuery("Album");
            checkAlbumName.whereEqualTo("name", lAlbumName);
            checkAlbumName.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (fWhichTask == 2) {
                            saveAlbum(objects.get(0));
                        } else {
                            if (objects.isEmpty()) {
                                ParseObject lSaveAlbum = new ParseObject("Album");
                                saveAlbum(lSaveAlbum);
                            } else fAlbumName.setError("Album name already exists!");

                        }
                    }

                }
            });
        }

        else fAlbumName.setError("Empty Album name!");

    }

    public void cancelOnClick (View aView){
        finish();
    }

    public void saveAlbumOnClick(View aView){

    }

    public void saveAlbum(ParseObject aToSave){

        final Album lTempAlbum = new Album();
        SharedPreferences sp = getSharedPreferences("AlbumDetails",MODE_PRIVATE);
        final SharedPreferenceSetup lSetPreference = new SharedPreferenceSetup(sp);

        final String lAlbumPrivacy = fPrivate.isChecked()?"Public":"Private";
        final String lAlbumName = fAlbumName.getText().toString();

        ParseUser lCurrentUser = ParseUser.getCurrentUser();

        aToSave.put("name", lAlbumName);
        aToSave.put("privacy", lAlbumPrivacy);
        aToSave.put("owner", lCurrentUser);

        lTempAlbum.setAlbumName(lAlbumName);
        lTempAlbum.setPrivacy(lAlbumPrivacy);
        lTempAlbum.setOwnerName(lCurrentUser.getString("name"));

        if (fImageBitmap != null) {
            ByteArrayOutputStream lStream = new ByteArrayOutputStream();
            fImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, lStream);
            byte[] lImageToUpload = lStream.toByteArray();

            final ParseFile lImageFile = new ParseFile("thumbnail.png", lImageToUpload);
            lImageFile.saveInBackground();
            aToSave.put("thumbnail", lImageFile);
            lTempAlbum.setAlbumImage(fImageBitmap);
        }

        lSetPreference.putAlbumPreferences("myAlbum", lTempAlbum);

        if (fWhichTask == 2) {
            lSetPreference.putAlbumPreferences("oldAlbumName", fOldAlbumName);
        }

        aToSave.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (fWhichTask == 2) {
                        makeToast("Album edited successfully!");
                        setResult(RESULT_OK);
                    }
                    else{
                        makeToast("Album Successfully created!");
                        toActivity(fGOTO_ALBUM_VIEW, fAlbumName.getText().toString());
                    }
                    finish();
                } else {
                    makeToast("Upload Error");
                }
            }
        });

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

                    break;



            }
        }
    }
}
