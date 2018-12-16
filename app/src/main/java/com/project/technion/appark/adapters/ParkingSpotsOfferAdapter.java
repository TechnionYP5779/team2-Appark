package com.project.technion.appark.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.project.technion.appark.activities.OfferPopActivity;
import com.project.technion.appark.activities.ParkingSpotActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ParkingSpotsOfferAdapter extends ArrayAdapter<Offer> {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDB;

    public ParkingSpotsOfferAdapter(Context context, ArrayList<Offer> offers){
        super(context,0, offers);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Offer offer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parking_spots_offers_list_item, parent, false);
        }
        TextView textViewStartTime = convertView.findViewById(R.id.start_time);

        TextView textViewEndTime = convertView.findViewById(R.id.end_time);
        SimpleDateFormat start_format = new SimpleDateFormat("MMMM d, yyyy 'from' h:mm a");
        SimpleDateFormat end_format = new SimpleDateFormat("MMMM d, yyyy 'until' h:mm a");

        textViewStartTime.setText(start_format.format(offer.startTime().getTime()));
        textViewEndTime.setText(end_format.format(offer.endTime().getTime()));
        handleDeleteButton(convertView, position);
        return convertView;
    }

    private void handleDeleteButton(View convertView, int position){
        Button deleteButton = convertView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(view -> {

                mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.child("Users").child(mUser.getUid()).getValue(User.class);
                        Offer offer = getItem(position);
                        for(ParkingSpot p : u.parkingSpots){
                            if(p.id.equals(offer.parkingSpotId)){
                                p.offers.remove(offer.id);
                                break;
                            }
                        }
                        mDB.child("Offers").child(offer.id).removeValue();
                        mDB.child("Users").child(mUser.getUid()).setValue(u);
                        Toast.makeText(getContext(), "the offer was deleted ", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


        });
    }
}