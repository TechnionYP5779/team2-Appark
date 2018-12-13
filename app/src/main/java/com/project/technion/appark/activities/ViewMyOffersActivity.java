package com.project.technion.appark.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.exceptions.ParkingSpotNotInSystem;

import java.util.List;

public class ViewMyOffersActivity extends AppCompatActivity {
    private static final String  TAG = "ViewMyOffersActivity";

    private DataBase db;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_offers);

        db = DummyDB.getInstance();
        int id = getIntent().getIntExtra("user_id",-1);
        Log.d(TAG,"user id is "+id);
        mUser = db.getUser(id);

        final TextView offerListTextView = findViewById(R.id.offer_list);
        List<Integer> myOffersIDS = db.getParkingSpotsOfUser(mUser.getId());
        if(myOffersIDS.size() != 0)
            offerListTextView.setText("");
        for(Integer i : myOffersIDS){
            try {
                offerListTextView.append(db.getParkingSpot(i).toString());
            } catch (ParkingSpotNotInSystem parkingSpotNotInSystem) {
                parkingSpotNotInSystem.printStackTrace();
            }
        }
    }
}
