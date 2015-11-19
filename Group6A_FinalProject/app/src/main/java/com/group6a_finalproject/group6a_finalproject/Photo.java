package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;

/**
 * Created by Arunkumar's on 11/19/2015.
 */
public class Photo {
    String photoName;
    Bitmap photoBitmap;

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }
}
