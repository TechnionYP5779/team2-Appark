package com.project.technion.appark.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.Offer;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class ReservationActivity extends AppCompatActivity {

    private DatabaseReference mDB;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY, HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
//        String address = getIntent().getStringExtra("Address");
        double price = getIntent().getDoubleExtra("price", -1.0);
        String sellerId = getIntent().getStringExtra("userId");
        String reservationId = getIntent().getStringExtra("reservationId");
        String psId = getIntent().getStringExtra("PSID");
        long startMillis = getIntent().getLongExtra("startMillis", -1);
        long endMillis = getIntent().getLongExtra("endMillis", -1);

        TextView priceText = findViewById(R.id.raPrice);
        priceText.setText(String.valueOf(price) + Constants.CURRENCY + " per hour");

        TextView timesText = findViewById(R.id.raAvailability);
        Calendar startCalendar = getCalendar(startMillis);
        Calendar endCalendar = getCalendar(endMillis);
        timesText.setText(calendarAsString(startCalendar) + " - " + calendarAsString(endCalendar));

        TextView addressText = findViewById(R.id.raAddress);
        addressText.setText(getIntent().getStringExtra("Address"));

        LinearLayout addressLayer = findViewById(R.id.crAddressLayer);
        LinearLayout streetViewLayer = findViewById(R.id.crStreetviewLayer);
        LinearLayout unbookMeLayer = findViewById(R.id.unbookLayer);

        addressLayer.setOnClickListener(v -> {
            String location = String.format(Locale.getDefault(), "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", "", lat, lng), "UTF-8");
            startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(location)));
        });

        streetViewLayer.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + lat + "," + lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        unbookMeLayer.setOnClickListener(v -> mDB.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User seller = Objects.requireNonNull(dataSnapshot.child(sellerId).getValue(User.class));
                User buyer = Objects.requireNonNull(dataSnapshot.child(Objects.requireNonNull(mAuth.getUid())).getValue(User.class));

                String offerId = Objects.requireNonNull(mDB.push().getKey());
                Offer offer = new Offer(offerId, psId, sellerId, startMillis, endMillis, lat, lng, price);

                seller.removeReservationById(reservationId);
                buyer.removeReservationById(reservationId);

                if (seller.addOffer(offer)){
                    mDB.child("Offers").child(offerId).setValue(offer);
                }

                mDB.child("Users").child(sellerId).setValue(seller);
                mDB.child("Users").child(mAuth.getUid()).setValue(buyer);

                unbookMeLayer.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Parking Spot Unbooked!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));
        unbookMeLayer.setEnabled(true);

        FloatingActionButton fab = findViewById(R.id.raFab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private Calendar getCalendar(long timeInMillis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        return c;
    }

    private String calendarAsString(Calendar c) {
        return dateFormat.format(c.getTime());
    }
}
