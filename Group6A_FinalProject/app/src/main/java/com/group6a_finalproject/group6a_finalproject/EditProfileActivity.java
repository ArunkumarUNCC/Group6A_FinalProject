package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    final String fGOTO_MAIN_PROFILE = "android.intent.action.MAIN_PROFILE";
    final int fSELECT_PICTURE = 1;

    ParseImageView fProfilePic;
    EditText fName, fEmail;
    Switch fGender,fPrivacy,fNotify;

    Bitmap fImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fImageBitmap = null;

        getItems();
        setItems();
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
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void getItems (){
        fName = (EditText) findViewById(R.id.editTextEditProfileName);
        fEmail = (EditText) findViewById(R.id.editTextEditProfileEmail);
        fGender = (Switch) findViewById(R.id.switchEdiptProfileGender);
        fPrivacy = (Switch) findViewById(R.id.switchEditProfilePrivacy);
        fNotify = (Switch) findViewById(R.id.switchEditProfileNotifications);

        fProfilePic = (ParseImageView) findViewById(R.id.imageViewEditProfilePicture);
    }

    public void setItems(){
        ParseUser user = ParseUser.getCurrentUser();
        fName.setText(user.getString("name"));
        fEmail.setText(user.getEmail());
        if (user.getString("gender").equals("Male"))
            fGender.setChecked(false);
        else fGender.setChecked(true);
        fPrivacy.setChecked(user.getBoolean("isVisible"));
        fNotify.setChecked(user.getBoolean("getNotification"));

        ParseFile file = user.getParseFile("thumbnail");
        if (file!=null) {
            fProfilePic.setParseFile(file);
            fProfilePic.loadInBackground();
            fProfilePic.setScaleType(ParseImageView.ScaleType.FIT_XY);
        }
    }

    public void toActivity(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("Some Extra", aExtra);
        startActivity(lIntent);
    }

    public void sendActivityResult(){
        Intent lIntent = new Intent();
        setResult(RESULT_OK,lIntent);
        finish();
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void saveProfileOnClick (View aView){
        final String lName,lEmail,lGender;
        lName = fName.getText().toString();
        lEmail =fEmail.getText().toString();
        lGender = fGender.isChecked()?"Female":"Male";

        //Validations
        Pattern lCheckSpace = Pattern.compile("\\s");
        Matcher lSpaceMatcher = lCheckSpace.matcher(lName);
        boolean lIsSpace = lSpaceMatcher.find();

        if(lName.isEmpty() || lEmail.isEmpty() || !lIsSpace){
            if (lName.isEmpty())
                fName.setError("Name is empty");
            if (!lIsSpace)
                fName.setError("Invalid name");
            if (lEmail.isEmpty())
                fEmail.setError("Email is empty");

        }
        else{
            ParseUser lSaveUser = ParseUser.getCurrentUser();
            lSaveUser.setUsername(lEmail);
            lSaveUser.setEmail(lEmail);
            lSaveUser.put("gender", lGender);
            lSaveUser.put("name", lName);
            lSaveUser.put("getNotification",fNotify.isChecked());
            lSaveUser.put("isVisible",fPrivacy.isChecked());

            if(fImageBitmap!=null) {
                ByteArrayOutputStream lStream = new ByteArrayOutputStream();
                fImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, lStream);
                byte[] lImageToUpload = lStream.toByteArray();

                final ParseFile lImageFile = new ParseFile("default.png", lImageToUpload);
                lImageFile.saveInBackground();
                lSaveUser.put("thumbnail", lImageFile);
            }

            lSaveUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null) {
                        makeToast("Profile updated!");
                        sendActivityResult();
                    }
                    else{
                        fEmail.setError("Invalid Email");
                        e.printStackTrace();
                    }
                }
            });
        }

        //toActivity(fGOTO_MAIN_PROFILE, "Some extra");
    }

    public void cancelOnClick (View aView){
        finish();
    }

    public void editAvatarOnClick (View aView){
        Intent lPictureIntent = new Intent(Intent.ACTION_PICK);
        lPictureIntent.setType("image/");
        startActivityForResult(lPictureIntent, fSELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case fSELECT_PICTURE:
                    Uri lSelectedImgUri;
                    lSelectedImgUri = data.getData();
                    try {
                        fImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),lSelectedImgUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fProfilePic.setImageURI(lSelectedImgUri);


            }
        }
    }
}
