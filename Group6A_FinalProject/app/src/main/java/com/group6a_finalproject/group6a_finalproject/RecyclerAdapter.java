package com.group6a_finalproject.group6a_finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PhotosGridViewHolder>{

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

//        Picasso.with(fContext).load(lPhotoBitmap)
//                .resize(40, 40).into(holder.lPhoto);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
