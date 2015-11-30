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

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlbumsList extends AppCompatActivity implements GetAlbumsAsync.IGetAlbums{

    final String fGOTO_CRTEATE_ALBUM = "android.intent.action.CREATE_ALBUM";
    final int fCHECK_CREATE_ALBUM = 1003;
    static final int fCHECK_EDIT_ALBUM = 1004;

    RecyclerView fAlbumsListRecycler;
    RecyclerAdapter fAlbumsAdapter;
    LinearLayoutManager fAlbumsLayoutManager;

    ArrayList<Album> fAlbumsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_list);

        fAlbumsListRecycler = (RecyclerView) findViewById(R.id.recyclerViewAlbums);

        new GetAlbumsAsync(this).execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albums_list, menu);
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

    private void setRecyclerView(ArrayList<Album> albums) {
        fAlbumsLayoutManager=  new LinearLayoutManager(this);
        fAlbumsListRecycler.setLayoutManager(fAlbumsLayoutManager);

        if (albums!=null)
            fAlbumsList = albums;

        fAlbumsAdapter = new RecyclerAdapter(AlbumsList.this,1);
        fAlbumsAdapter.setAlbumsList(fAlbumsList);
        fAlbumsListRecycler.setAdapter(fAlbumsAdapter);
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

                }
                break;

            case fCHECK_EDIT_ALBUM:
                Log.d("Say ","hello");
                if (resultCode == RESULT_OK){
                    SharedPreferences sp = getSharedPreferences("AlbumDetails",MODE_PRIVATE);
                    SharedPreferenceSetup lTempAlbum = new SharedPreferenceSetup(sp);
                    if(lTempAlbum.checkKey("oldAlbumName")){
                        String aOldAlbumName = lTempAlbum.getOldAlbumPreference("oldAlbumName");
                        for(int i=0;i<fAlbumsList.size();i++){
                            if(fAlbumsList.get(i).getAlbumName().equals(aOldAlbumName)){
                                fAlbumsList.set(i, lTempAlbum.getAlbumPreference("myAlbum"));
                                fAlbumsAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
        }

    }

    public void createAlbumOnClick (MenuItem aItem){
        toActivityForResult(fGOTO_CRTEATE_ALBUM,1);
    }

}
