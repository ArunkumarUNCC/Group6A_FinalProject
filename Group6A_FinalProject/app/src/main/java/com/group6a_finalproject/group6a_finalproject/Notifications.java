package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity implements GetAlbumNotificationsAsync.IGetAlbumNotifications,
        GetPhotoNotificationsAsync.IGetPhotoNotifications {

    final String fGOTO_ALBUM_LIST = "android.intent.action.ALBUM_LIST";
    final String fGOTO_USER_DIRECTORY ="android.intent.action.USER_DIRECTORY";
    final String fGOTO_USER_INBOX = "android.intent.action.USER_INBOX";
    final String fGOTO_MY_PROFILE = "android.intent.action.MAIN_PROFILE";
    public static final int fEDIT_PHOTO = 1000;

    RecyclerAdapter fAlbumAdapter,fPhotoAdapter;
    LinearLayoutManager fAlbumNotificationManager,fPhotoNotificationManager;
    RecyclerView fAlbumView,fPhotoView;
    ParseUser fCurrentUser;

    ArrayList<Album> fAlbums;
    ArrayList<String> fPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        getItems();

        fCurrentUser = ParseUser.getCurrentUser();

        new GetAlbumNotificationsAsync(this).execute();
        new GetPhotoNotificationsAsync(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifications, menu);
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
        fAlbums = new ArrayList<>();
        fAlbumView = (RecyclerView) findViewById(R.id.recyclerViewAlbumNotifications);
        fPhotoView = (RecyclerView) findViewById(R.id.recyclerViewPhotoNotifications);
    }

    @Override
    public void putAlbumNotifications(ArrayList<Album> albums) {
        if (albums!=null){
            setAlbumRecyclerView(albums);
        }
    }

    public void setAlbumRecyclerView(ArrayList<Album> aAlbums){

        fAlbumNotificationManager = new LinearLayoutManager(this);
        fAlbumView.setLayoutManager(fAlbumNotificationManager);
        fAlbums = aAlbums;

        fAlbumAdapter = new RecyclerAdapter(Notifications.this,4);
        fAlbumAdapter.setAlbumsList(fAlbums);
        fAlbumView.setAdapter(fAlbumAdapter);
    }

    @Override
    public void putPhotoNotifications(ArrayList<String> aPhotos) {
        if (aPhotos != null) {
            fPhotoNotificationManager = new LinearLayoutManager(this);
            fPhotoView.setLayoutManager(fPhotoNotificationManager);
            fPhotos = aPhotos;

            fPhotoAdapter = new RecyclerAdapter(Notifications.this,5,aPhotos);
            fPhotoView.setAdapter(fPhotoAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null) {
            Bundle lIntentBundle = data.getExtras();

            switch (requestCode) {
                case fEDIT_PHOTO:
                    if (resultCode == RESULT_OK) {
                        int position = lIntentBundle.getInt("photoPosition");
                        fPhotos.remove(position);
                        fPhotoAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    public void logoutOnClick (MenuItem aItem){

    }

    public void viewAlbumsOnClick(MenuItem aItem){
        toActivity(fGOTO_ALBUM_LIST, 1);
    }

    public void viewProfileOnClick(MenuItem aItem){
        toActivity(fGOTO_MY_PROFILE);
    }

    public void viewInboxOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_INBOX);
    }

    public void viewUserDirectoryOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_DIRECTORY, 1, false);
    }

    //Starting activity
    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
        finish();
    }

    //To start User Directory
    public void toActivity(String aIntent, int aExtra1, boolean aExtra2){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("fromCompose", aExtra1);
        lIntent.putExtra("fromShared", aExtra2);
        startActivity(lIntent);
        finish();
    }

    //To start View Albums
    public void toActivity(String aIntent, int aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("album_flag", aExtra);
        lIntent.putExtra("current_user", fCurrentUser.getEmail());
        startActivity(lIntent);
        finish();
    }
}
