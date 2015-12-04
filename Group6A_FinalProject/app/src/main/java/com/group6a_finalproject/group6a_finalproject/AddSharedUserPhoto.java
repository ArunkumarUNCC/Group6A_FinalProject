package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AddSharedUserPhoto extends AppCompatActivity {

    final String fNOTIFICATIONS = "Notifications";
    final String fFROMUSER = "fromUser";
    final String fALBUM_COLUMN = "album";
    final String fNAME = "name";
    final String fTHUMBNAIL = "thumbnail";

    EditText fSharedPhotoTitle;
    ParseImageView fSharedPhotoThumbnail;
    TextView fAddedBy,fAddedTo;
    Button fButtonDelete,fButtonSave,fButtonCancel;

    String fPhotoId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shared_user_photo);

        getItems();
        setItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_shared_user_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getItems() {
        fSharedPhotoTitle = (EditText) findViewById(R.id.editTextSharedPhotoTitle);
        fSharedPhotoThumbnail = (ParseImageView) findViewById(R.id.imageViewSharedPhotoThumbnail);
        fAddedBy = (TextView) findViewById(R.id.textViewAddedBy);
        fAddedTo = (TextView) findViewById(R.id.textViewAddedTo);
        fButtonDelete = (Button) findViewById(R.id.buttonDeleteSharedPhoto);
        fButtonSave = (Button) findViewById(R.id.buttonSaveSharedPhoto);
        fButtonCancel = (Button) findViewById(R.id.buttonCancelSharing);

        fPhotoId = getIntent().getStringExtra("photoId");
    }

    private void setItems() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> lGetNotifications = ParseQuery.getQuery(fNOTIFICATIONS);
                lGetNotifications.include(fFROMUSER);
                lGetNotifications.include(fALBUM_COLUMN);
                lGetNotifications.include(fALBUM_COLUMN);
                lGetNotifications.whereEqualTo("objectId", fPhotoId);
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

                            fSharedPhotoTitle.setText(lPhotoTitle);
                            if(lPhotoParseFile != null){
                                fSharedPhotoThumbnail.setParseFile(lPhotoParseFile);
                                fSharedPhotoThumbnail.loadInBackground();
                                fSharedPhotoThumbnail.setScaleType(ParseImageView.ScaleType.FIT_XY);
                            }
                            fAddedBy.setText("Added By:" + lPhotoOwner);
                            fAddedTo.setText("Added To:"+lAbumObject.getString(fNAME));

                        }
                    }
                });
            }
        }).run();
    }

    public void deleteSharedOnClick(View aView){}

    public void saveSharedOnClick(View aView){}

    public void cancelSharedOnClick(View aView){
        finish();
    }
}
