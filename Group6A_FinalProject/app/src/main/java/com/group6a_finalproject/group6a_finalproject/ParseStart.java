package com.group6a_finalproject.group6a_finalproject;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Michael.
 */
public class ParseStart extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "YtE1gtFuBgmjXkjR4kQhlb7Jfu8TJxysKecc0dyL", "BKUis8Z2F2OqwfrEZg8D5xMtYZMjS3LAmlJQ68qb");
    }
}
