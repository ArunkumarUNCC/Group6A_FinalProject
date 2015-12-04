package com.group6a_finalproject.group6a_finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by Arunkumar's on 11/18/2015.
 */
public class GetPhotosAsync extends AsyncTask<String,Void,ArrayList<Photo>> {
    IGetPhotos fActivity;
    ProgressDialog fProgress;
    String fPROGRESSMESSAGE = "Loading Photos";
    int which;

    ArrayList<Photo> fAlbumPhotos;

    ParseUser fCurrentUser = ParseUser.getCurrentUser();

    public GetPhotosAsync(IGetPhotos fActivity,int aWhichTable) {
        this.fActivity = fActivity;
        fAlbumPhotos = new ArrayList<Photo>();
        this.which = aWhichTable;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        fProgress = new ProgressDialog((Context) fActivity);
        fProgress.setMessage(fPROGRESSMESSAGE);
        fProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fProgress.show();
    }

    public void addToList(ParseObject object){
        final Photo lCurrentAlbum = new Photo();
        lCurrentAlbum.setPhotoName(object.getString("name"));

        ParseFile lPhotoImage = object.getParseFile("thumbnail");

        if (lPhotoImage != null) {
            lCurrentAlbum.setPhotoBitmap(lPhotoImage);
        } else{
            lCurrentAlbum.setPhotoBitmap(null);
        }
        fAlbumPhotos.add(lCurrentAlbum);
    }

    @Override
    protected ArrayList<Photo> doInBackground(String... params) {

        ParseQuery<ParseObject> lGetAlbumID = ParseQuery.getQuery("Album");
        lGetAlbumID.whereEqualTo("name", params[0]);
        try {
            List<ParseObject> objects = lGetAlbumID.find();

            if (!objects.isEmpty()) {
                ParseQuery<ParseObject> lGetPhotos = ParseQuery.getQuery("Photos");
                lGetPhotos.whereEqualTo("album", objects.get(0));

                List<ParseObject> photos = lGetPhotos.find();
                for (ParseObject object : photos) {
                    addToList(object);
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fAlbumPhotos;

    }

    @Override
    protected void onPostExecute(ArrayList<Photo> photos) {
        super.onPostExecute(photos);

        fProgress.dismiss();
        fActivity.putPhotos(photos);
    }

    public static interface IGetPhotos{
        public void putPhotos(ArrayList<Photo> photos);
    }
}
