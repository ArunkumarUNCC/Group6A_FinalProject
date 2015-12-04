package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;

import com.parse.ParseFile;

/**
 * Created by Arunkumar's on 11/19/2015.
 */
public class Photo {
    String photoName,objectId;
    ParseFile photoBitmap;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public ParseFile getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(ParseFile photoBitmap) {
        this.photoBitmap = photoBitmap;
    }
}
