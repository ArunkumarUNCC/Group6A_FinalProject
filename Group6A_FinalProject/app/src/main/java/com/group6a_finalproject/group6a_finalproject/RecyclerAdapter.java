package com.group6a_finalproject.group6a_finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Arunkumar's on 11/19/2015.
 */
/*public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PhotosGridViewHolder>{

    ArrayList<Photo> fPhotosForDisplay;
    Context fContext;

    public RecyclerAdapter(ArrayList<Photo> fPhotosForDisplay, Context fContext) {
        this.fPhotosForDisplay = fPhotosForDisplay;
        this.fContext = fContext;
    }

    public static class PhotosGridViewHolder extends RecyclerView.ViewHolder{

        TextView lPhotoName;
        ImageView lPhoto;

        public PhotosGridViewHolder(View itemView) {
            super(itemView);

            lPhotoName = (TextView) itemView.findViewById(R.id.textViewPhotoName);
            lPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
        }
    }

    @Override
    public PhotosGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_grid, parent, false);
        PhotosGridViewHolder lPhotosView = new PhotosGridViewHolder(lView);
        return lPhotosView;
    }

    @Override
    public void onBindViewHolder(PhotosGridViewHolder holder, int position) {
        Bitmap lPhotoBitmap = fPhotosForDisplay.get(position).getPhotoBitmap();
        String lPhotoString = fPhotosForDisplay.get(position).getPhotoName();

        holder.lPhotoName.setText(lPhotoString);
        holder.lPhoto.setImageBitmap(lPhotoBitmap);
    }

    @Override
    public int getItemCount() {
        return fPhotosForDisplay.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}*/


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<Photo> fPhotosForDisplay;
    Context fContext;
    int whichRecycler;

    public RecyclerAdapter(ArrayList<Photo> fPhotosForDisplay, Context fContext, int which) {
        this.fPhotosForDisplay = fPhotosForDisplay;
        this.fContext = fContext;
        this.whichRecycler = which;
    }

    public static class PhotosGridViewHolder extends RecyclerView.ViewHolder{

        TextView lPhotoName;
        ImageView lPhoto;

        public PhotosGridViewHolder(View itemView) {
            super(itemView);

            lPhotoName = (TextView) itemView.findViewById(R.id.textViewPhotoName);
            lPhoto = (ImageView) itemView.findViewById(R.id.imageViewPhoto);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder lViewHolder = null;
        LayoutInflater lInflater = LayoutInflater.from(parent.getContext());

        switch (whichRecycler){
            case 2:
                View lView1 = lInflater.inflate(R.layout.photo_grid, parent, false);
                lViewHolder = new PhotosGridViewHolder(lView1);
                break;

        }

        return lViewHolder;
    }

        @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (whichRecycler){
            case 2:
                PhotosGridViewHolder lPhotos = (PhotosGridViewHolder) holder;
                configureViewHolder1(lPhotos,position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (whichRecycler){
            case 2:
                return fPhotosForDisplay.size();

        }
        return 0;
    }

    private void configureViewHolder1(PhotosGridViewHolder lPhotos, int position) {
        Bitmap lPhotoBitmap = fPhotosForDisplay.get(position).getPhotoBitmap();
        String lPhotoString = fPhotosForDisplay.get(position).getPhotoName();

        lPhotos.lPhotoName.setText(lPhotoString);
        lPhotos.lPhoto.setImageBitmap(lPhotoBitmap);
    }
}