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
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    final String fGOTO_MAIN_PROFILE = "android.intent.action.MAIN_PROFILE";

    ImageView fProfilePic;
    EditText fName, fEmail;
    Switch fGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //TODO Load current user information
        getItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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
        fName = (EditText) findViewById(R.id.editTextEditProfileName);
        fEmail = (EditText) findViewById(R.id.editTextEditProfileEmail);
        fGender = (Switch) findViewById(R.id.switchEdiptProfileGender);

        fProfilePic = (ImageView) findViewById(R.id.imageViewEditProfilePicture);
    }

    public void toActivity(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("Some Extra", aExtra);
        startActivity(lIntent);
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void saveProfileOnClick (View aView){
        makeToast("Profile updated!");
        //TODO Pass correct extra
        toActivity(fGOTO_MAIN_PROFILE, "Some extra");
    }

    public void editAvatarOnClick (View aView){
        //TODO give ability to add different profile picture
    }
}
