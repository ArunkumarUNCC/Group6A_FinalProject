package com.group6a_finalproject.group6a_finalproject;

import android.app.ProgressDialog;
import android.content.Context;
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
public class GetAlbumsAsync extends AsyncTask<String,Void,ArrayList<Album>> {

    IGetAlbums fActivity;
    ProgressDialog fProgress;
    String fPROGRESSMESSAGE = "Loading Albums";

    ArrayList<Album> fAlbums;

    ParseUser fCurrentUser = ParseUser.getCurrentUser();

    public GetAlbumsAsync(IGetAlbums fActivity) {
        this.fActivity = fActivity;
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
        final Album lCurrentAlbum = new Album();
        lCurrentAlbum.setAlbumName(object.getString("name"));
        lCurrentAlbum.setPrivacy(object.getString("privacy"));
        ParseFile lPhotoImage = object.getParseFile("thumbnail");

        if (lPhotoImage != null) {
            lPhotoImage.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    lCurrentAlbum.setAlbumImage(bitmap);

                }
            });
        } else{

            lCurrentAlbum.setAlbumImage(null);
        }

        switch (aUserFlag){
            case 1:
                lCurrentAlbum.setOwnerName(ParseUser.getCurrentUser().getString("name"));
                break;
            case 2:
                try {
                    lCurrentAlbum.setOwnerName(object.getParseUser("owner").fetchIfNeeded().getString("name"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
        fAlbums.add(lCurrentAlbum);
    }

    @Override
    protected ArrayList<Album> doInBackground(String... params) {
        ParseQuery<ParseObject> lGetPublicAlbums = ParseQuery.getQuery("Album");
        lGetPublicAlbums.whereEqualTo("privacy", "Public");
        try {
            List<ParseObject> lObjects = lGetPublicAlbums.find();

            if(lObjects.size()>0) {
                for (ParseObject album : lObjects) {
                    addToList(album, 2);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> lGetMyPrivateAlbums = ParseQuery.getQuery("Album");
        lGetMyPrivateAlbums.include("owner");
        lGetMyPrivateAlbums.whereEqualTo("owner", fCurrentUser);
        lGetMyPrivateAlbums.whereEqualTo("privacy","Private");
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
