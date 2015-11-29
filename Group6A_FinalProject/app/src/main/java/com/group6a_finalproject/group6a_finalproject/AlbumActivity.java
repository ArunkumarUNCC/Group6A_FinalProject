package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements GetPhotosAsync.IGetPhotos{

    final String fGOTO_ADD_PHOTO = "android.intent.action.ADD_PHOTO";
    final String fGOTO_INVITE = "android.intent.action.INVITE_USERS";
    final  String fALBUM_NAME_EXTRA = "ALBUM_NAME";
    final int fNEW_PHOTO_REQCODE = 1002;

    RecyclerView fPhotoRecycler;
    RecyclerAdapter fAdapter;
    LinearLayoutManager fRecyclerLayout;
    Button fAddPhotoButton;
    TextView fAlbumNameText;

    static String fAlbumName;
    ParseUser fAlbumOwner;

    ArrayList<Photo> fAlbumPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        fAlbumPhotos = new ArrayList<Photo>();


        getItems();
        checkPrivacy();
        getAlbumOwner();

        //Display Album name
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(fAlbumName);

        new GetPhotosAsync(this,2).execute(fAlbumName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
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
        fAlbumName = getIntent().getExtras().getString("ALBUM_TITLE");
        fPhotoRecycler = (RecyclerView) findViewById(R.id.RecyclerViewPhoto);
        fAddPhotoButton = (Button) findViewById(R.id.buttonAddPhoto);
        fAlbumNameText = (TextView) findViewById(R.id.textViewPhotoAlbumName);
        fAlbumNameText.setText(fAlbumName);
    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void toActivity(String aIntent,String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra(fALBUM_NAME_EXTRA,aExtra);
        startActivity(lIntent);
    }

    public void toActivityForResult(String aIntent,String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra(fALBUM_NAME_EXTRA,aExtra);
        startActivityForResult(lIntent, fNEW_PHOTO_REQCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case fNEW_PHOTO_REQCODE:
                    String lPhotoId = data.getExtras().getString("NEW_PHOTO");
                    ParseQuery<ParseObject> lGetPhoto = ParseQuery.getQuery("Photos");
                    lGetPhoto.whereEqualTo("objectId", lPhotoId);
                    lGetPhoto.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                final Photo lNewPhoto = new Photo();
                                lNewPhoto.setPhotoName(objects.get(0).getString("name"));

                                ParseFile lImageFromParse = objects.get(0).getParseFile("thumbnail");
                                lImageFromParse.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        lNewPhoto.setPhotoBitmap(bitmap);

                                        fAlbumPhotos.add(lNewPhoto);
                                        fAdapter.notifyDataSetChanged();
                                    }
                                });


                            }
                        }
                    });
            }
        }
    }

    public void addPhotoOnClick (View aView){
        toActivityForResult(fGOTO_ADD_PHOTO, fAlbumName);
    }

    public void setRecyclerView(ArrayList<Photo> photos){
        fRecyclerLayout = new GridLayoutManager(AlbumActivity.this,2);
        fPhotoRecycler.setLayoutManager(fRecyclerLayout);

        if (photos!=null)
            fAlbumPhotos = photos;

        fAdapter = new RecyclerAdapter(fAlbumPhotos,AlbumActivity.this,2,fAlbumOwner,fAlbumName);
        fPhotoRecycler.setAdapter(fAdapter);
    }

    public void checkPrivacy(){
        ParseQuery<ParseObject> lGetAllPublic = ParseQuery.getQuery("Album");
        lGetAllPublic.whereEqualTo("name",fAlbumName);
        lGetAllPublic.whereEqualTo("privacy", "Public");

        ParseQuery<ParseObject> lGetMyPrivate = ParseQuery.getQuery("Album");
        lGetMyPrivate.whereEqualTo("owner", ParseUser.getCurrentUser());
        lGetMyPrivate.whereEqualTo("name",fAlbumName);
        lGetMyPrivate.whereEqualTo("privacy","Private");

        ArrayList<ParseQuery<ParseObject>> lQueries = new ArrayList<ParseQuery<ParseObject>>();
        lQueries.add(lGetAllPublic);
        lQueries.add(lGetMyPrivate);

        ParseQuery<ParseObject> lCheckPrivacy = ParseQuery.or(lQueries);
        lCheckPrivacy.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (objects.isEmpty())
                    fAddPhotoButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void putPhotos(ArrayList<Photo> photos) {
        setRecyclerView(photos);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    public void shareAlbumOnClick(MenuItem aItem){
        toActivity(fGOTO_INVITE, fAlbumName);
    }

    private void getAlbumOwner() {
        ParseQuery<ParseObject> lGetOwner = ParseQuery.getQuery("Album");
        lGetOwner.whereEqualTo("name",fAlbumName);
        lGetOwner.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                fAlbumOwner = objects.get(0).getParseUser("owner");
            }
        });
    }
}
