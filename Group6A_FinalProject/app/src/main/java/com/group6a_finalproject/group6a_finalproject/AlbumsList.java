package com.group6a_finalproject.group6a_finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class AlbumsList extends AppCompatActivity implements GetAlbumsAsync.IGetAlbums{

    final String fGOTO_CRTEATE_ALBUM = "android.intent.action.CREATE_ALBUM";
    final String fGOTO_MY_PROFILE = "android.intent.action.MAIN_PROFILE";
    final String fGOTO_ALBUM_LIST = "android.intent.action.ALBUM_LIST";
    final String fGOTO_USER_DIRECTORY ="android.intent.action.USER_DIRECTORY";
    final String fGOTO_USER_INBOX = "android.intent.action.USER_INBOX";
    final String fGOTO_NOTIFICATIONS = "android.intent.action.NOTIFICATIONS";
    final int fCHECK_CREATE_ALBUM = 1003;
    static final int fCHECK_EDIT_ALBUM = 1004;

    RecyclerView fAlbumsListRecycler;
    RecyclerAdapter fAlbumsAdapter;
    LinearLayoutManager fAlbumsLayoutManager;
    TextView fAlbumListTitle;

    static ArrayList<Album> fAlbumsList;
    int fWhoseAlbum;

    ParseUser fCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_list);

        getItems();

        new GetAlbumsAsync(this,fCurrentUser).execute(fWhoseAlbum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albums_list, menu);
        if (fWhoseAlbum == 2)
            return false;
        else
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

    private void getItems(){
        fAlbumsListRecycler = (RecyclerView) findViewById(R.id.recyclerViewAlbums);
        fAlbumListTitle = (TextView) findViewById(R.id.textViewAlbumListTitle);

        Bundle lExtras = getIntent().getExtras();
        fWhoseAlbum = lExtras.getInt("album_flag");
        String lUserEmail = lExtras.getString("current_user");
        ParseQuery<ParseUser> lGetUser = ParseUser.getQuery();
        lGetUser.whereContains("email",lUserEmail);
        try {
            List<ParseUser> lUsers = lGetUser.find();
            if(lUsers!=null){
                fCurrentUser = lUsers.get(0);

                if (fCurrentUser == ParseUser.getCurrentUser())
                    fAlbumListTitle.setText("My Albums");
                else fAlbumListTitle.setText(fCurrentUser.getString("name")+"'s Albums");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Album> sortList(ArrayList<Album> nonSorted){

        Collections.sort(nonSorted, new Comparator<Album>() {
            @Override
            public int compare(Album lhs, Album rhs) {
                return lhs.getAlbumName().compareToIgnoreCase(rhs.getAlbumName());
            }
        });

        return nonSorted;
    }

    private void setRecyclerView(ArrayList<Album> albums) {
        fAlbumsLayoutManager=  new LinearLayoutManager(this);
        fAlbumsListRecycler.setLayoutManager(fAlbumsLayoutManager);

        if (albums!=null) {
            fAlbumsList = albums;
            fAlbumsList = sortList(fAlbumsList);
        }

        fAlbumsAdapter = new RecyclerAdapter(AlbumsList.this,1);
        fAlbumsAdapter.setAlbumsList(fAlbumsList);
        fAlbumsListRecycler.setAdapter(fAlbumsAdapter);
    }

    private void resetRecyclerView(ArrayList<Album> albums){
        albums = sortList(albums);

        RecyclerAdapter lAlbumsAdapter = new RecyclerAdapter(AlbumsList.this,1);
        lAlbumsAdapter.setAlbumsList(albums);
        fAlbumsListRecycler.swapAdapter(lAlbumsAdapter, false);

    }

    @Override
    public void putAlbums(ArrayList<Album> albums) {
        setRecyclerView(albums);
    }

    public void toActivityForResult(String aIntent,int aExtra){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("taskToPerform",aExtra);
        startActivityForResult(lIntent, fCHECK_CREATE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case fCHECK_CREATE_ALBUM:
                if(resultCode == RESULT_OK){

                    SharedPreferences sp = getSharedPreferences("AlbumDetails",MODE_PRIVATE);
                    SharedPreferenceSetup lTempAlbum = new SharedPreferenceSetup(sp);
                    Album toPutIntoList = lTempAlbum.getAlbumPreference("myAlbum");


                    if(!fAlbumsList.contains(toPutIntoList)){
                        fAlbumsList.add(toPutIntoList);
                        fAlbumsAdapter.notifyDataSetChanged();
                    }
                    lTempAlbum.clearPreference();
                }
                break;

            case fCHECK_EDIT_ALBUM:
                if (resultCode == RESULT_OK){
                    SharedPreferences sp = getSharedPreferences("AlbumDetails",MODE_PRIVATE);
                    SharedPreferenceSetup lTempAlbum = new SharedPreferenceSetup(sp);
                    if(lTempAlbum.checkKey("oldAlbumName")){
                        String aOldAlbumName = lTempAlbum.getOldAlbumPreference("oldAlbumName");
                        for(int i=0;i<fAlbumsList.size();i++){
                            if(fAlbumsList.get(i).getAlbumName().equals(aOldAlbumName)){
                                fAlbumsList.set(i, lTempAlbum.getAlbumPreference("myAlbum"));
                                fAlbumsList = sortList(fAlbumsList);
                                fAlbumsAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    lTempAlbum.clearPreference();
                }
        }

    }

    public void createAlbumOnClick (MenuItem aItem){
        toActivityForResult(fGOTO_CRTEATE_ALBUM, 1);
    }

    public void allAlbumsOnClick(MenuItem aItem){
        resetRecyclerView(fAlbumsList);
    }

    public void myAlbumsOnClick(MenuItem aItem){
        ArrayList<Album> lMyAlbums = new ArrayList<>();
        String lCurrentUserEmail = fCurrentUser.getEmail();

        for(Album album:fAlbumsList){
            if (lCurrentUserEmail.equals(album.getOwnerEmail())){
                lMyAlbums.add(album);
            }
        }

        resetRecyclerView(lMyAlbums);
    }

    public void publicAlbumsOnClick(MenuItem aItem){
        ArrayList<Album> lMyAlbums = new ArrayList<>();
        String lCurrentUserEmail = fCurrentUser.getEmail();

        for(Album album:fAlbumsList){
            if (album.getPrivacy().equals("Public") && !lCurrentUserEmail.equals(album.getOwnerEmail())){
                lMyAlbums.add(album);
            }
        }

        resetRecyclerView(lMyAlbums);
    }

    public void sharedAlbumsOnClick(MenuItem aItem){
        ArrayList<Album> lMyAlbums = new ArrayList<>();
        String lCurrentUserEmail = fCurrentUser.getEmail();

        for(Album album:fAlbumsList){
            if (album.getPrivacy().equals("Shared")){
//                if((album.getOwnerEmail().equals(fCurrentUser.getEmail()) || canAdd(album.getAlbumName())))
                    lMyAlbums.add(album);
            }
        }

        resetRecyclerView(lMyAlbums);
    }

    public boolean canAdd(String aAlbumName){
        boolean lCanAdd = false;

        ParseQuery<ParseObject> lGetAlbumObject = ParseQuery.getQuery("Album");
        lGetAlbumObject.whereEqualTo("name",aAlbumName);
        try {
            List<ParseObject> lAlbum = lGetAlbumObject.find();

            ParseQuery<ParseObject> lGetShared = ParseQuery.getQuery("AlbumShare");
            lGetShared.include("sharedWith");
            lGetShared.include("album");
            lGetShared.whereEqualTo("album",lAlbum.get(0));
            List<ParseObject> lSharedUsers = lGetShared.find();

            for (ParseObject objects:lSharedUsers){
                if (objects.getParseUser("sharedWith").equals(fCurrentUser)){
                    lCanAdd = true;
                    break;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return lCanAdd;
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

    public void viewUserDirectoryOnClick (MenuItem aItem){
        toActivity(fGOTO_USER_DIRECTORY, 1, false);
    }

    //Starting activity
    public void toActivity(String aIntent){
        Intent lIntent = new Intent(aIntent);
        startActivity(lIntent);
        finish();
    }

    //To start User Directory
    public void toActivity(String aIntent, int aExtra1, boolean aExtra2){
        Intent lIntent = new Intent(aIntent);
        lIntent.putExtra("fromCompose", aExtra1);
        lIntent.putExtra("fromShared", aExtra2);
        startActivity(lIntent);
        finish();
    }

}
