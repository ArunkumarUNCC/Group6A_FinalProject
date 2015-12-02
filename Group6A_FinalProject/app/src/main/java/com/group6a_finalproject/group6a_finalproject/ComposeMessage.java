package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ComposeMessage extends AppCompatActivity {

    final String fUSER_FROM = "userFrom";
    final String fMESSAGE = "Message";
    final String fUSER_TO = "userTo";
    final String fREAD = "Read";
    final int fTO_USER_DIRECTORY = 1002;
    String fTo_User_Email = "";

    EditText fMessageBody;
    TextView fToField;

    Boolean fFromMessageView;
    Messages fMessage;

    ParseUser fCurrentUser, fReturnUser;
    ParseObject fParseObj = new ParseObject("MessageTable");//String is table name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);

        getItems();

        if(fFromMessageView){
            fReturnUser = getReturnAddress(fMessage);
            fToField.setText("TO: " + fReturnUser.getString("name"));
            fToField.setClickable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose_message, menu);
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

    public void getItems (){
        fMessageBody = (EditText) findViewById(R.id.editTextComposeMessageBody);
        fToField = (TextView) findViewById(R.id.textViewToMessageField);
        fCurrentUser = ParseUser.getCurrentUser();

        fFromMessageView = getIntent().getBooleanExtra("FromView", false);
        fMessage = (Messages) getIntent().getSerializableExtra("Message");
    }

    public void makeToast(String aString){
        Toast.makeText(getApplicationContext(), aString, Toast.LENGTH_SHORT).show();
    }

    public ParseUser getReturnAddress(Messages aMessage){

        ParseUser lUser = null;

        ParseQuery<ParseObject> lQuery = ParseQuery.getQuery("MessageTable")
                .whereEqualTo("objectId", aMessage.getObjectID());
        try{
            List<ParseObject> lMessage = lQuery.find();

            for(ParseObject message : lMessage){
                lUser =  message.getParseUser("userFrom");
            }
        }catch (ParseException e){
            e.printStackTrace();
        }

        fTo_User_Email = lUser.getEmail();

        return lUser;
    }

    public void cancelOnClick (View aView){
        finish();
    }

    public void composeMessageSendOnClick (View aView){

        if(fMessageBody.length() > 0){
            final ParseQuery<ParseUser> lQuery = ParseQuery.getQuery("_User");
            lQuery.whereEqualTo("username", fTo_User_Email);

            try {
                List<ParseUser> objects = lQuery.find();

                for (ParseUser user : objects) {
                    fParseObj.put(fUSER_FROM, fCurrentUser);//current user objectID
                    if(fFromMessageView)//if replying to a message
                        fParseObj.put(fUSER_TO, fReturnUser);//to user objectID
                    else
                        fParseObj.put(fUSER_TO, user);//to user objectID
                    fParseObj.put(fMESSAGE, fMessageBody.getText().toString());
                    fParseObj.put(fREAD, false);
                }

                fParseObj.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            UserInbox.fUserMessages.add(fParseObj);
                            UserInbox.fAdapter.notifyDataSetChanged();
                            makeToast("Message sent!");
                            finish();
                        } else {
                            e.printStackTrace();
                            makeToast("Message not sent!");
                        }
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }else makeToast("Cannot send empty message.");
    }

    public void toFieldOnClick(View aView){
        toActivity(fTO_USER_DIRECTORY);
    }

    //Starting activity for result
    public void toActivity(int aCode){
        Intent lIntent = new Intent(this, UserDirectory.class);
        lIntent.putExtra("fromCompose", 2);
        lIntent.putExtra("fromShared",false);
        startActivityForResult(lIntent, aCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            fTo_User_Email = data.getStringExtra("toField");
            fToField.setText("TO: " + fTo_User_Email);
        }
    }
}
