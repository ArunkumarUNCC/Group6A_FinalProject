package com.group6a_finalproject.group6a_finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by Arunkumar's on 11/25/2015.
 */
public class GetAlbumsAsync extends AsyncTask<Integer,Void,ArrayList<Album>> {

    IGetAlbums fActivity;
    ProgressDialog fProgress;
    String fPROGRESSMESSAGE = "Loading Albums";

    ArrayList<Album> fAlbums;

    ParseUser fCurrentUser;

    public GetAlbumsAsync(IGetAlbums fActivity,ParseUser aCurrentUser) {
        this.fActivity = fActivity;
        fCurrentUser = aCurrentUser;
        fAlbums = new ArrayList<Album>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        fProgress = new ProgressDialog((Context) fActivity);
        fProgress.setMessage(fPROGRESSMESSAGE);
        fProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fProgress.show();
    }

    public void addToList(ParseObject object,int aUserFlag){
        ParseUser lUser = null;
        final Album lCurrentAlbum = new Album();
        lCurrentAlbum.setAlbumName(object.getString("name"));
        if(aUserFlag != 2)
            lCurrentAlbum.setPrivacy(object.getString("privacy"));
        else lCurrentAlbum.setPrivacy("Shared");
        ParseFile lPhotoImage = object.getParseFile("thumbnail");

        if (lPhotoImage != null) {
            lCurrentAlbum.setAlbumImage(lPhotoImage);
        } else{

            lCurrentAlbum.setAlbumImage(null);
        }

        try {
            lUser = object.getParseUser("owner").fetchIfNeeded();
            lCurrentAlbum.setOwnerName(lUser.getString("name"));
            lCurrentAlbum.setOwnerEmail(lUser.getEmail());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int i;
        boolean isExist = false;
        for (i = 0;i<fAlbums.size();i++){
            if (fAlbums.get(0).getAlbumName().equals(lCurrentAlbum.getAlbumName())) {
                isExist = true;
                break;
            }
        }
        if(!isExist)
            fAlbums.add(lCurrentAlbum);
    }

    @Override
    protected ArrayList<Album> doInBackground(Integer... params) {
        int lQueryFlag = params[0];
        if (lQueryFlag == 1) {

            ParseQuery<ParseObject> lGetSharedAlbums = ParseQuery.getQuery("AlbumShare");
            lGetSharedAlbums.include("sharedWith");
            lGetSharedAlbums.include("album");
            lGetSharedAlbums.whereEqualTo("sharedWith", fCurrentUser);
            try {
                List<ParseObject> lObjects = lGetSharedAlbums.find();

                if (lObjects.size()>0){
                    for (ParseObject album:lObjects){
                        ParseObject lObject = album.getParseObject("album");
                        addToList(lObject,2);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ParseQuery<ParseObject> lGetPublicAlbums = ParseQuery.getQuery("Album");
            lGetPublicAlbums.include("owner");
            lGetPublicAlbums.whereEqualTo("privacy", "Public");
            lGetPublicAlbums.whereNotEqualTo("owner", fCurrentUser);
            try {
                List<ParseObject> lObjects = lGetPublicAlbums.find();

                if (lObjects.size() > 0) {
                    for (ParseObject album : lObjects) {
                        addToList(album, 1);
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ParseQuery<ParseObject> lGetMyPrivateAlbums = ParseQuery.getQuery("Album");
        lGetMyPrivateAlbums.include("owner");
        lGetMyPrivateAlbums.whereEqualTo("owner", fCurrentUser);
        if (lQueryFlag == 2){
            lGetMyPrivateAlbums.whereEqualTo("privacy","Public");
        }
        try {
            List<ParseObject> lObjects = lGetMyPrivateAlbums.find();

            if(lObjects.size()>0) {
                for (ParseObject album : lObjects) {
                    addToList(album, 1);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fAlbums;
    }

    @Override
    protected void onPostExecute(ArrayList<Album> albums) {
        super.onPostExecute(albums);

        fProgress.dismiss();
        fActivity.putAlbums(albums);
    }

    public static interface IGetAlbums{
        public void putAlbums(ArrayList<Album> photos);
    }
}
