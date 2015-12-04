package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.parse.CountCallback;
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
    final String fGOTO_CRTEATE_ALBUM = "android.intent.action.CREATE_ALBUM";
    final String fGOTO_USER_DIRECTORY ="android.intent.action.USER_DIRECTORY";
    final  String fALBUM_NAME_EXTRA = "ALBUM_NAME";
    final  String fALBUM_USER = "IS_SHARED_USER";
    final int fNEW_PHOTO_REQCODE = 1002;
    final int fEDIT_ALBUM_REQCODE = 1003;

    RecyclerView fPhotoRecycler;
    RecyclerAdapter fAdapter;
    LinearLayoutManager fRecyclerLayout;
    Button fAddPhotoButton;
    TextView fAlbumNameText;
    MenuItem fShareMenu,fEditMenu,fSharedMembers;

    static String fAlbumName;
    boolean fIsSharedUser;
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

        setActionBarTitle(fAlbumName);

        new GetPhotosAsync(this,2).execute(fAlbumName);
    }

    private void setActionBarTitle(String aAlbumName) {
        //Display Album name
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(aAlbumName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        fShareMenu = menu.findItem(R.id.shareAlbum);
        fEditMenu = menu.findItem(R.id.editAlbum);
        fSharedMembers = menu.findItem(R.id.showSharedMembers);

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

        fAddPhotoButton.setVisibility(View.INVISIBLE);

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

    public void toActivityForResult(String aIntent,String aExtra,boolean aIsSharedUser){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra(fALBUM_NAME_EXTRA,aExtra);
        lIntent.putExtra(fALBUM_USER,aIsSharedUser);
        startActivityForResult(lIntent, fNEW_PHOTO_REQCODE);
    }

    public void toActivityForResult(String aIntent,int aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("taskToPerform", aExtra);
        lIntent.putExtra("albumName",fAlbumName);
        startActivityForResult(lIntent, fEDIT_ALBUM_REQCODE);
    }

    //To start User Directory
    public void toActivity(String aIntent, int aExtra1, boolean aExtra2,boolean aExtra3){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("fromCompose", aExtra1);
        lIntent.putExtra("fromShared",aExtra2);
        lIntent.putExtra("albumName",fAlbumName);
        lIntent.putExtra("showShared",aExtra3);
        startActivity(lIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case fNEW_PHOTO_REQCODE:
                    if (!fIsSharedUser) {
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
                                    lNewPhoto.setPhotoBitmap(lImageFromParse);

                                    fAlbumPhotos.add(lNewPhoto);
                                    fAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    break;

                case fEDIT_ALBUM_REQCODE:
                    SharedPreferences sp = getSharedPreferences("AlbumDetails",MODE_PRIVATE);
                    SharedPreferenceSetup lTempAlbum = new SharedPreferenceSetup(sp);
                    if (lTempAlbum.checkKey("oldAlbumName")){
                        Album toPutIntoList = lTempAlbum.getAlbumPreference("myAlbum");
                        setActionBarTitle(toPutIntoList.getAlbumName());

                    }
            }
        }
    }

    public void addPhotoOnClick (View aView){

            toActivityForResult(fGOTO_ADD_PHOTO, fAlbumName, fIsSharedUser);

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
        ParseQuery<ParseObject> lGetAlbumId = ParseQuery.getQuery("Album");
        lGetAlbumId.whereEqualTo("name",fAlbumName);
        lGetAlbumId.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject lCorrectAlbum = objects.get(0);

                    ParseQuery<ParseObject> lGetShared = ParseQuery.getQuery("AlbumShare");
                    lGetShared.include("album");
                    lGetShared.include("sharedWith");
                    lGetShared.whereEqualTo("album", lCorrectAlbum);
                    lGetShared.whereEqualTo("sharedWith", ParseUser.getCurrentUser());
                    lGetShared.countInBackground(new CountCallback() {
                        @Override
                        public void done(int count, ParseException e) {
                            if (e == null) {
                                if (count > 0) {
                                    fAddPhotoButton.setVisibility(View.VISIBLE);
                                    return;
                                } else {
                                    ParseQuery<ParseObject> lGetMyPrivate = ParseQuery.getQuery("Album");
                                    lGetMyPrivate.include("owner");
                                    lGetMyPrivate.whereEqualTo("owner", ParseUser.getCurrentUser());
                                    lGetMyPrivate.whereEqualTo("name", fAlbumName);
                                    lGetMyPrivate.countInBackground(new CountCallback() {
                                        @Override
                                        public void done(int count, ParseException e) {
                                            if (e == null) {
                                                if (count > 0) {
                                                    fAddPhotoButton.setVisibility(View.VISIBLE);
                                                    return;
                                                } else {
                                                    return;
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });


                }
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

    public void editAlbumOnClick(MenuItem aItem){
        toActivityForResult(fGOTO_CRTEATE_ALBUM, 2);
    }

    public void shareAlbumOnClick(MenuItem aItem){
        toActivity(fGOTO_INVITE, fAlbumName);
    }

    public void sharedMembersOnClick(MenuItem aItem){
        toActivity(fGOTO_USER_DIRECTORY, 1, true, true);
    }

    private void getAlbumOwner() {
        ParseQuery<ParseObject> lGetOwner = ParseQuery.getQuery("Album");
        lGetOwner.whereEqualTo("name", fAlbumName);
        lGetOwner.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    fAlbumOwner = objects.get(0).getParseUser("owner");

                    try {
                        if (!ParseUser.getCurrentUser().fetchIfNeeded().getUsername().equals(fAlbumOwner.fetchIfNeeded().getUsername())) {
                            deleteMenuItems();
                            fIsSharedUser = true;
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                } else e.printStackTrace();
            }
        });
    }

    public void deleteMenuItems(){
        fShareMenu.setVisible(false);
        fEditMenu.setVisible(false);
        fSharedMembers.setVisible(false);
    }
}
