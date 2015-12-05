package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseImageView;

public class ViewMessage extends AppCompatActivity {

    TextView fFromField, fMessageBody;
    ParseImageView fMessageAttachment;
    Messages fMessage;

    final String fGOTO_COMPOSE_MESSAGE = "android.intent.action.COMPOSE_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        getItems();

        fFromField.setText("FROM: " + fMessage.getFromField());
        fMessageBody.setText(fMessage.getMessageBody());
        fMessageAttachment.setParseFile(fMessage.getAttachment());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_message, menu);
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

    public void getItems(){
        fFromField = (TextView) findViewById(R.id.textViewViewMessageFromField);
        fMessageBody = (TextView) findViewById(R.id.editTextViewMessageBody);
        fMessageAttachment = (ParseImageView) findViewById(R.id.imageViewMessageAttachmentView);
        fMessage = (Messages) getIntent().getSerializableExtra("Message");
    }

    public void toActivity(String aIntent, Messages aMessage){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("FromView", true);
        lIntent.putExtra("Message", aMessage);
        startActivity(lIntent);
    }

    public void replyOnClick (View aView){
        toActivity(fGOTO_COMPOSE_MESSAGE, fMessage);
        finish();
    }
}
