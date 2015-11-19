package com.group6a_finalproject.group6a_finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by Arunkumar's on 11/18/2015.
 */
public class GetPhotosAsync extends AsyncTask<String,Void,ArrayList<Photo>> {
    IGetPhotos fActivity;
    ProgressDialog fProgress;
    String fPROGRESSMESSAGE = "Loading Photos";

    public GetPhotosAsync(IGetPhotos fActivity) {
        this.fActivity = fActivity;
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

        //TODO -- Get all photos from the parse database

        return null;
    }

    public static interface IGetPhotos{
        public void putPhotos(ArrayList<Bitmap> photos);
    }
}
