package com.project.technion.appark.activities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.project.technion.appark.adapters.ParkingSpotsOfferAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpotActivity extends AppCompatActivity {
    private static final String TAG = "ParkingSpotActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private ListView mListView;
    private ParkingSpotsOfferAdapter mAdapter;
    private String parkingSpotId;
    private ParkingSpot parkingSpot;
    private FloatingActionButton mFab;
    private TextView tvAddress, tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spot);
        tvAddress = findViewById(R.id.address);
        tvPrice = findViewById(R.id.price);
        // parking_spot
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        parkingSpotId = getIntent().getStringExtra("parking_spot_id");
        mListView = findViewById(R.id.list_view);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.child("Users").child(mUser.getUid()).getValue(User.class);
                List<ParkingSpot> parkingSpotsList = u.parkingSpots;
                for(ParkingSpot ps: parkingSpotsList){
                    if(ps.getId().equals(parkingSpotId)){
                        parkingSpot = ps;
                        break;
                    }
                }
                //TODO: make sure it should not happen
                if (parkingSpot == null) {
                    Toast.makeText(ParkingSpotActivity.this, "Should not happen!", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Offer> offersList = parkingSpot.offers.stream()
                        .map(offerID -> dataSnapshot.child("Offers").child(offerID).getValue(Offer.class)).collect(Collectors.toList());
                tvAddress.setText(parkingSpot.address);
                tvPrice.setText(parkingSpot.price + " $");
                mAdapter = new ParkingSpotsOfferAdapter(getApplicationContext(), new ArrayList<>(offersList));
                mListView.setAdapter(mAdapter);
                TextView noOffers = findViewById(R.id.textView_no_offers);
                if (offersList.size() == 0) {
                    noOffers.setVisibility(View.VISIBLE);
                } else {
                    noOffers.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), OfferPopActivity.class);
            i.putExtra("parking_spot_index", parkingSpotId);
            startActivity(i);
        });

        Button buttonStreetView = findViewById(R.id.button_street_view);
        buttonStreetView.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+parkingSpot.lat+","+parkingSpot.lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        });
    }
}

