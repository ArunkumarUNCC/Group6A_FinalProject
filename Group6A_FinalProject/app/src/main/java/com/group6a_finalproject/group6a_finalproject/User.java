package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;

/**
 * Created by Arunkumar's on 11/24/2015.
 */
public class User {
    String userName,userMail;
    Bitmap userImage;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }
}
