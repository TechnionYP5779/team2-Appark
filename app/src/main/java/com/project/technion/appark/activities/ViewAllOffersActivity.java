package com.project.technion.appark.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.DummyParkingSpot;
import com.project.technion.appark.adapters.OffersAdapter;
import com.project.technion.appark.R;
import com.project.technion.appark.User;

import java.util.ArrayList;

public class ViewAllOffersActivity extends AppCompatActivity {
    private static final String  TAG = "ViewAllOffersActivity";

    private DataBase db;
    private User mUser;

    private ListView mListView;
    private OffersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_offers);
        mListView = findViewById(R.id.list_view);

        FloatingActionButton search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Let's search!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(ViewAllOffersActivity.this, SearchParkingsActivity.class);
//                i.putExtra("user_id",mUser.getId());
                startActivity(i);
            }
        });

        db = DummyDB.getInstance();
        int id = getIntent().getIntExtra("user_id",-1);
        Log.d(TAG,"user id is "+id);
        mUser = db.getUser(id);

        ArrayList<DummyParkingSpot> spots = new ArrayList<>(db.getAllParkingSpot());
//        mAdapter = new OffersAdapter(this, spots);
//        mListView.setAdapter(mAdapter);
        TextView noOffers = findViewById(R.id.textView_no_offers);
        if(spots.size() == 0){
            noOffers.setVisibility(View.VISIBLE);
        }
        else{
            noOffers.setVisibility(View.INVISIBLE);
        }
    }

}
