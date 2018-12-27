package com.project.technion.appark.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.R;
import com.project.technion.appark.Reservation;
import com.project.technion.appark.User;
import com.project.technion.appark.utils.Constants;

import java.util.Locale;

public class OfferActivity extends AppCompatActivity {
    private LinearLayout addressLayer, streetViewLayer, bookMeLayer;
    private TextView mAddress, mPrice;

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        double price = getIntent().getDoubleExtra("price", -1.0);
        String userId = getIntent().getStringExtra("userId");
        String offerId = getIntent().getStringExtra("offerId");
        String psId = getIntent().getStringExtra("PSID");
        long startMillis = getIntent().getLongExtra("startMillis", -1);
        long endMillis = getIntent().getLongExtra("endMillis", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addressLayer = findViewById(R.id.addressLayer);
        streetViewLayer = findViewById(R.id.streetviewLayer);
        bookMeLayer = findViewById(R.id.bookLayer);
        mAddress = findViewById(R.id.tvAddress);
        mAddress.setText(getIntent().getStringExtra("Address"));
        mPrice = findViewById(R.id.tvPrice);
        mPrice.setText(String.format("%s %s per hour", price, Constants.CURRENCY));

        addressLayer.setOnClickListener(v -> {
            String location = String.format(Locale.getDefault(), "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", "", lat, lng), "UTF-8");
            startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(location)));
        });

        streetViewLayer.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+ lat + "," + lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        bookMeLayer.setOnClickListener(v -> mDB.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userId.equals(mAuth.getUid())) {
                    Toast.makeText(getApplicationContext(), "You can't book your own offer!", Toast.LENGTH_SHORT).show();
                    return;
                }
                User seller = dataSnapshot.child(userId).getValue(User.class);
                User buyer = dataSnapshot.child(mAuth.getUid()).getValue(User.class);

                String rid = mDB.push().getKey();
                Reservation reservation = new Reservation(rid, userId, mAuth.getUid(), psId, startMillis, endMillis);

                seller.reservations.add(reservation);
                buyer.reservations.add(reservation);

                seller.removeOfferById(offerId);
                buyer.removeOfferById(offerId);

                mDB.child("Offers").child(offerId).removeValue();
                mDB.child("Users").child(userId).setValue(seller);
                mDB.child("Users").child(mAuth.getUid()).setValue(buyer);
                bookMeLayer.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Parking Spot Booked!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
        bookMeLayer.setEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}
