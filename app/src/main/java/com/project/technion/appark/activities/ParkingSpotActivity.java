package com.project.technion.appark.activities;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.Offer;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.adapters.ParkingSpotsAdapter;
import com.project.technion.appark.adapters.ParkingSpotsOfferAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpotActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private ListView mListView;
    private ParkingSpotsOfferAdapter mAdapter;
    private Integer parkingSpotIndex;
    private ParkingSpot parkingSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spot);

        // parking_spot
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        parkingSpotIndex = getIntent().getIntExtra("parking_spot_index",-1);
        mListView = findViewById(R.id.list_view);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.child("Users").child(mUser.getUid()).getValue(User.class);
                parkingSpot = u.parkingSpots.get(parkingSpotIndex);
                //TODO: make sure it should not happen
                if (parkingSpot == null) {
                    Toast.makeText(ParkingSpotActivity.this, "Should not happen!", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Offer> offersList = parkingSpot.offers.stream()
                        .map(offerID -> dataSnapshot.child("Offers").child(offerID).getValue(Offer.class)).collect(Collectors.toList());

                mAdapter = new ParkingSpotsOfferAdapter(getApplicationContext(), new ArrayList<>(offersList));
                mListView.setAdapter(mAdapter);
                TextView noOffers = findViewById(R.id.textView_no_offers);
                if (offersList.size() == 0) {
                    noOffers.setVisibility(View.VISIBLE);
                } else {
                    noOffers.setVisibility(View.INVISIBLE);
                }

//                Toast.makeText(MasterActivity.this, u.getName() +" "+u.getContactInfo(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
