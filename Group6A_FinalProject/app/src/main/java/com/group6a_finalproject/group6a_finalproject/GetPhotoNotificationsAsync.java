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
 * Created by Arunkumar's on 12/1/2015.
 */
public class GetPhotoNotificationsAsync extends AsyncTask<Void,Void,ArrayList<String>>{

    final String fNAME_COULUMN = "name";
    final String fALBUM_COULUMN = "album";
    final String fTHUMBNAIL_COULUMN = "thumbnail";
    final String fFROM_USER = "fromUser";
    final String fTO_USER = "toUser";

    IGetPhotoNotifications fActivity;
    ProgressDialog fProgress;
    String fPROGRESSMESSAGE = "Loading Notifications";

    ArrayList<String> fPhotos;
    ParseUser fCurrentUser = ParseUser.getCurrentUser();

    public GetPhotoNotificationsAsync(IGetPhotoNotifications fActivity) {
        this.fActivity = fActivity;
        this.fPhotos = new ArrayList<>();
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
    protected ArrayList<String> doInBackground(Void... params) {
        ParseQuery<ParseObject> lQueryAlbumNotifications = ParseQuery.getQuery("Notifications");
        lQueryAlbumNotifications.include(fTO_USER);
        lQueryAlbumNotifications.whereEqualTo(fTO_USER, fCurrentUser);
        lQueryAlbumNotifications.whereExists(fTHUMBNAIL_COULUMN);

        lQueryAlbumNotifications.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        fPhotos.add(object.getObjectId());
                    }
                }
            }
        });
        return fPhotos;
    }

    @Override
    protected void onPostExecute(ArrayList<String> photos) {
        super.onPostExecute(photos);

        fProgress.dismiss();
        fActivity.putPhotoNotifications(photos);
    }

    public static interface IGetPhotoNotifications{
        public void putPhotoNotifications(ArrayList<String> photos);
    }


}
