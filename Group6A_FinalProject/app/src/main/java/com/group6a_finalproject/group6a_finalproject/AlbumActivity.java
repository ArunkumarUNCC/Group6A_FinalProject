package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    final String fGOTO_ADD_PHOTO = "android.intent.action.ADD_PHOTO";
    RecyclerView fPhotoRecycler;
    LinearLayoutManager fRecyclerLayout;
    Button fAddPhotoButton;

    String fAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        fAlbumName = getIntent().getExtras().getString("ALBUM_TITLE");

        //Display Album name
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(fAlbumName);

        //TODO load photos (if any) -- won't be any for new album but check anways
        getItems();

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
        fPhotoRecycler = (RecyclerView) findViewById(R.id.RecyclerViewPhoto);
        fAddPhotoButton = (Button) findViewById(R.id.buttonAddPhoto);
    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void addPhotoOnClick (View aView){
        toActivity(fGOTO_ADD_PHOTO);
    }

    public void setRecyclerView(){
        fRecyclerLayout = new GridLayoutManager(this,2);
        fPhotoRecycler.setLayoutManager(fRecyclerLayout);
    }

    public void checkPrivacy(){
        ParseQuery<ParseObject> checkPrivacy = ParseQuery.getQuery("Album");
        checkPrivacy.whereEqualTo("name",fAlbumName);
        checkPrivacy.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(objects.get(0).getString("privacy").equals("Private"))
                    fAddPhotoButton.setVisibility(View.INVISIBLE);
            }
        });
    }
}
