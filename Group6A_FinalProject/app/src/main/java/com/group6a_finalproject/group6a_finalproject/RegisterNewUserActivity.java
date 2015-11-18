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
        //TODO: implement signUp (don't forget to get gender flag -- save as male or female text)
        makeToast("Successfully registered!");
        finish();
    }

    public void cancelOnClick (View aView){
        finish();
    }
}
