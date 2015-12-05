package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;

import com.parse.ParseFile;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Michael.
 */
public class Messages implements Serializable{
    String messageBody, toField, fromField, objectID;
    Date timeStamp;
    ParseFile attachment;
    Bitmap userIcon;

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getToField() {
        return toField;
    }

    public void setToField(String toField) {
        this.toField = toField;
    }

    public String getFromField() {
        return fromField;
    }

    public void setFromField(String fromField) {
        this.fromField = fromField;
    }

    public Bitmap getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(Bitmap userIcon) {
        this.userIcon = userIcon;
    }

    public ParseFile getAttachment() {
        return attachment;
    }

    public void setAttachment(ParseFile attachment) {
        this.attachment = attachment;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
