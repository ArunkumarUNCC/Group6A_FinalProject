package com.group6a_finalproject.group6a_finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.group6a_finalproject.group6a_finalproject.R.id.textViewDirectoryUserEmail;
import static com.group6a_finalproject.group6a_finalproject.R.id.textViewRecyclerPrivacy;


/**
 * Created by Arunkumar's on 11/19/2015.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    final String fGOTO_ALBUM_VIEW = "android.intent.action.ALBUM_VIEW";
    final String fNOTIFICATIONS = "Notifications";
    final String fTOUSER = "toUser";
    final String fFROMUSER = "fromUser";
    final String fSHARED_WITH = "sharedWith";
    final String fALBUM_COLUMN = "album";
    final String fALBUM = "Album";
    final String fNAME = "name";
    final String fOWNER = "owner";
    final String fPRIVACY = "privacy";
    final ParseUser fCURRENT_USER = ParseUser.getCurrentUser();

    ArrayList<Photo> fPhotosForDisplay;
    ArrayList<User> fUsersForDisplay;
    ArrayList<Album> fAlbumsForDisplay;
    Context fContext;
    int whichRecycler;
    int fwhichActivity;
    String fAlbumName;

    ParseUser fAlbumOwner;

    public RecyclerAdapter(Context fContext, int which) {
        this.fContext = fContext;
        this.whichRecycler = which;
    }

    public RecyclerAdapter(ArrayList<Photo> albumPhotoList, Context fContext, int which,ParseUser aAlbumOwner,String aAlbumName) {
        this.fPhotosForDisplay = albumPhotoList;
        this.fContext = fContext;
        this.whichRecycler = which;
        this.fAlbumOwner = aAlbumOwner;
        this.fAlbumName = aAlbumName;
    }

    public RecyclerAdapter(ArrayList<User> userList, Context fContext, int which, int aWhichActivity) {
        this.fUsersForDisplay = userList;
        this.fContext = fContext;
        this.whichRecycler = which;
        this.fwhichActivity = aWhichActivity;
    }

    public void setAlbumsList(ArrayList<Album> aAlbumsList){
        this.fAlbumsForDisplay = aAlbumsList;
    }

    //To display a list of albums
    public static class AlbumsLinearViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout lAlbumRelativeLayout;
        TextView lAlbumName,lAlbumOwnerText,lAlbumPrivacyText;
        ImageView lAlbumImage,lAlbumDelete;

        public AlbumsLinearViewHolder(View itemView) {
            super(itemView);

            lAlbumRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutAlbumList);
            lAlbumImage = (ImageView) itemView.findViewById(R.id.imageViewRecyclerAlbumImage);
            lAlbumName = (TextView) itemView.findViewById(R.id.textViewRecylclerAlbumName);
            lAlbumOwnerText = (TextView) itemView.findViewById(R.id.textViewRecyclerOwnerName);
            lAlbumPrivacyText = (TextView) itemView.findViewById(textViewRecyclerPrivacy);
            lAlbumDelete = (ImageView) itemView.findViewById(R.id.imageViewRecyclerDelete);
        }
    }

    //To display photos of a particular album
    public static class PhotosGridViewHolder extends RecyclerView.ViewHolder{
        LinearLayout lPhotoGridLayout;

        TextView lPhotoName;
        ImageView lPhoto;

        public PhotosGridViewHolder(View itemView) {
            super(itemView);

            lPhotoGridLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutPhotoGrid);
            lPhotoName = (TextView) itemView.findViewById(R.id.textViewPhotoName);
            lPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
        }
    }

    //To display user list
    public static class UsersLinearViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout lUserRelativeLayout;

        TextView lUserName,lUserEmail;
        ImageView lUserPhoto;

        public UsersLinearViewHolder(View itemView) {
            super(itemView);

            lUserRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutUserRecycler);
            lUserName = (TextView) itemView.findViewById(R.id.textViewDirectoryUserName);
            lUserEmail = (TextView) itemView.findViewById(textViewDirectoryUserEmail);
            lUserPhoto = (ImageView) itemView.findViewById(R.id.imageViewDirectoryThumb);
        }
    }

    //To display album notifications
    public static class AlbumNotifications extends RecyclerView.ViewHolder{
        LinearLayout lAlbumNotificationLayout;
        ImageView lAlbumLogo,lAccept,lReject;
        TextView lAlbumShareMsg;

        public AlbumNotifications(View itemView) {
            super(itemView);

            lAlbumNotificationLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutAlbumRow);
            lAlbumLogo = (ImageView) itemView.findViewById(R.id.imageViewAlbumNotification);
            lAccept = (ImageView) itemView.findViewById(R.id.imageViewAccept);
            lReject = (ImageView) itemView.findViewById(R.id.imageViewReject);
            lAlbumShareMsg = (TextView) itemView.findViewById(R.id.textViewAlbumNotification);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder lViewHolder = null;
        LayoutInflater lInflater = LayoutInflater.from(parent.getContext());

        switch (whichRecycler){
            case 1:
                View lView1 = lInflater.inflate(R.layout.album_row, parent, false);
                lViewHolder = new AlbumsLinearViewHolder(lView1);
                break;

            case 2:
                View lView2 = lInflater.inflate(R.layout.photo_grid, parent, false);
                lViewHolder = new PhotosGridViewHolder(lView2);
                break;

            case 3:
                View lView3 = lInflater.inflate(R.layout.user_directory_row, parent, false);
                lViewHolder = new UsersLinearViewHolder(lView3);
                break;

            case 4:
                View lView4 = lInflater.inflate(R.layout.album_notification_row,parent,false);
                lViewHolder = new AlbumNotifications(lView4);
                break;
        }

        return lViewHolder;
    }

        @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (whichRecycler){
            case 1:
                AlbumsLinearViewHolder lAlbums = (AlbumsLinearViewHolder) holder;
                configureAlbumViewHolder(lAlbums, position);
                break;

            case 2:
                PhotosGridViewHolder lPhotos = (PhotosGridViewHolder) holder;
                configurePhotoViewHolder(lPhotos, position);
                break;

            case 3:
                UsersLinearViewHolder lUsers = (UsersLinearViewHolder) holder;
                configureUserViewHolder(lUsers, position);
                break;

            case 4:
                AlbumNotifications lAlbumNotifications = (AlbumNotifications) holder;
                configureAlbumNotificationsViewHolder(lAlbumNotifications,position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (whichRecycler){
            case 1:
            case 4:
                return fAlbumsForDisplay.size();
            case 2:
                return fPhotosForDisplay.size();
            case 3:
                return fUsersForDisplay.size();

        }
        return 0;
    }

    //Display User Albums
    private void configureAlbumViewHolder(final AlbumsLinearViewHolder lAlbums, final int position) {
        final Bitmap lAlbumBitmap = fAlbumsForDisplay.get(position).getAlbumImage();
        final String lAlbumString = fAlbumsForDisplay.get(position).getAlbumName();
        String lAlbumOwner = fAlbumsForDisplay.get(position).getOwnerName();
        String lAlbumPrivacy = fAlbumsForDisplay.get(position).getPrivacy();

        lAlbums.lAlbumName.setText(lAlbumString);
        lAlbums.lAlbumOwnerText.setText(lAlbumOwner);
        lAlbums.lAlbumPrivacyText.setText(lAlbumPrivacy);

        if (!ParseUser.getCurrentUser().getString(fNAME).equals(lAlbumOwner))
            lAlbums.lAlbumDelete.setImageBitmap(null);
        else{
            lAlbums.lAlbumDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder lConfirmDelete = new AlertDialog.Builder(fContext);
                    lConfirmDelete.setMessage("Are you sure to delete this album?");
                    lConfirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseQuery<ParseObject> lFindRows = ParseQuery.getQuery(fALBUM);
                            lFindRows.include(fOWNER);
                            lFindRows.whereEqualTo(fOWNER, fCURRENT_USER);
                            lFindRows.whereEqualTo(fNAME, lAlbumString);

                            lFindRows.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    final ParseObject lAlbumId = objects.get(0);
                                    ParseQuery<ParseObject> lDeletePhotos = ParseQuery.getQuery("Photos");
                                    lDeletePhotos.whereEqualTo(fALBUM_COLUMN, lAlbumId);
                                    lDeletePhotos.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                for (ParseObject photoObjects : objects) {
                                                    photoObjects.deleteEventually();
                                                }
                                            }
                                        }
                                    });

                                    lAlbumId.deleteEventually();
                                    fAlbumsForDisplay.remove(position);
                                    notifyDataSetChanged();


                                }
                            });
                        }
                    });
                    lConfirmDelete.setNegativeButton("No", null);
                    AlertDialog lDeleteAlbum = lConfirmDelete.create();
                    lDeleteAlbum.show();
                }
            });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (lAlbumBitmap==null) {
                    lAlbums.lAlbumImage.setImageResource(R.drawable.no_mage);
                }
                else{

                          lAlbums.lAlbumImage.setImageBitmap(lAlbumBitmap);
                      }

                }
        }).run();

        lAlbums.lAlbumRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivityFromAlbumList(fGOTO_ALBUM_VIEW, lAlbumString);
            }
        });
    }

    //Display Album photos
    private void configurePhotoViewHolder(final PhotosGridViewHolder lPhotos, final int position) {
        final Bitmap lPhotoBitmap = fPhotosForDisplay.get(position).getPhotoBitmap();
        final String lPhotoString = fPhotosForDisplay.get(position).getPhotoName();

        lPhotos.lPhotoName.setText(lPhotoString);
        new Thread(new Runnable() {
            @Override
            public void run() {
                lPhotos.lPhoto.setImageBitmap(lPhotoBitmap);
            }
        }).run();


        if(fAlbumOwner.equals(fCURRENT_USER)){
            lPhotos.lPhotoGridLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AlertDialog.Builder lConfirmDelete = new AlertDialog.Builder(fContext);
                    lConfirmDelete.setMessage("Are you sure to delete this photo?");
                    lConfirmDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseQuery<ParseObject> lFindRows = ParseQuery.getQuery("Photos");
                            lFindRows.include(fALBUM_COLUMN);
                            lFindRows.whereEqualTo(fNAME, lPhotoString);

                            lFindRows.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {

                                    if(e==null){
                                        for(ParseObject object : objects){
                                            if(object.getParseObject(fALBUM_COLUMN).getString(fNAME).equals(fAlbumName)){
                                                ParseObject.createWithoutData("Photos",object.getObjectId()).deleteEventually();
                                                fPhotosForDisplay.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    }

                                }
                            });
                        }
                    });
                    lConfirmDelete.setNegativeButton("No", null);
                    AlertDialog lDeleteAlbum = lConfirmDelete.create();
                    lDeleteAlbum.show();

                    return true;
                }
            });
        }
    }

    //Display User list
    private void configureUserViewHolder(UsersLinearViewHolder lUsers, final int position) {
        Bitmap lPhotoBitmap = fUsersForDisplay.get(position).getUserImage();
        String lUserName = fUsersForDisplay.get(position).getUserName();
        String lUserEmail = fUsersForDisplay.get(position).getUserMail();

        lUsers.lUserName.setText(lUserName);
        lUsers.lUserEmail.setText(lUserEmail);

        if (lPhotoBitmap==null)
            lUsers.lUserPhoto.setImageResource(R.drawable.no_mage);
        else lUsers.lUserPhoto.setImageBitmap(lPhotoBitmap);

        lUsers.lUserRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fwhichActivity == 2) {
                    Intent lIntent = new Intent();
                    lIntent.putExtra("toField", fUsersForDisplay.get(position).getUserMail());
                    ((UserDirectory) fContext).setResult(Activity.RESULT_OK, lIntent);
                    ((UserDirectory) fContext).finish();

                } else {
                    //TODO goto other users profile and their albums
                }
            }
        });
    }

    //Display Album Notifications
    private void configureAlbumNotificationsViewHolder(AlbumNotifications aAlbumNotifications, final int position){
        Bitmap lAlbumBitmap = fAlbumsForDisplay.get(position).getAlbumImage();
        final String lAlbumString = fAlbumsForDisplay.get(position).getAlbumName();
        String lAlbumOwner = fAlbumsForDisplay.get(position).getOwnerName();

        if(lAlbumBitmap == null){
            aAlbumNotifications.lAlbumLogo.setImageResource(R.drawable.no_mage);
        }else aAlbumNotifications.lAlbumLogo.setImageBitmap(lAlbumBitmap);
        aAlbumNotifications.lAlbumShareMsg.setText(lAlbumOwner + " shared an album with you");

        aAlbumNotifications.lAlbumNotificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivityFromAlbumNotificationsList(fGOTO_ALBUM_VIEW, lAlbumString);
            }
        });

        aAlbumNotifications.lReject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> lGetAlbumId = ParseQuery.getQuery(fALBUM);
                lGetAlbumId.whereEqualTo(fNAME, lAlbumString);
                lGetAlbumId.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            deleteFromNotification(objects.get(0),position);
                        }
                    }
                });

            }
        });

        aAlbumNotifications.lAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> lGetAlbumId = ParseQuery.getQuery(fALBUM);
                lGetAlbumId.whereEqualTo(fNAME, lAlbumString);
                lGetAlbumId.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            ParseObject lGetAlbum = objects.get(0);

                            ParseObject lAcceptInvite = new ParseObject("AlbumShare");
                            lAcceptInvite.put(fALBUM_COLUMN,lGetAlbum);
                            lAcceptInvite.put(fSHARED_WITH,fCURRENT_USER);
                            lAcceptInvite.saveInBackground();

                            lGetAlbum.put(fPRIVACY, "Shared");
                            lGetAlbum.saveInBackground();

                            deleteFromNotification(lGetAlbum,position);
                        }
                    }
                });
            }
        });
    }

    public void toActivityFromAlbumList(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("ALBUM_TITLE", aExtra);
        ((AlbumsList)fContext ).startActivityForResult(lIntent, AlbumsList.fCHECK_EDIT_ALBUM);
    }

    public void toActivityFromAlbumNotificationsList(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("ALBUM_TITLE", aExtra);
        ((Notifications)fContext ).startActivity(lIntent);
    }

    public void deleteFromNotification(ParseObject lObject, final int position){
        ParseQuery<ParseObject> lRejectInvite = new ParseQuery<ParseObject>(fNOTIFICATIONS);
        lRejectInvite.include(fTOUSER);
        lRejectInvite.include(fALBUM_COLUMN);
        lRejectInvite.whereEqualTo(fTOUSER, fCURRENT_USER);
        lRejectInvite.whereEqualTo(fALBUM_COLUMN, lObject);
        lRejectInvite.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects) {
                        object.deleteEventually();
                        fAlbumsForDisplay.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }
        });
    }
}