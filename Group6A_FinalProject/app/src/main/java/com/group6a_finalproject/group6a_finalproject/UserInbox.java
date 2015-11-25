package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserInbox extends AppCompatActivity implements GetMessagesAsync.IGetMessages {

    final String fGOTO_COMPOSE_MESSAGE = "android.intent.action.COMPOSE_MESSAGE";
    static List<ParseObject> fUserMessages = new ArrayList<>();
    static MessageAdapter fAdapter;

    RecyclerView fMessageRecycler;
    LinearLayoutManager fRecyclerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inbox);

        getItems();

        new GetMessagesAsync(this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_inbox, menu);
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

    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
    }

    public void composeMessageOnClick (View aView){
        toActivity(fGOTO_COMPOSE_MESSAGE);
    }

    public void getItems(){
        fMessageRecycler = (RecyclerView) findViewById(R.id.RecyclerViewInbox);
    }

    public void setRecyclerView(ArrayList<Messages> aMessageList){
        fRecyclerLayout = new LinearLayoutManager(UserInbox.this);
        fMessageRecycler.setLayoutManager(fRecyclerLayout);

        if (aMessageList != null)
            fAdapter = new MessageAdapter(aMessageList, UserInbox.this);
        fMessageRecycler.setAdapter(fAdapter);
    }

    @Override
    public void putMessages(ArrayList<Messages> messages) {
        setRecyclerView(messages);
    }
}
