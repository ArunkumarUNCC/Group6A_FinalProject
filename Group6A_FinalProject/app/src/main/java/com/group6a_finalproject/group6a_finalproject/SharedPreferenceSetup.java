package com.group6a_finalproject.group6a_finalproject;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Arunkumar's on 11/27/2015.
 */
public class SharedPreferenceSetup {
    static SharedPreferences sp;

    public SharedPreferenceSetup(SharedPreferences sp) {
        this.sp = sp;
    }

    static boolean checkPreferences(String checkString){
        if(sp.contains(checkString)){
            return true;
        }
        else return false;
    }

    public void putAlbumPreferences(String apreferenceString, Album aAlbumInstance) {
        Gson gson = new Gson();
        String albumJson = gson.toJson(aAlbumInstance);
        sp.edit().putString(apreferenceString,albumJson).apply();

    }

    public void putAlbumPreferences(String aPreferenceString,String aOldAlbumName){
        sp.edit().putString(aPreferenceString,aOldAlbumName).apply();
    }

    public Album getAlbumPreference(String getValue) {
        Gson gson = new Gson();
        String albumJson = sp.getString(getValue,null);
        Type type = new TypeToken<Album>(){}.getType();
        return gson.fromJson(albumJson,type);
    }

    public String getOldAlbumPreference(String key){
        return sp.getString(key,null);
    }

    public boolean checkKey(String key){
        return sp.contains(key);
    }
}
