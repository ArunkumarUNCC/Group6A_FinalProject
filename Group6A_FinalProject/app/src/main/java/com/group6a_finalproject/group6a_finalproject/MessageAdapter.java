package com.group6a_finalproject.group6a_finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageLinearHolder> {

    ArrayList<Messages> fUserMessages;
    Context fContext;
    final String fGOTO_VIEW_MESSAGE = "android.intent.action.VIEW_MESSAGE";

    public MessageAdapter(ArrayList<Messages> aUserMessages, Context aContext) {
        this.fUserMessages = aUserMessages;
        this.fContext = aContext;
    }

    public static class MessageLinearHolder extends RecyclerView.ViewHolder {

        ParseImageView lUserIcon;
        TextView lFromField, lMessageBody, lTimeStamp;

        public MessageLinearHolder(View itemView) {
            super(itemView);
            lUserIcon = (ParseImageView) itemView.findViewById(R.id.imageViewInboxThumb);
            lFromField = (TextView) itemView.findViewById(R.id.textViewInboxName);
            lMessageBody = (TextView) itemView.findViewById(R.id.textViewDirectoryInboxMessagePreview);
            lTimeStamp = (TextView) itemView.findViewById(R.id.textViewInboxTimeStamp);
        }
    }

    @Override
    public MessageLinearHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
        MessageLinearHolder lMessageView = new MessageLinearHolder(lView);
        return lMessageView;
    }

    @Override
    public void onBindViewHolder(MessageLinearHolder holder, final int position) {
        ParseFile lIcon = null;
        try {
            lIcon = fUserMessages.get(position).getUserIcon();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String lFromField = fUserMessages.get(position).getFromField();
        String lMessagePreview = fUserMessages.get(position).getMessageBody();

        if(lIcon != null){
            holder.lUserIcon.setParseFile(lIcon);
            holder.lUserIcon.loadInBackground();
            holder.lUserIcon.setScaleType(ParseImageView.ScaleType.CENTER_CROP);
        }
        holder.lFromField.setText("From: " + lFromField);
        holder.lMessageBody.setText(lMessagePreview);
        holder.lTimeStamp.setText(fUserMessages.get(position).getTimeStamp().toString());

       ParseQuery<ParseObject> lReadQuery = ParseQuery.getQuery("MessageTable")
               .whereEqualTo("objectId", fUserMessages.get(position).getObjectID());
        try {//issue is in the try block
            ParseObject lIsRead = lReadQuery.getFirst();
            if(lIsRead.getBoolean("Read")){
                holder.itemView.setBackgroundColor(Color.parseColor("#D3D3D3"));
//                notifyDataSetChanged();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set message to read
                ParseQuery<ParseObject> lQuery = ParseQuery.getQuery("MessageTable")
                        .whereEqualTo("objectId", fUserMessages.get(position).getObjectID());
                try {
                    List<ParseObject> lMessage = lQuery.find();
                    for(ParseObject message : lMessage){
                        message.put("Read", true);
                        notifyDataSetChanged();
                        message.save();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //send message to message view
                toActivity(fGOTO_VIEW_MESSAGE, fUserMessages.get(position));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder lConfirmDelete = new AlertDialog.Builder(fContext);
                lConfirmDelete.setMessage("Are you sure you want to delete this message?")
                    .setTitle("Delete message?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //delete selected message
                            ParseQuery<ParseObject> lFindRow = ParseQuery.getQuery("MessageTable");
                            lFindRow.whereEqualTo("objectId", fUserMessages.get(position).getObjectID())
                                    .findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            ParseObject.createWithoutData("MessageTable", objects.get(0).getObjectId()).deleteEventually();
                                            fUserMessages.remove(position);
                                            notifyDataSetChanged();
                                        }
                                    });
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
                return true;
            }
        });
    }

    public void toActivity(String aIntent, Messages aMessage){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("Message", aMessage);
        fContext.startActivity(lIntent);
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
