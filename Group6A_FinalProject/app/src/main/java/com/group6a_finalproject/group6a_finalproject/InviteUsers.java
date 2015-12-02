package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class InviteUsers extends AppCompatActivity {

    final String fGOTO_USER_DIRECTORY = "android.intent.action.USER_DIRECTORY";
    final String fALBUMSNOTIFICATIONS = "Notifications";
    final String fFROMUSER = "fromUser";
    final String fTOUSER = "toUser";
    final String fALBUM = "album";

    final int fUSER_DIR_REQCODE = 1500;

    TextView fSharelabel;
    EditText fShareWith;
    ImageView fDeleteUser;
    Button fShare,fCancel;

    String fAlbumName;
    ParseUser fShareWithUser;
    ParseObject fShareObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_users);

        getItems();
        setItems();

        fShareWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toActivityForResult(fGOTO_USER_DIRECTORY, fUSER_DIR_REQCODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invite_users, menu);
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
        fAlbumName = getIntent().getStringExtra("ALBUM_NAME");

        fSharelabel = (TextView) findViewById(R.id.textViewShare);
        fShareWith = (EditText) findViewById(R.id.editTextShare);
        fDeleteUser = (ImageView) findViewById(R.id.imageViewRemoveUser);
        fShare = (Button) findViewById(R.id.buttonShare);
        fCancel = (Button) findViewById(R.id.buttonShareCancel);

    }

    private void setItems(){
        fSharelabel.setText("Share " + fAlbumName + " with:");

        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> lGetAlbumRef = ParseQuery.getQuery("Album");
                lGetAlbumRef.whereEqualTo("name", fAlbumName);
                try {
                    List<ParseObject> lObjects = lGetAlbumRef.find();
                    fShareObject = lObjects.get(0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void toActivityForResult(String aIntent,int aCode){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("fromCompose", 2);
        lIntent.putExtra("fromShared", true);
        lIntent.putExtra("albumName",fAlbumName);
        startActivityForResult(lIntent, aCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case fUSER_DIR_REQCODE:
                if(resultCode == RESULT_OK){
                    String lUserMail = data.getStringExtra("toField");

                    ParseQuery<ParseUser> lGetUser = ParseQuery.getQuery("_User");
                    lGetUser.whereEqualTo("email",lUserMail);
                    lGetUser.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                fShareWithUser = objects.get(0);
                                fShareWith.setText(fShareWithUser.getString("name"));
                            }
                        }
                    });
                }
                break;
        }
    }

    public void shareButtonOnClick(View aView){
        if(fShareWithUser!=null){
            ParseObject lSaveNotification = new ParseObject(fALBUMSNOTIFICATIONS);
            lSaveNotification.put(fFROMUSER,ParseUser.getCurrentUser());
            lSaveNotification.put(fTOUSER,fShareWithUser);
            lSaveNotification.put(fALBUM,fShareObject);

            lSaveNotification.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null) {
                        makeToast("Notification sent to " + fShareWithUser.getString("name"));
                        finish();
                    }
                }
            });
        }
    }

    public  void shareCancelOnClick(View aView){
        finish();
    }

    public void deleteUserOnClick(View aView){
        fShareWithUser=  null;
        fShareWith.setText("");
    }

    public void makeToast(String aDisplayMessage){
        Toast.makeText(this,aDisplayMessage,Toast.LENGTH_SHORT).show();
    }
}
