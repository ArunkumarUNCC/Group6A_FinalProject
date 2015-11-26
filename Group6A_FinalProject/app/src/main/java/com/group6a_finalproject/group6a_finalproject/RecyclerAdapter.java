package com.group6a_finalproject.group6a_finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.ArrayList;

import static com.group6a_finalproject.group6a_finalproject.R.id.textViewDirectoryUserEmail;
import static com.group6a_finalproject.group6a_finalproject.R.id.textViewRecyclerPrivacy;


/**
 * Created by Arunkumar's on 11/19/2015.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    final String fGOTO_ALBUM_VIEW = "android.intent.action.ALBUM_VIEW";

    ArrayList<Photo> fPhotosForDisplay;
    ArrayList<User> fUsersForDisplay;
    ArrayList<Album> fAlbumsForDisplay;
    Context fContext;
    int whichRecycler;
    int fwhichActivity;

    public RecyclerAdapter(Context fContext, int which) {
        this.fContext = fContext;
        this.whichRecycler = which;
    }

    public RecyclerAdapter(ArrayList<Photo> albumPhotoList, Context fContext, int which) {
        this.fPhotosForDisplay = albumPhotoList;
        this.fContext = fContext;
        this.whichRecycler = which;
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

        TextView lPhotoName;
        ImageView lPhoto;

        public PhotosGridViewHolder(View itemView) {
            super(itemView);

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
        }
    }

    @Override
    public int getItemCount() {
        switch (whichRecycler){
            case 1:
                return fAlbumsForDisplay.size();
            case 2:
                return fPhotosForDisplay.size();
            case 3:
                return fUsersForDisplay.size();

        }
        return 0;
    }

    private void configureAlbumViewHolder(AlbumsLinearViewHolder lAlbums, int position) {
        Bitmap lAlbumBitmap = fAlbumsForDisplay.get(position).getAlbumImage();
        final String lAlbumString = fAlbumsForDisplay.get(position).getAlbumName();
        String lAlbumOwner = fAlbumsForDisplay.get(position).getOwnerName();
        String lAlbumPrivacy = fAlbumsForDisplay.get(position).getPrivacy();

        lAlbums.lAlbumName.setText(lAlbumString);
        lAlbums.lAlbumOwnerText.setText(lAlbumOwner);
        lAlbums.lAlbumPrivacyText.setText(lAlbumPrivacy);

        if (!ParseUser.getCurrentUser().getString("name").equals(lAlbumOwner))
            lAlbums.lAlbumDelete.setImageBitmap(null);

        if (lAlbumBitmap==null) {
            lAlbums.lAlbumImage.setImageResource(R.drawable.no_mage);
        }
        else lAlbums.lAlbumImage.setImageBitmap(lAlbumBitmap);

        lAlbums.lAlbumRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivityFromAlbumList(fGOTO_ALBUM_VIEW, lAlbumString);
            }
        });
    }

    private void configurePhotoViewHolder(PhotosGridViewHolder lPhotos, int position) {
        Bitmap lPhotoBitmap = fPhotosForDisplay.get(position).getPhotoBitmap();
        String lPhotoString = fPhotosForDisplay.get(position).getPhotoName();

        lPhotos.lPhotoName.setText(lPhotoString);
        lPhotos.lPhoto.setImageBitmap(lPhotoBitmap);
    }

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

    public void toActivityFromAlbumList(String aIntent, String aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("ALBUM_TITLE", aExtra);
        ((AlbumsList)fContext ).startActivity(lIntent);
    }
}