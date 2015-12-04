package com.group6a_finalproject.group6a_finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity implements GetAlbumNotificationsAsync.IGetAlbumNotifications,
        GetPhotoNotificationsAsync.IGetPhotoNotifications {

    RecyclerAdapter fAdapter;
    LinearLayoutManager fAlbumNotificationManager,fPhotoNotificationManager;
    RecyclerView fAlbumView,fPhotoView;

    ArrayList<Album> fAlbums;
    ArrayList<String> fPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        getItems();

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

        fAdapter = new RecyclerAdapter(Notifications.this,4);
        fAdapter.setAlbumsList(fAlbums);
        fAlbumView.setAdapter(fAdapter);
    }

    @Override
    public void putPhotoNotifications(ArrayList<String> aPhotos) {
        if (aPhotos != null) {
            fPhotoNotificationManager = new LinearLayoutManager(this);
            fPhotoView.setLayoutManager(fPhotoNotificationManager);
            fPhotos = aPhotos;

            fAdapter = new RecyclerAdapter(Notifications.this,5,aPhotos);
            fPhotoView.setAdapter(fAdapter);
        }
    }
}
