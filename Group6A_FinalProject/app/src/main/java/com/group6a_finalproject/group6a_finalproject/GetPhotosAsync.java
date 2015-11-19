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

    ArrayList<Photo> fAlbumPhotos;

    public GetPhotosAsync(IGetPhotos fActivity) {
        this.fActivity = fActivity;
        fAlbumPhotos = new ArrayList<Photo>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        fProgress = new ProgressDialog((Context) fActivity);
        fProgress.setMessage(fPROGRESSMESSAGE);
        fProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fProgress.show();
    }

    @Override
    protected ArrayList<Photo> doInBackground(String... params) {
        ParseQuery<ParseObject> lGetAlbumID = ParseQuery.getQuery("Album");
        lGetAlbumID.whereEqualTo("name", params[0]);
        lGetAlbumID.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                    if (!objects.isEmpty()) {
                        ParseQuery<ParseObject> lGetPhotos = ParseQuery.getQuery("Photos");
                        lGetPhotos.whereEqualTo("album", objects.get(0));
                        lGetPhotos.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> photos, ParseException e) {
                                if (e == null) {
                                    ArrayList<Photo> lPhotosList = new ArrayList<Photo>();

                                    for (ParseObject object : photos) {
                                        final Photo lNewPhoto = new Photo();
                                        lNewPhoto.setPhotoName(object.getString("name"));

                                        ParseFile lPhotoImage = object.getParseFile("photo");
                                        if (lPhotoImage != null) {
                                            lPhotoImage.getDataInBackground(new GetDataCallback() {
                                                @Override
                                                public void done(byte[] data, ParseException e) {
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                                    lNewPhoto.setPhotoBitmap(bitmap);
                                                }
                                            });
                                        }
                                        lPhotosList.add(lNewPhoto);
                                    }
                                    fAlbumPhotos = lPhotosList;
                                } else e.printStackTrace();

                            }
                        });
                    }
                }

            }
        });


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
