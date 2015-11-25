package com.group6a_finalproject.group6a_finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Michael.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageLinearHolder>{

    ArrayList<Messages> fUserMessages;
    Context fContext;

    public MessageAdapter(ArrayList<Messages> aUserMessages, Context aContext){
        this.fUserMessages = aUserMessages;
        this.fContext = aContext;
    }

    public static class MessageLinearHolder extends RecyclerView.ViewHolder{

        ImageView lUserIcon;
        TextView lFromField, lMessageBody;

        public MessageLinearHolder(View itemView) {
            super(itemView);
            lUserIcon = (ImageView) itemView.findViewById(R.id.imageViewInboxThumb);
            lFromField = (TextView) itemView.findViewById(R.id.textViewInboxName);
            lMessageBody = (TextView) itemView.findViewById(R.id.textViewDirectoryInboxMessagePreview);
        }
    }

    @Override
    public MessageLinearHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row,  parent,  false);
        MessageLinearHolder lMessageView = new MessageLinearHolder(lView);
        return lMessageView;
    }

    @Override
    public void onBindViewHolder(MessageLinearHolder holder, int position) {
//        Bitmap lUserIcon = fUserMessages.get(position).getUserIcon();
        String lFromField = fUserMessages.get(position).getFromField();
        String lMessagePreview = fUserMessages.get(position).getMessageBody();

//        holder.lUserIcon.setImageBitmap(lUserIcon);
        holder.lFromField.setText(lFromField);
        holder.lMessageBody.setText(lMessagePreview);
    }

    @Override
    public int getItemCount() {
        return fUserMessages.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
