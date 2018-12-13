package com.project.technion.appark.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.ParkingSpotsAdapter;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.exceptions.ParkingSpotNotInSystem;

import java.util.ArrayList;
import java.util.List;

public class ViewAllOffersActivity extends AppCompatActivity {
    private static final String  TAG = "ViewAllOffersActivity";

    private DataBase db;
    private User mUser;

    private ListView mListView;
    private ParkingSpotsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_parking);
        mListView = findViewById(R.id.list_view);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        db = DummyDB.getInstance();
        int id = getIntent().getIntExtra("user_id",-1);
        Log.d(TAG,"user id is "+id);
        mUser = db.getUser(id);

        ArrayList<ParkingSpot> spots = new ArrayList<>(db.getAllParkingSpot());
        mAdapter = new ParkingSpotsAdapter(this, spots);
        mListView.setAdapter(mAdapter);
        TextView noOffers = findViewById(R.id.textView_no_offers);
        if(spots.size() == 0){
            noOffers.setVisibility(View.VISIBLE);
        }
        else{
            noOffers.setVisibility(View.INVISIBLE);
        }
    }

}