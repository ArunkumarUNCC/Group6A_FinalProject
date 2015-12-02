package com.group6a_finalproject.group6a_finalproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arunkumar's on 12/1/2015.
 */
public class GetAlbumNotificationsAsync extends AsyncTask<Void,Void,ArrayList<Album>> {

    IGetAlbumNotifications fActivity;
    ProgressDialog fProgress;
    String fPROGRESSMESSAGE = "Loading Notifications";

    ArrayList<Album> fAlbums;
    ParseUser fCurrentUser = ParseUser.getCurrentUser();

    public GetAlbumNotificationsAsync(IGetAlbumNotifications fActivity) {
        this.fActivity = fActivity;
        this.fAlbums = new ArrayList<>();
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
    protected ArrayList<Album> doInBackground(Void... params) {

        ParseQuery<ParseObject> lQueryAlbumNotifications = ParseQuery.getQuery("Notifications");
        lQueryAlbumNotifications.include("toUser");
        lQueryAlbumNotifications.include("fromUser");
        lQueryAlbumNotifications.include("album");
        lQueryAlbumNotifications.whereEqualTo("toUser", fCurrentUser);
        lQueryAlbumNotifications.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject object:objects){
                        final Album album = new Album();
                        ParseObject lAlbum = object.getParseObject("album");
                        ParseObject lAlbumOwner = object.getParseObject("fromUser");

                        album.setAlbumName(lAlbum.getString("name"));
                        album.setOwnerName(lAlbumOwner.getString("name"));

                        ParseFile lPhotoImage = lAlbum.getParseFile("thumbnail");
                        if (lPhotoImage != null) {
                            lPhotoImage.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    album.setAlbumImage(bitmap);

                                }
                            });
                        } else{
                            album.setAlbumImage(null);
                        }

                        fAlbums.add(album);
                    }
                }
            }
        });

        return fAlbums;
    }

    @Override
    protected void onPostExecute(ArrayList<Album> albums) {
        super.onPostExecute(albums);

        fProgress.dismiss();
        fActivity.putAlbumNotifications(albums);
    }

    public static interface IGetAlbumNotifications{
        public void putAlbumNotifications(ArrayList<Album> photos);
    }
}
