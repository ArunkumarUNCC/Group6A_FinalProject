package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class AlbumsList extends AppCompatActivity implements GetPhotosAsync.IGetPhotos{

    RecyclerView fAlbumsListRecycler;
    RecyclerAdapter fAlbumsAdapter;
    LinearLayoutManager fAlbumsLayoutManager;

    ArrayList<Photo> fAlbumsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_list);

        fAlbumsListRecycler = (RecyclerView) findViewById(R.id.recyclerViewAlbums);

        new GetPhotosAsync(this,1).execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albums_list, menu);
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

    private void setRecyclerView(ArrayList<Photo> albums) {
        fAlbumsLayoutManager=  new LinearLayoutManager(this);
        fAlbumsListRecycler.setLayoutManager(fAlbumsLayoutManager);

        if (albums!=null)
            fAlbumsList = albums;

        fAlbumsAdapter = new RecyclerAdapter(fAlbumsList,AlbumsList.this,1);
        fAlbumsListRecycler.setAdapter(fAlbumsAdapter);
    }

    @Override
    public void putPhotos(ArrayList<Photo> photos) {
//        Log.d("Check albums", String.valueOf(photos.size()));
        setRecyclerView(photos);
    }
}
