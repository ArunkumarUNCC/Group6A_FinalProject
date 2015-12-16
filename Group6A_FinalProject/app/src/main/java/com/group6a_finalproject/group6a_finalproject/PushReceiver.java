package com.group6a_finalproject.group6a_finalproject;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Arunkumar's on 12/11/2015.
 */
public class PushReceiver extends ParsePushBroadcastReceiver{

    final String fGOTO_NOTIFICATIONS = "android.intent.action.NOTIFICATIONS";
    final String fGOTO_EDIT_SHARED_PHOTO = "android.intent.action.EDIT_SHARED_PHOTO";
    final String fGOTO_ALBUM_VIEW = "android.intent.action.ALBUM_VIEW";
    final String fGOTO_USER_DIRECTORY ="android.intent.action.USER_DIRECTORY";
    final String fGOTO_USER_INBOX = "android.intent.action.USER_INBOX";

    JSONObject fData;
    public static final String fPARSE_DATA_KEY = "com.parse.Data";

    @Override
    protected void onPushOpen(final Context context, Intent intent) {
        super.onPushOpen(context, intent);

        fData = getDataFromIntent(intent);
        String lActivityType = null;
        try {
            lActivityType = fData.getString("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent lIntent;

        if (lActivityType!=null) {
            switch (lActivityType) {
                case "Album Invite":
                    lIntent = new Intent(fGOTO_NOTIFICATIONS);
                    lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lIntent);
                    break;

                case "New Photo":
                    String lPhotoId = null;
                    try {
                        lPhotoId = fData.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    lIntent = new Intent(fGOTO_EDIT_SHARED_PHOTO);
                    lIntent.putExtra("photoId", lPhotoId);
                    lIntent.putExtra("photoPosition",0);
                    lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lIntent);
                    break;

                case "Photo Accept":
                    String lAlbumId = null;
                    try {
                        lAlbumId = fData.getString("album");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (lAlbumId!=null){
                        ParseQuery<ParseObject> lAlbum = ParseQuery.getQuery("Album");
                        lAlbum.whereEqualTo("objectId",lAlbumId);
                        lAlbum.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null){
                                    String lAlbumName = objects.get(0).getString("name");

                                    Intent lIntent = new Intent(fGOTO_ALBUM_VIEW);
                                    lIntent.putExtra("ALBUM_TITLE",lAlbumName);
                                    lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(lIntent);
                                }
                            }
                        });
                    }
                    break;

                case "New User":
                    lIntent = new Intent(fGOTO_USER_DIRECTORY);
                    lIntent.putExtra("fromCompose", 1);
                    lIntent.putExtra("fromShared",false);
                    lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lIntent);
                    break;

                case "New Message":
                    lIntent = new Intent(fGOTO_USER_INBOX);
                    lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(lIntent);
                    break;
            }
        }
    }

    private JSONObject getDataFromIntent(Intent intent) {
        JSONObject data = null;
        try {
            data = new JSONObject(intent.getExtras().getString(fPARSE_DATA_KEY));
        } catch (JSONException e) {
            // Json was not readable...
        }
        return data;
    }
}
