package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserDirectory extends AppCompatActivity implements GetUsersAsync.IGetUsers{

    final String fGOTO_MY_PROFILE = "android.intent.action.MAIN_PROFILE";
    final String fGOTO_ALBUM_LIST = "android.intent.action.ALBUM_LIST";
    final String fGOTO_USER_INBOX = "android.intent.action.USER_INBOX";
    final String fGOTO_NOTIFICATIONS = "android.intent.action.NOTIFICATIONS";

    RecyclerView fUserRecycler;
    RecyclerAdapter fAdapter;
    LinearLayoutManager fRecyclerLayout;

    ArrayList<User> fUsers;
    boolean fIsShared,fShowShared;
    int fWhich;
    String fAlbumName;
    ParseUser fCurrentUser;

    Bundle fIntentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_directory);

        fUsers = new ArrayList<>();
        getItems();
        fCurrentUser = ParseUser.getCurrentUser();

        if (fIsShared){
            fAlbumName = fIntentBundle.getString("albumName");
            fShowShared = fIntentBundle.getBoolean("showShared");
            new GetUsersAsync(this,fAlbumName,fShowShared,fIsShared).execute();
        }
        else
            new GetUsersAsync(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_directory, menu);
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
        fIntentBundle = getIntent().getExtras();

        fWhich = fIntentBundle.getInt("fromCompose");
        fIsShared = fIntentBundle.getBoolean("fromShared");
        fUserRecycler = (RecyclerView) findViewById(R.id.RecyclerViewUserDirectory);

        fAlbumName = null;
        fShowShared = false;
    }

    @Override
    public void putUsers(ArrayList<User> users) {
        setRecyclerView(users);
    }

    private void setRecyclerView(ArrayList<User> photos) {
        fRecyclerLayout = new LinearLayoutManager(UserDirectory.this);
        fUserRecycler.setLayoutManager(fRecyclerLayout);

        if (photos!=null)
            fUsers = photos;

        fAdapter = new RecyclerAdapter(fUsers,UserDirectory.this,3,fWhich);
        fUserRecycler.setAdapter(fAdapter);
    }

    public void logoutOnClick (MenuItem aItem){

    }

    public void viewNotificationsOnClick(MenuItem aItem){
        toActivity(fGOTO_NOTIFICATIONS);
    }

    public void viewProfileOnClick(MenuItem aItem){
        toActivity(fGOTO_MY_PROFILE);
    }

    public void viewInboxOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_INBOX);
    }

    public void viewUserAlbumsOnClick (MenuItem aItem){
        toActivity(fGOTO_ALBUM_LIST, 1);
    }

    //Starting activity
    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
        finish();
    }

    //To start View Albums
    public void toActivity(String aIntent, int aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("album_flag", aExtra);
        lIntent.putExtra("current_user", fCurrentUser.getEmail());
        startActivity(lIntent);
        finish();
    }

}
