package com.group6a_finalproject.group6a_finalproject;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseImageView;

import java.util.ArrayList;

/**
 * Created by Arunkumar's on 12/4/2015.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Context fContext;
    private LayoutInflater fLayoutInflator;
    private ArrayList<Photo> fPhotos;

    public ViewPagerAdapter(Context fContext,ArrayList<Photo> aPhotos) {
        this.fContext = fContext;
        this.fPhotos = aPhotos;
    }

    @Override
    public int getCount() {
        return fPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Photo photo = fPhotos.get(position);

        fLayoutInflator = (LayoutInflater) fContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View photoView = fLayoutInflator.inflate(R.layout.swipe_layout,container,false);

        ParseImageView lParseImageView = (ParseImageView) photoView.findViewById(R.id.imageViewSlider);
        TextView lPhotoTitle = (TextView) photoView.findViewById(R.id.textViewPhotoTitle);

        lParseImageView.setParseFile(photo.getPhotoBitmap());
        lParseImageView.loadInBackground();
        lParseImageView.setScaleType(ParseImageView.ScaleType.FIT_XY);
        container.addView(photoView);

        lPhotoTitle.setText(photo.getPhotoName());
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
