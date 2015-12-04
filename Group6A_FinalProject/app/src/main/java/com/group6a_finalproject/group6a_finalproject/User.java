package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;

import com.parse.ParseFile;

/**
 * Created by Arunkumar's on 11/24/2015.
 */
public class User {
    String userName,userMail;
    ParseFile userImage;

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

    public ParseFile getUserImage() {
        return userImage;
    }

    public void setUserImage(ParseFile userImage) {
        this.userImage = userImage;
    }
}
