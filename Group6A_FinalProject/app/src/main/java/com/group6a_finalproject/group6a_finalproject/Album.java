package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;

import com.parse.ParseFile;

/**
 * Created by Arunkumar's on 11/25/2015.
 */
public class Album {
    String albumName,ownerName,ownerEmail,privacy;
//    Bitmap albumImage;
    ParseFile albumImage;

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public ParseFile getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(ParseFile albumImage) {
        this.albumImage = albumImage;
    }
}
