package com.group6a_finalproject.group6a_finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseObject;

import java.util.ArrayList;

public class UserDirectory extends AppCompatActivity implements GetUsersAsync.IGetUsers{

    RecyclerView fUserRecycler;
    RecyclerAdapter fAdapter;
    LinearLayoutManager fRecyclerLayout;

    ArrayList<User> fUsers;
    boolean fIsShared,fShowShared;
    int fWhich;
    String fAlbumName;

    Bundle fIntentBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_directory);

        fUsers = new ArrayList<>();
        getItems();

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
}
