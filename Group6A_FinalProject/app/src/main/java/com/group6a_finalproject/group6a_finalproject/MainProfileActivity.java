package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MainProfileActivity extends AppCompatActivity {

    final String fGOTO_EDIT_PROFILE = "android.intent.action.EDIT_PROFILE";
    final String fGOTO_CRTEATE_ALBUM = "android.intent.action.CREATE_ALBUM";

    ImageView fProfilePic;
    TextView fName, fEmail, fGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);

        getItems();

        //TODO Load profile information
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_profile, menu);
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
        fName = (TextView) findViewById(R.id.textViewProfileName);
        fEmail = (TextView) findViewById(R.id.textViewProfileEmail);
        fGender = (TextView) findViewById(R.id.textViewProfileGender);

        fProfilePic = (ImageView) findViewById(R.id.imageViewProfilePicture);
    }

    public void toActivity(){
        Intent lIntent = new Intent(MainProfileActivity.this, MainActivity.class);
        startActivity(lIntent);
    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void toActivity(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("Some Extra", aExtra);
        startActivity(lIntent);
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void editProfileOnClick (View aView){
        //TODO send current user information
        toActivity(fGOTO_EDIT_PROFILE, "Some Extra");
    }

    public void createAlbumOnClick (MenuItem aItem){
        toActivity(fGOTO_CRTEATE_ALBUM);
    }

    public void viewAlbumOnClick (MenuItem aItem){

    }

    public void viewUserDirectoryOnClick (MenuItem aItem){

    }

    public void logoutOnClick (MenuItem aItem){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    toActivity();
                    finish();
                }
            }
        });
    }
}
