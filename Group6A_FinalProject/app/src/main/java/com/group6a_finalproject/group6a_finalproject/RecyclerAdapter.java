package com.group6a_finalproject.group6a_finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.group6a_finalproject.group6a_finalproject.R.id.textViewDirectoryUserEmail;


/**
 * Created by Arunkumar's on 11/19/2015.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<Photo> fPhotosForDisplay;
    ArrayList<User> fUsersForDisplay;
    Context fContext;
    int whichRecycler;
    int fwhichActivity;


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


    //To display a list of albums
    public static class AlbumsLinearViewHolder extends RecyclerView.ViewHolder{
        TextView lAlbumName;
        ImageView lAlbumImage;

        public AlbumsLinearViewHolder(View itemView) {
            super(itemView);

            lAlbumImage = (ImageView) itemView.findViewById(R.id.imageViewRecyclerAlbumImage);
            lAlbumName = (TextView) itemView.findViewById(R.id.textViewRecylclerAlbumName);
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
            case 2:
                return fPhotosForDisplay.size();
            case 3:
                return fUsersForDisplay.size();

        }
        return 0;
    }

    private void configureAlbumViewHolder(AlbumsLinearViewHolder lAlbums, int position) {
        Bitmap lAlbumBitmap = fPhotosForDisplay.get(position).getPhotoBitmap();
        String lAlbumString = fPhotosForDisplay.get(position).getPhotoName();

        lAlbums.lAlbumName.setText(lAlbumString);
        if (lAlbumBitmap==null) {
            lAlbums.lAlbumImage.setImageResource(R.drawable.no_mage);
        }
        else lAlbums.lAlbumImage.setImageBitmap(lAlbumBitmap);
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
                if (fwhichActivity == 2){
                    Intent lIntent = new Intent();
                    lIntent.putExtra("username",fUsersForDisplay.get(position).getUserName());
                    ((UserDirectory) fContext).setResult(3001, lIntent);
                    ((UserDirectory) fContext).finish();

                }else{
                    //TODO goto other users profile and their albums
                }
            }
        });
    }
}