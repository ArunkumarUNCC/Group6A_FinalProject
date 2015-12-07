package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    final String fGOTO_REGISTER_NEW_USER = "android.intent.action.REGISTER_NEW_USER";
    final String fGOTO_MAIN_PROFILE = "android.intent.action.MAIN_PROFILE";
    EditText fEmail, fPassword;
    LoginButton fFacebookLogin;
    CallbackManager fCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getItems();

        ParseUser lCheckUser = ParseUser.getCurrentUser();
        if(lCheckUser!=null){
            toActivity(fGOTO_MAIN_PROFILE);
            finish();
        }

        fFacebookLogin.registerCallback(fCallBack, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        fEmail = (EditText) findViewById(R.id.editTextLoginEmail);
        fPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        fFacebookLogin = (LoginButton) findViewById(R.id.facebookLogin_button);
        FacebookSdk.sdkInitialize(getApplicationContext());
        fCallBack = CallbackManager.Factory.create();
    }

    public void facebookLoginOnClick (View aView){

    }

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public void loginOnClick (View aView){
        String lEmail, lPassWord;
        lEmail = fEmail.getText().toString();
        lPassWord = fPassword.getText().toString();

        if(lEmail.isEmpty() || lPassWord.isEmpty()){
            if(lEmail.isEmpty()){
                fEmail.setError("Email cannot be empty!");
            }

            if(lPassWord.isEmpty()){
                fPassword.setError("Password cannot be empty!");
            }
            return;
        }else{
            ParseUser.logInInBackground(lEmail, lPassWord, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
//                        makeToast("Login Successful");
                        toActivity(fGOTO_MAIN_PROFILE);

                        ParseQuery lSetChannel = ParseInstallation.getQuery();
                        lSetChannel.include("user");
                        lSetChannel.whereEqualTo("user",user);

                        ParsePush lSubscribe = new ParsePush();
                        lSubscribe.setQuery(lSetChannel);
                        lSubscribe.subscribeInBackground("NewUser");


                        finish();
                    } else
                        makeToast("Invalid credentials");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fCallBack.onActivityResult(requestCode, resultCode,  data);

    }

    public void registerOnClick (View aView){
        toActivity(fGOTO_REGISTER_NEW_USER);
    }
}
