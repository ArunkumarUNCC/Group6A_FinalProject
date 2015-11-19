package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class AlbumActivity extends AppCompatActivity {

    final String fGOTO_ADD_PHOTO = "android.intent.action.ADD_PHOTO";
    RecyclerView fPhotoRecycler;
    Button fAddPhotoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        //TODO load photos (if any) -- won't be any for new album but check anways
        getItems();
        if(false){//TODO add flag if have permission
            fAddPhotoButton.setVisibility(View.INVISIBLE);
        }
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
}
