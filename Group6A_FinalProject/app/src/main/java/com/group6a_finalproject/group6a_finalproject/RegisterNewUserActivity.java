package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        final ParseUser lSignupUser = new ParseUser();
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
                    ParseQuery<ParseUser> user = ParseQuery.getQuery("_User");
                    user.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                    user.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                for (ParseUser object : objects) {
                                    if(object.getBoolean("getNotification")) {
                                        Map<String, String> lNotifyUsers = new HashMap<>();
                                        lNotifyUsers.put("toUser", object.getObjectId());
                                        lNotifyUsers.put("fromUser", ParseUser.getCurrentUser().getString("name"));
                                        lNotifyUsers.put("type", "New User");
                                        lNotifyUsers.put("message", " has newly signed up to the system");

                                        ParseCloud.callFunctionInBackground("notifyPushUsers", lNotifyUsers, new FunctionCallback<Object>() {
                                            @Override
                                            public void done(Object object, ParseException e) {

                                                if (e == null) {

                                                } else
                                                    e.printStackTrace();

                                            }
                                        });
                                    }
                                }
                                ParseInstallation lNewUser = ParseInstallation.getCurrentInstallation();
                                lNewUser.put("user", ParseUser.getCurrentUser().getObjectId());
                                lNewUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            makeToast("Registration Successful");
                                        } else e.printStackTrace();
                                    }
                                });
                            } else e.printStackTrace();
                        }
                    });

                }
                else{
                    fEmail.setError("Email already exists");
                    e.printStackTrace();
                }

                finish();
            }
        });
    }

    public void cancelOnClick (View aView){
        finish();
    }
}
