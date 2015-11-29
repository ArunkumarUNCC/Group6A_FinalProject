package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

public class MainProfileActivity extends AppCompatActivity {

    final String fGOTO_EDIT_PROFILE = "android.intent.action.EDIT_PROFILE";
    final String fGOTO_ALBUM_VIEW = "android.intent.action.ALBUM_VIEW";
    final String fGOTO_ALBUM_LIST = "android.intent.action.ALBUM_LIST";
    final String fGOTO_USER_DIRECTORY ="android.intent.action.USER_DIRECTORY";
    final String fGOTO_USER_INBOX = "android.intent.action.USER_INBOX";
    final int fEDIT_PROFILE_REQCODE = 1001;

    ImageView fProfilePic;
    TextView fName, fEmail, fGender,fPrivacy,fNotify;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_profile);

        //setValues();
        getItems();
        setItems();
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
        fNotify = (TextView) findViewById(R.id.textViewProfileNotification);
        fPrivacy = (TextView) findViewById(R.id.textViewProfilePrivacy);

        fProfilePic = (ImageView) findViewById(R.id.imageViewProfilePicture);
    }

    public void setItems(){
        ParseUser user = ParseUser.getCurrentUser();
        fName.setText("Name:  "+user.getString("name"));
        fEmail.setText("Email: "+user.getEmail());
        fGender.setText("Gender: "+user.getString("gender"));
        if (user.getBoolean("getNotification"))
            fNotify.setText("Get Notifications : Yes");
        else fNotify.setText("Get Notifications : No");

        if(user.getBoolean("isVisible"))
            fPrivacy.setText("Profile : Public");
        else fPrivacy.setText("Profile : Private");


        ParseFile file = user.getParseFile("thumbnail");
        if (file!=null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    fProfilePic.setImageBitmap(bitmap);
                }
            });
        }
    }


    public void logout(){
        Intent lIntent = new Intent(MainProfileActivity.this, MainActivity.class);
        startActivity(lIntent);
    }

    //Starting activity for result
    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivityForResult(lIntent, fEDIT_PROFILE_REQCODE);
    }

    public void toActivity(String aIntent, int aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("user_dir_flag", aExtra);
        startActivity(lIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == fEDIT_PROFILE_REQCODE){
            if (resultCode == RESULT_OK){
                setItems();
            }
        }
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void editProfileOnClick (View aView){
        toActivity(fGOTO_EDIT_PROFILE);
    }

    public void viewInboxOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_INBOX);
    }

    public void viewAlbumOnClick (MenuItem aItem){
        toActivity(fGOTO_ALBUM_LIST);
    }

    public void viewUserDirectoryOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_DIRECTORY,1);
    }

    public void logoutOnClick (MenuItem aItem){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    logout();
                    finish();
                }
            }
        });
    }
}
