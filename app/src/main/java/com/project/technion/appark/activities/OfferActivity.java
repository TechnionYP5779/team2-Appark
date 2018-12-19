package com.project.technion.appark.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.project.technion.appark.R;

import java.util.Locale;

public class OfferActivity extends AppCompatActivity {
    private LinearLayout addressLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        addressLayer = findViewById(R.id.addressLayer);
        addressLayer.setOnClickListener(v -> {
            /*
            Geocoder gc = new Geocoder(getContext());
            if(gc.isPresent()) {
                List<Address> list = null;
                try {
                    list = gc.getFromLocation(offer.lat, offer.lng, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String found_address = list.get(0).getAddressLine(0);
                String location = String.format(Locale.getDefault(), "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", found_address, offer.lat, offer.lng), "UTF-8");
                getContext().startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(location)));
            }
            */
            String location = String.format(Locale.getDefault(), "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", "", lat, lng), "UTF-8");
            //String uri = "waze://?ll=40.761043, -73.980545&navigate=yes";
            //String.format(Locale.ENGLISH, "geo:0,0?q=") + android.net.Uri.encode(String.format("%s@%f,%f", label, latitude, longitude), "UTF-8");
            startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(location)));
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}
