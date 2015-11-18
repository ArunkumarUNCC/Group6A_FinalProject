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
        //TODO Update the initialize
        Parse.initialize(this, "VhuCw5tx7kT9ziE2iZGtvdhdbQsglap3rpu4Oozk", "QwWyqBVc4HK54AHAy1w367aFDUWDSzWLtDPsT4kn");
    }
}
