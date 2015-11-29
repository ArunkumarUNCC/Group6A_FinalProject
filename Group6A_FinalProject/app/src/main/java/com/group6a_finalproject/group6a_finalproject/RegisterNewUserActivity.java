package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterNewUserActivity extends AppCompatActivity {

    TextView fName, fEmail, fPassword, fConfirmPassword;
    Switch fGenderSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        getItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_new_user, menu);
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
        fName = (TextView) findViewById(R.id.editTextRegisterName);
        fEmail = (TextView) findViewById(R.id.editTextRegisterEmail);
        fPassword = (TextView) findViewById(R.id.editTextRegisterPassword);
        fConfirmPassword = (TextView) findViewById(R.id.editTextRegisterConfirmPassword);
        fGenderSwitch = (Switch) findViewById(R.id.switchRegisterGender);
    }


    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void signUpOnClick (View aView){

        final String lName,lEmail,lPassword,lConfirmPassword,lGender;
        lName = fName.getText().toString();
        lEmail = fEmail.getText().toString();
        lPassword = fPassword.getText().toString();
        lConfirmPassword = fConfirmPassword.getText().toString();
        lGender = fGenderSwitch.isChecked()==true?"Female":"Male";

        //Validations
        Pattern lCheckSpace = Pattern.compile("\\s");
        Matcher lSpaceMatcher = lCheckSpace.matcher(lName);
        boolean lIsSpace = lSpaceMatcher.find();

        if(lName.isEmpty()){
            fName.setError("Enter Name");
        }
        else if(!lIsSpace){
            fName.setText("");
            fName.setError("Invalid Name");
        }
        if(lEmail.isEmpty()){
            fEmail.setError("Enter Email");
        }
        if (lPassword.isEmpty()){
            fPassword.setError("Empty Password");
        }
        if (lConfirmPassword.isEmpty()){
            fConfirmPassword.setError("Re Enter Password");
        }
        if(!lPassword.equals(lConfirmPassword)){
            fPassword.setText("");
            fConfirmPassword.setText("");
            makeToast("Passwords Mismatch");

            return;
        }

        //Signing up in Parse
        ParseUser lSignupUser = new ParseUser();
        lSignupUser.setEmail(lEmail);
        lSignupUser.setPassword(lPassword);
        lSignupUser.setUsername(lEmail);
        lSignupUser.put("name", lName);
        lSignupUser.put("gender", lGender);
        lSignupUser.put("getNotification",true);
        lSignupUser.put("isVisible",true);
        lSignupUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    ParseInstallation lNewUser = ParseInstallation.getCurrentInstallation();
                    lNewUser.put("user", lEmail);
                    lNewUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParsePush lSendNotification = new ParsePush();
                                ParseQuery lQuery = ParseInstallation.getQuery();
                                lQuery.whereNotEqualTo("user", lEmail);
                                lSendNotification.sendMessageInBackground("New User " + lName + " signed up", lQuery);

                                makeToast("Registration Successful");
                                finish();
                            }
                        }
                    });


                }
                else{
                    fEmail.setError("Email already exists");
                }
            }
        });
    }

    public void cancelOnClick (View aView){
        finish();
    }
}
