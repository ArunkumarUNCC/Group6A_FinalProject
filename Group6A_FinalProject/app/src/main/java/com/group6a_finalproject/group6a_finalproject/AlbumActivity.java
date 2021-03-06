package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
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

    static RecyclerView fPhotoRecycler;
    RecyclerAdapter fAdapter;
    public static ViewPagerAdapter fPagerAdapter;
    LinearLayoutManager fRecyclerLayout;
    static Button fAddPhotoButton;
    static TextView fAlbumNameText;
    MenuItem fShareMenu,fEditMenu,fSharedMembers;
//    public static ParseImageView fsliderImages;
    static ViewPager fPager;

    static String fAlbumName;
    boolean fIsSharedUser;
    static boolean fIsSliderOn = false;
    ParseUser fAlbumOwner;

    public static ArrayList<Photo> fAlbumPhotos;

    static ParseUser fCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        fAlbumPhotos = new ArrayList<Photo>();
        fCurrentUser = ParseUser.getCurrentUser();

        getItems();
        checkPrivacy();
        getAlbumOwner();

        new GetPhotosAsync(this,2).execute(fAlbumName);
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void getItems (){
        fAlbumName = getIntent().getExtras().getString("ALBUM_TITLE");
        fPhotoRecycler = (RecyclerView) findViewById(R.id.RecyclerViewPhoto);
        fAddPhotoButton = (Button) findViewById(R.id.buttonAddPhoto);
        fAlbumNameText = (TextView) findViewById(R.id.textViewPhotoAlbumName);
        fAlbumNameText.setText(fAlbumName);
//        fsliderImages = (ParseImageView) findViewById(R.id.imageViewSlider);
//        fsliderImages.setVisibility(View.INVISIBLE);
        fPager = (ViewPager) findViewById(R.id.viewPager);
        fPager.setVisibility(View.INVISIBLE);


        fAddPhotoButton.setVisibility(View.INVISIBLE);

    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void toActivity(String aIntent,String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra(fALBUM_NAME_EXTRA, aExtra);
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

    //To start User Directory for selecting a user
    public void toActivity(String aIntent, int aExtra1, boolean aExtra2,boolean aExtra3){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("fromCompose", aExtra1);
        lIntent.putExtra("fromShared",aExtra2);
        lIntent.putExtra("albumName",fAlbumName);
        lIntent.putExtra("showShared", aExtra3);
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
                                    fPagerAdapter.notifyDataSetChanged();
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
                        fAlbumNameText.setText(toPutIntoList.getAlbumName());
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

        fPagerAdapter = new ViewPagerAdapter(this,fAlbumPhotos);
        fPhotoRecycler.setAdapter(fAdapter);
        fPager.setAdapter(fPagerAdapter);
    }

    public static void checkPrivacy(){
        ParseQuery<ParseObject> lGetAlbumId = ParseQuery.getQuery("Album");
        lGetAlbumId.whereEqualTo("name", fAlbumName);
        lGetAlbumId.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ParseObject lCorrectAlbum = objects.get(0);

                    ParseQuery<ParseObject> lGetShared = ParseQuery.getQuery("AlbumShare");
                    lGetShared.include("album");
                    lGetShared.include("sharedWith");
                    lGetShared.whereEqualTo("album", lCorrectAlbum);
                    lGetShared.whereEqualTo("sharedWith", fCurrentUser);
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
                                    lGetMyPrivate.whereEqualTo("owner", fCurrentUser);
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
        if(fIsSliderOn){
            swapSlider();
        }else {
            setResult(RESULT_OK);
            super.onBackPressed();
        }
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
                        if (!fCurrentUser.fetchIfNeeded().getUsername().equals(fAlbumOwner.fetchIfNeeded().getUsername())) {
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

    public static void manageSlider(int position){
        swapSlider();
        fPager.setCurrentItem(position);
//        fsliderImages.setParseFile(fAlbumPhotos.get(position).getPhotoBitmap());
//        fsliderImages.loadInBackground();
//        fsliderImages.setScaleType(ParseImageView.ScaleType.FIT_XY);
    }

    public static void swapSlider(){
        fIsSliderOn = !fIsSliderOn;
        int lSliderVisibility = fIsSliderOn == true ? View.VISIBLE : View.INVISIBLE;
        int lGridVisibility = fIsSliderOn == false ? View.VISIBLE : View.INVISIBLE;

//        fsliderImages.setVisibility(lSliderVisibility);
        fPager.setVisibility(lSliderVisibility);
        fPhotoRecycler.setVisibility(lGridVisibility);
        fAlbumNameText.setVisibility(lGridVisibility);
//        fAddPhotoButton.setVisibility(lGridVisibility);
        checkPrivacy();
    }

    public void logoutOnClick (MenuItem aItem){


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.remove("user");
        installation.saveInBackground();

        fCurrentUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    logout();
                    finish();
                }
            }
        });
    }

    public void logout(){
        if(LoginManager.getInstance() != null)
            LoginManager.getInstance().logOut();
        Intent lIntent = new Intent(AlbumActivity.this, MainActivity.class);
        startActivity(lIntent);
    }
}
