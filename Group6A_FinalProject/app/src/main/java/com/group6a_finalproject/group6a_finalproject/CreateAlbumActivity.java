package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class CreateAlbumActivity extends AppCompatActivity {

    final String fGOTO_ALBUM_VIEW = "android.intent.action.ALBUM_VIEW";

    EditText fAlbumName;
    ImageView fAlbumThumb;
    Switch fPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

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
        //TODO give ability to add thumbnail
    }

    public void createAlbumOnClick (View aView){
        //TODO save info to Parse.com
        makeToast("Album Successfully created!");
        toActivity(fGOTO_ALBUM_VIEW, fAlbumName.getText().toString());
    }

    private void cancelOnClick (View aView){
        finish();
    }
}
