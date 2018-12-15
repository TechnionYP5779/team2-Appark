package com.project.technion.appark.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.Offer;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;

import java.util.ArrayList;


public class OffersAdapter extends ArrayAdapter<Offer> {
    private final FirebaseAuth mAuth;
    private DatabaseReference mDB;


    public OffersAdapter(Context context, ArrayList<Offer> offers){
        super(context,0, offers);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Offer offer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.all_offers_list_item, parent, false);
        }
        final TextView textViewLocation = convertView.findViewById(R.id.textView_location);
        final TextView textViewPrice = convertView.findViewById(R.id.textView_price);

        mDB.child("Users").child(offer.userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                for(ParkingSpot p : u.parkingSpots){
                    if(p.id.equals(offer.parkingSpotId)){
                        textViewLocation.setText(p.address);
                        textViewPrice.setText(p.price+" $");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return convertView;
    }
}