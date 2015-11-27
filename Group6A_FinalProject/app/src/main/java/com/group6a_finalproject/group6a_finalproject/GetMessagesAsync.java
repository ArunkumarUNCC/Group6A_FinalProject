package com.group6a_finalproject.group6a_finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael.
 */
public class GetMessagesAsync extends AsyncTask<Void, Void, ArrayList<Messages>> {
    IGetMessages fActivity;
    ProgressDialog fProgress;
    final String fPROGRESS_MSG = "Loading messages...";
    final String fMESSAGE_TABLE = "MessageTable";

    ArrayList<Messages> fMessageList;

    public GetMessagesAsync(IGetMessages aActivity){
        this.fActivity = aActivity;
        fMessageList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        fProgress = new ProgressDialog((Context) fActivity);
        fProgress.setMessage(fPROGRESS_MSG);
        fProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fProgress.show();
    }

    public void addToList(ParseObject aMessage){
        final Messages lMessage = new Messages();
        lMessage.setFromField(aMessage.getString("userFrom"));
        lMessage.setToField(aMessage.getString("userTo"));
        lMessage.setMessageBody(aMessage.getString("Message"));
        //add icon
        fMessageList.add(lMessage);
    }

    @Override
    protected ArrayList<Messages> doInBackground(Void... params) {
        ParseQuery<ParseObject> lMessages = ParseQuery.getQuery(fMESSAGE_TABLE);
        lMessages.include("userTo");//column names!
        lMessages.whereEqualTo("userTo", ParseUser.getCurrentUser());
        try {
            List<ParseObject> lMessageList = lMessages.find();
            for(ParseObject message : lMessageList){
                addToList(message);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fMessageList;
    }

    @Override
    protected void onPostExecute(ArrayList<Messages> messages) {
        super.onPostExecute(messages);
        fProgress.dismiss();
        fActivity.putMessages(messages);
    }

    public static interface IGetMessages{
        public void putMessages(ArrayList<Messages> messages);
    }
}
