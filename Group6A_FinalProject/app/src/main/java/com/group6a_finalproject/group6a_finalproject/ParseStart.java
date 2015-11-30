package com.group6a_finalproject.group6a_finalproject;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

/**
 * Created by Michael.
 */
public class ParseStart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "YtE1gtFuBgmjXkjR4kQhlb7Jfu8TJxysKecc0dyL", "BKUis8Z2F2OqwfrEZg8D5xMtYZMjS3LAmlJQ68qb");

        //For push notifications
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
//                    Log.d("Hello", "world");
                }else e.printStackTrace();
            }
        });
    }
}
