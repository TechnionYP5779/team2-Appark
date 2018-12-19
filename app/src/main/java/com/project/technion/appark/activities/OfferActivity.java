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
    private LinearLayout addressLayer, streetViewLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        double lat = getIntent().getDoubleExtra("lat", 0);
        double lng = getIntent().getDoubleExtra("lng", 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addressLayer = findViewById(R.id.addressLayer);
        streetViewLayer = findViewById(R.id.streetviewLayer);


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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }
}
