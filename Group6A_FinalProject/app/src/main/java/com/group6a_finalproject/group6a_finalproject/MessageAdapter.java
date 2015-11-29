package com.group6a_finalproject.group6a_finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
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

    public MessageAdapter(ArrayList<Messages> aUserMessages, Context aContext) {
        this.fUserMessages = aUserMessages;
        this.fContext = aContext;
    }

    public static class MessageLinearHolder extends RecyclerView.ViewHolder {

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
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
        MessageLinearHolder lMessageView = new MessageLinearHolder(lView);
        return lMessageView;
    }

    @Override
    public void onBindViewHolder(MessageLinearHolder holder, final int position) {
//        Bitmap lUserIcon = fUserMessages.get(position).getUserIcon();
        String lFromField = fUserMessages.get(position).getFromField();
        String lMessagePreview = fUserMessages.get(position).getMessageBody();

//        holder.lUserIcon.setImageBitmap(lUserIcon);
        holder.lFromField.setText(lFromField);
        holder.lMessageBody.setText(lMessagePreview);
        holder.lMessageBody.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder lConfirmDelete = new AlertDialog.Builder(fContext);
                lConfirmDelete.setMessage("Are you sure you want to delete this message?")
                    .setTitle("Delete message?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //delete selected messages
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


    @Override
    public int getItemCount() {
        return fUserMessages.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
