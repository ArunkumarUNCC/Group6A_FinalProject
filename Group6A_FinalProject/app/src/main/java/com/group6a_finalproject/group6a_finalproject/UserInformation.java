package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Michael.
 */
public class UserInformation{
    String userName, userEmail, userGender;
    Bitmap userPic;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public Bitmap getUserPic() {
        return userPic;
    }

    public void setUserPic(Bitmap userPic) {
        this.userPic = userPic;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userGender='" + userGender + '\'' +
                '}';
    }

}
