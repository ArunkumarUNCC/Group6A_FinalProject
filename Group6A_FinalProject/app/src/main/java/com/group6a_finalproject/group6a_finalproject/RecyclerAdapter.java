package com.group6a_finalproject.group6a_finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.group6a_finalproject.group6a_finalproject.R.id.textViewDirectoryUserEmail;
import static com.group6a_finalproject.group6a_finalproject.R.id.textViewRecyclerPrivacy;


/**
 * Created by Arunkumar's on 11/19/2015.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    final String fGOTO_ALBUM_VIEW = "android.intent.action.ALBUM_VIEW";
    final String fGOTO_ALBUM_LIST = "android.intent.action.ALBUM_LIST";
    final String fGOTO_EDIT_SHARED_PHOTO = "android.intent.action.EDIT_SHARED_PHOTO";
    final String fGOTOPHOTO_SLIDER = "android.intent.action.PHOTO_SLIDER";
    final String fNOTIFICATIONS = "Notifications";
    final String fTOUSER = "toUser";
    final String fFROMUSER = "fromUser";
    final String fSHARED_WITH = "sharedWith";
    final String fALBUM_COLUMN = "album";
    final String fALBUM = "Album";
    final String fNAME = "name";
    final String fOWNER = "owner";
    final String fPRIVACY = "privacy";
    final String fTHUMBNAIL = "thumbnail";
    final ParseUser fCURRENT_USER = ParseUser.getCurrentUser();

    ArrayList<Photo> fPhotosForDisplay;
    ArrayList<User> fUsersForDisplay;
    ArrayList<Album> fAlbumsForDisplay;
    ArrayList<String> fPhotoIds;
    Context fContext;
    int whichRecycler;
    int fwhichActivity;
    String fAlbumName;
    boolean fCanExpand = false;

    ParseUser fAlbumOwner;

    public RecyclerAdapter(Context fContext, int which) {
        this.fContext = fContext;
        this.whichRecycler = which;
    }

    public RecyclerAdapter(ArrayList<Photo> albumPhotoList, Context fContext, int which,ParseUser aAlbumOwner,String aAlbumName) {
        this(fContext,which);
        this.fPhotosForDisplay = albumPhotoList;
        this.fAlbumOwner = aAlbumOwner;
        this.fAlbumName = aAlbumName;
    }

    public RecyclerAdapter(ArrayList<User> userList, Context fContext, int which, int aWhichActivity) {
        this(fContext,which);
        this.fUsersForDisplay = userList;
        this.fwhichActivity = aWhichActivity;
    }

    public RecyclerAdapter(Context fContext, int which, ArrayList<String> aIds){
        this(fContext,which);
        this.fPhotoIds = aIds;
    }

    public void setAlbumsList(ArrayList<Album> aAlbumsList){
        this.fAlbumsForDisplay = aAlbumsList;
    }

    //To display a list of albums
    public static class AlbumsLinearViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout lAlbumRelativeLayout;
        TextView lAlbumName,lAlbumOwnerText,lAlbumPrivacyText;
        ImageView lAlbumDelete;
        ParseImageView lAlbumImage;

        public AlbumsLinearViewHolder(View itemView) {
            super(itemView);

            lAlbumRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutAlbumList);
            lAlbumImage = (ParseImageView) itemView.findViewById(R.id.imageViewRecyclerAlbumImage);
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
        ParseImageView lPhoto;

        public PhotosGridViewHolder(View itemView) {
            super(itemView);

            lPhotoGridLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutPhotoGrid);
            lPhotoName = (TextView) itemView.findViewById(R.id.textViewPhotoName);
            lPhoto = (ParseImageView) itemView.findViewById(R.id.imageViewPhoto);
        }
    }

    //To display user list
    public static class UsersLinearViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout lUserRelativeLayout;

        TextView lUserName,lUserEmail;
        ParseImageView lUserPhoto;

        public UsersLinearViewHolder(View itemView) {
            super(itemView);

            lUserRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutUserRecycler);
            lUserName = (TextView) itemView.findViewById(R.id.textViewDirectoryUserName);
            lUserEmail = (TextView) itemView.findViewById(textViewDirectoryUserEmail);
            lUserPhoto = (ParseImageView) itemView.findViewById(R.id.imageViewDirectoryThumb);
        }
    }

    //To display album notifications
    public static class AlbumNotifications extends RecyclerView.ViewHolder{
        LinearLayout lAlbumNotificationLayout;
        ImageView lAccept,lReject;
        ParseImageView lAlbumLogo;
        TextView lAlbumShareMsg;

        public AlbumNotifications(View itemView) {
            super(itemView);

            lAlbumNotificationLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutAlbumRow);
            lAlbumLogo = (ParseImageView) itemView.findViewById(R.id.imageViewAlbumNotification);
            lAccept = (ImageView) itemView.findViewById(R.id.imageViewAccept);
            lReject = (ImageView) itemView.findViewById(R.id.imageViewReject);
            lAlbumShareMsg = (TextView) itemView.findViewById(R.id.textViewAlbumNotification);
        }
    }

    //To display photo notifications
    public static class PhotoNotifications extends RecyclerView.ViewHolder{
        RelativeLayout lPhotoNotificationLayout;
        ParseImageView lPhotoThumbnail;
        TextView lMsgText;

        public PhotoNotifications(View itemView) {
            super(itemView);

            lPhotoNotificationLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutPhotoRow);
            lPhotoThumbnail = (ParseImageView) itemView.findViewById(R.id.imageViewPhotoNotification);
            lMsgText = (TextView) itemView.findViewById(R.id.textViewPhotoNotification);
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

            case 5:
                View lView5 = lInflater.inflate(R.layout.photo_notification_row,parent,false);
                lViewHolder = new PhotoNotifications(lView5);
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

            case 5:
                PhotoNotifications lPhotoNotifications = (PhotoNotifications) holder;
                configurePhotoNotificationsViewHolder(lPhotoNotifications,position);
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
            case 5:
                return fPhotoIds.size();
        }
        return 0;
    }

    //Display User Albums
    private void configureAlbumViewHolder(final AlbumsLinearViewHolder lAlbums, final int position) {
        final ParseFile lAlbumBitmap = fAlbumsForDisplay.get(position).getAlbumImage();
        final String lAlbumString = fAlbumsForDisplay.get(position).getAlbumName();
        String lAlbumOwner = fAlbumsForDisplay.get(position).getOwnerName();
        String lAlbumPrivacy = fAlbumsForDisplay.get(position).getPrivacy();

        lAlbums.lAlbumName.setText(lAlbumString);
        lAlbums.lAlbumOwnerText.setText(lAlbumOwner);
        lAlbums.lAlbumPrivacyText.setText(lAlbumPrivacy);

        if (!ParseUser.getCurrentUser().getString(fNAME).equals(lAlbumOwner))
            lAlbums.lAlbumDelete.setImageBitmap(null);
        else{
            Picasso.with(fContext).load(android.R.drawable.ic_delete).into(lAlbums.lAlbumDelete);
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

                                    ParseQuery<ParseObject> lDeleteNotifications = ParseQuery.getQuery("Notifications");
                                    lDeleteNotifications.whereEqualTo(fALBUM_COLUMN, lAlbumId);
                                    lDeleteNotifications.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                for (ParseObject notificationObjects : objects) {
                                                    notificationObjects.deleteEventually();
                                                }
                                            }
                                        }
                                    });

                                    ParseQuery<ParseObject> lDeleteShared = ParseQuery.getQuery("AlbumShare");
                                    lDeleteShared.whereEqualTo(fALBUM_COLUMN, lAlbumId);
                                    lDeleteShared.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                for (ParseObject sharedObjects : objects) {
                                                    sharedObjects.deleteEventually();
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
                    Picasso.with(fContext).load(R.drawable.no_mage).into(lAlbums.lAlbumImage);
//                    lAlbums.lAlbumImage.setImageResource(R.drawable.no_mage);
                }
                else{
                    lAlbums.lAlbumImage.setParseFile(lAlbumBitmap);
                    lAlbums.lAlbumImage.setScaleType(ParseImageView.ScaleType.FIT_XY);
                    lAlbums.lAlbumImage.loadInBackground();
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
        final ParseFile lPhotoBitmap = fPhotosForDisplay.get(position).getPhotoBitmap();
        final String lPhotoString = fPhotosForDisplay.get(position).getPhotoName();
        final String lPhotoId = fPhotosForDisplay.get(position).getObjectId();

        lPhotos.lPhotoName.setText(lPhotoString);
        new Thread(new Runnable() {
            @Override
            public void run() {
                lPhotos.lPhoto.setParseFile(lPhotoBitmap);
                lPhotos.lPhoto.loadInBackground();
            }
        }).run();

        lPhotos.lPhotoGridLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlbumActivity.manageSlider(position);

            }
        });

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
                            lFindRows.whereEqualTo("objectId", lPhotoId);

                            lFindRows.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {

                                    if(e==null){
                                        for(ParseObject object : objects){
                                            if(object.getParseObject(fALBUM_COLUMN).getString(fNAME).equals(fAlbumName)){
                                                ParseObject.createWithoutData("Photos",object.getObjectId()).deleteEventually();
                                                fPhotosForDisplay.remove(position);
                                                notifyDataSetChanged();
                                                AlbumActivity.fPagerAdapter.notifyDataSetChanged();
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
        ParseFile lPhotoBitmap = fUsersForDisplay.get(position).getUserImage();
        String lUserName = fUsersForDisplay.get(position).getUserName();
        final String lUserEmail = fUsersForDisplay.get(position).getUserMail();

        lUsers.lUserName.setText(lUserName);
        lUsers.lUserEmail.setText(lUserEmail);

        if (lPhotoBitmap==null)
            lUsers.lUserPhoto.setImageResource(R.drawable.no_mage);
        else{
            lUsers.lUserPhoto.setParseFile(lPhotoBitmap);
            lUsers.lUserPhoto.loadInBackground();
            lUsers.lUserPhoto.setScaleType(ParseImageView.ScaleType.FIT_XY);
        }

        lUsers.lUserRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fwhichActivity == 2) {
                    Intent lIntent = new Intent();
                    lIntent.putExtra("toField", fUsersForDisplay.get(position).getUserMail());
                    ((UserDirectory) fContext).setResult(Activity.RESULT_OK, lIntent);
                    ((UserDirectory) fContext).finish();

                } else {
                    toActivity(fGOTO_ALBUM_LIST, 2, lUserEmail);
                }
            }
        });
    }

    //Display Album Notifications
    private void configureAlbumNotificationsViewHolder(AlbumNotifications aAlbumNotifications, final int position){
        ParseFile lAlbumBitmap = fAlbumsForDisplay.get(position).getAlbumImage();
        final String lAlbumString = fAlbumsForDisplay.get(position).getAlbumName();
        String lAlbumOwner = fAlbumsForDisplay.get(position).getOwnerName();

        if(lAlbumBitmap == null){
            aAlbumNotifications.lAlbumLogo.setImageResource(R.drawable.no_mage);
        }else{
            aAlbumNotifications.lAlbumLogo.setParseFile(lAlbumBitmap);
            aAlbumNotifications.lAlbumLogo.loadInBackground();
        }
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
                            deleteFromNotification(objects.get(0), position);
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
                            lAcceptInvite.put(fALBUM_COLUMN, lGetAlbum);
                            lAcceptInvite.put(fSHARED_WITH, fCURRENT_USER);
                            lAcceptInvite.saveInBackground();

                            lGetAlbum.put("isShared", true);
                            lGetAlbum.saveInBackground();

                            deleteFromNotification(lGetAlbum, position);
                        }
                    }
                });
            }
        });
    }

    //Display Photo Notifications
    private void configurePhotoNotificationsViewHolder(final PhotoNotifications aPhotoNotifications, final int position){
        final String lCurrrentId = fPhotoIds.get(position);
        final TextView lMsgText = aPhotoNotifications.lMsgText;
        final ParseImageView lPhotoThumbnail = aPhotoNotifications.lPhotoThumbnail;
        byte[] lParseImage;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> lGetNotifications = ParseQuery.getQuery(fNOTIFICATIONS);
                lGetNotifications.include(fFROMUSER);
                lGetNotifications.include(fALBUM_COLUMN);
                lGetNotifications.include(fALBUM_COLUMN);
                lGetNotifications.whereEqualTo("objectId", lCurrrentId);
                lGetNotifications.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null){
                            final ParseObject lObject = objects.get(0);
                            final ParseFile lPhotoParseFile = lObject.getParseFile(fTHUMBNAIL);
                            ParseObject lAbumObject = lObject.getParseObject(fALBUM_COLUMN);
                            ParseUser lFrom = lObject.getParseUser(fFROMUSER);

                            final String lPhotoTitle = lObject.getString(fNAME);
                            final String lPhotoOwner = lFrom.getString(fNAME);

                            if(lPhotoParseFile != null){
                                lPhotoThumbnail.setParseFile(lPhotoParseFile);
                                lPhotoThumbnail.loadInBackground();
                                lPhotoThumbnail.setScaleType(ParseImageView.ScaleType.FIT_XY);
                            }

                            lMsgText.setText(lPhotoOwner + " created a new photo in " + lAbumObject.getString(fNAME));

                            aPhotoNotifications.lPhotoNotificationLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent lIntent = new Intent(fGOTO_EDIT_SHARED_PHOTO);
                                    lIntent.putExtra("photoId", lObject.getObjectId());
                                    lIntent.putExtra("photoPosition",position);
                                    ((Notifications)fContext).startActivityForResult(lIntent, Notifications.fEDIT_PHOTO);
                                }
                            });
                        }
                    }
                });
            }
        }).run();

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

    //To start View Albums
    public void toActivity(String aIntent, int aExtra, String aEmail){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("album_flag", aExtra);
        lIntent.putExtra("current_user",aEmail);
        fContext.startActivity(lIntent);
    }
}