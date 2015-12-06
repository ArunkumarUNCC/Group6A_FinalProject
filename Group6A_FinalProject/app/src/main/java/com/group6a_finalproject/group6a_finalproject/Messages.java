package com.group6a_finalproject.group6a_finalproject;

import android.graphics.Bitmap;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Michael.
 */
public class Messages implements Serializable{
    String messageBody, toField, fromField, objectID;
    Date timeStamp;
    ParseFile attachment, userIcon;

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

    public ParseFile getUserIcon() throws ParseException {
//        Log.d("Something", getFromField());
        ParseQuery<ParseObject> lQuery = ParseQuery.getQuery("MessageTable")
                .whereEqualTo("objectId", getObjectID());
        List<ParseObject> lMessage = lQuery.find();
        return lMessage.get(0).getParseUser("userFrom").fetchIfNeeded().getParseFile("thumbnail");
    }

    public void setUserIcon(ParseFile userIcon) {

        this.userIcon = userIcon;
    }

    public ParseFile getAttachment() throws ParseException {
        //query
        ParseQuery<ParseObject> lQuery = ParseQuery.getQuery("MessageTable")
                .whereEqualTo("objectId", getObjectID());
        List<ParseObject> lMessage = lQuery.find();
        return lMessage.get(0).getParseFile("Attachment");
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
