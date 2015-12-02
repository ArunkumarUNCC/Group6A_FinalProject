package com.group6a_finalproject.group6a_finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arunkumar's on 11/24/2015.
 */
public class GetUsersAsync extends AsyncTask<Void,Void,ArrayList<User>>{

    IGetUsers fActivity;
    ProgressDialog fProgress;
    String fPROGRESS_MESSAGE = "Loading Users";

    ArrayList<User> fUsers;
    boolean fIsShared;
    String fAlbumName = null;
    List<ParseUser> fSharedUsers;

    public GetUsersAsync(IGetUsers fActivity) {
        this.fActivity = fActivity;
        fUsers = new ArrayList<>();
    }

    public GetUsersAsync(IGetUsers fActivity,String aAlbumName) {
        this.fActivity = fActivity;
        this.fAlbumName = aAlbumName;
        fUsers = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        fProgress = new ProgressDialog((Context) fActivity);
        fProgress.setMessage(fPROGRESS_MESSAGE);
        fProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fProgress.show();
    }

    public void addToList(ParseUser object){
        final User lUser = new User();
        lUser.setUserName(object.getString("name"));
        lUser.setUserMail(object.getEmail());
        ParseFile lUserImage = object.getParseFile("thumbnail");

        if (lUserImage != null) {
            lUserImage.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    lUser.setUserImage(bitmap);

                }
            });
        } else lUser.setUserImage(null);
        fUsers.add(lUser);
    }

    @Override
    protected ArrayList<User> doInBackground(Void... params) {
        ParseQuery<ParseUser> lGetUsers = ParseQuery.getQuery("_User");
        lGetUsers.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        lGetUsers.whereEqualTo("isVisible", true);
        try {
            List<ParseUser> lUsers = lGetUsers.find();

            if(fAlbumName!=null){
                fSharedUsers = getSharedUsers();
            }

            for (ParseUser user:lUsers){
                if (!fSharedUsers.contains(user))
                    addToList(user);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fUsers;
    }

    @Override
    protected void onPostExecute(ArrayList<User> users) {
        super.onPostExecute(users);

        fProgress.dismiss();
        fActivity.putUsers(users);
    }

    public List<ParseUser> getSharedUsers(){
        ParseObject lAlbumId = getAlbumId(fAlbumName);
        final List<ParseUser> lSharedUsers = new ArrayList<>();

        ParseQuery<ParseObject> lUsers = ParseQuery.getQuery("AlbumShare");
        lUsers.include("album");
        lUsers.include("sharedWith");
        lUsers.whereEqualTo("album", lAlbumId);
        try {
            List<ParseObject> objects = lUsers.find();
            for(ParseObject lObject:objects){
                lSharedUsers.add(lObject.getParseUser("sharedWith"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lSharedUsers;
    }

    public ParseObject getAlbumId(String aAlbumName){
        ParseObject lAlbumId = null;

        ParseQuery<ParseObject> lGetId = ParseQuery.getQuery("Album");
        lGetId.whereEqualTo("name",aAlbumName);
        try {
            List<ParseObject> lAlbums = lGetId.find();
            lAlbumId = lAlbums.get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lAlbumId;
    }

    public static interface IGetUsers{
        void putUsers(ArrayList<User> users);
    }
}
