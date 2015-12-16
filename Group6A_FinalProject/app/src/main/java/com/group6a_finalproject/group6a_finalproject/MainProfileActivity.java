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

import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainProfileActivity extends AppCompatActivity {

    final String fGOTO_EDIT_PROFILE = "android.intent.action.EDIT_PROFILE";
    final String fGOTO_ALBUM_LIST = "android.intent.action.ALBUM_LIST";
    final String fGOTO_USER_DIRECTORY ="android.intent.action.USER_DIRECTORY";
    final String fGOTO_USER_INBOX = "android.intent.action.USER_INBOX";
    final String fGOTO_NOTIFICATIONS = "android.intent.action.NOTIFICATIONS";
    final int fEDIT_PROFILE_REQCODE = 1001;

//    ImageView fProfilePic;
    TextView fName, fEmail, fGender,fPrivacy,fNotify;
    ParseImageView fProfilePic;
    ParseUser fCurrentUser = ParseUser.getCurrentUser();

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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void getItems (){
        fName = (TextView) findViewById(R.id.textViewProfileName);
        fEmail = (TextView) findViewById(R.id.textViewProfileEmail);
        fGender = (TextView) findViewById(R.id.textViewProfileGender);
        fNotify = (TextView) findViewById(R.id.textViewProfileNotification);
        fPrivacy = (TextView) findViewById(R.id.textViewProfilePrivacy);

        fProfilePic = (ParseImageView) findViewById(R.id.imageViewProfilePicture);
    }

    public void setItems(){

        fName.setText("Name:  "+fCurrentUser.getString("name"));

        if (fCurrentUser.getEmail() == null){
            fEmail.setText("No Email");
        }else
            fEmail.setText("Email: "+fCurrentUser.getEmail());
        fGender.setText("Gender: "+fCurrentUser.getString("gender"));
        if (fCurrentUser.getBoolean("getNotification"))
            fNotify.setText("Get Notifications : Yes");
        else fNotify.setText("Get Notifications : No");

        if(fCurrentUser.getBoolean("isVisible"))
            fPrivacy.setText("Profile : Public");
        else fPrivacy.setText("Profile : Private");


        ParseFile file = fCurrentUser.getParseFile("thumbnail");
        if (file!=null) {
            fProfilePic.setParseFile(file);
            fProfilePic.loadInBackground();
            fProfilePic.setScaleType(ParseImageView.ScaleType.FIT_XY);
        }
    }


    public void logout(){
        if(LoginManager.getInstance() != null)
            LoginManager.getInstance().logOut();
        Intent lIntent = new Intent(MainProfileActivity.this, MainActivity.class);
        startActivity(lIntent);
    }

    //Starting activity for result
    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivityForResult(lIntent, fEDIT_PROFILE_REQCODE);
    }

    //To start View Albums
    public void toActivity(String aIntent, int aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("album_flag", aExtra);
        lIntent.putExtra("current_user",fCurrentUser.getEmail());
        startActivity(lIntent);
    }

    //To start User Directory
    public void toActivity(String aIntent, int aExtra1, boolean aExtra2){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("fromCompose", aExtra1);
        lIntent.putExtra("fromShared",aExtra2);
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

    public void editProfileOnClick (MenuItem aItem){
        toActivity(fGOTO_EDIT_PROFILE);
    }

    public void viewInboxOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_INBOX);
//        finish();
    }

    public void viewAlbumOnClick (MenuItem aItem){
        toActivity(fGOTO_ALBUM_LIST, 1);
//        finish();
    }

    public void viewUserDirectoryOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_DIRECTORY, 1, false);
//        finish();
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

    public void viewNotificationsOnClick(MenuItem aItem){
        toActivity(fGOTO_NOTIFICATIONS);
    }
}
