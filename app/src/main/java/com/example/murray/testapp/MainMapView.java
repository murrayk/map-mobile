package com.example.murray.testapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by murray on 25/08/14.
 */
public class MainMapView extends FragmentActivity {

    private static final String MAP_FRAGMENT_TAG = "com.example.murray.testapp.MAP_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main);


        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentByTag(MAP_FRAGMENT_TAG) == null) {
            MapFragment mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_container, mapFragment, MAP_FRAGMENT_TAG).commit();
        }
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
