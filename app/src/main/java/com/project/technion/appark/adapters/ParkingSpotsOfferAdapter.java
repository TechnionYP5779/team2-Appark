package com.project.technion.appark.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

    public ParkingSpotsOfferAdapter(Context context, ArrayList<Offer> offers) {
        super(context, 0, offers);
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
        if (offer == null) return convertView;

        TextView textViewStartTime = convertView.findViewById(R.id.start_time);

        TextView textViewEndTime = convertView.findViewById(R.id.end_time);
        SimpleDateFormat start_format = new SimpleDateFormat("dd/MM/YYYY, HH:mm");
        SimpleDateFormat end_format = new SimpleDateFormat("dd/MM/YYYY, HH:mm");

        textViewStartTime.setText(start_format.format(offer.startTime().getTime()));
        textViewEndTime.setText(end_format.format(offer.endTime().getTime()));

        Button deleteButton = convertView.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("Delete this offer?");
            builder.setMessage("Are you sure you want to delete this offer?");
            builder.setPositiveButton("YES", (dialog, which) -> mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.child("Users").child(mUser.getUid()).getValue(User.class);
                    Offer offer1 = getItem(position);
                    for (ParkingSpot p : u.parkingSpots) {
                        if (p.id.equals(offer1.parkingSpotId)) {
                            p.offers.remove(offer1.id);
                            break;
                        }
                    }
                    mDB.child("Offers").child(offer1.id).removeValue();
                    mDB.child("Users").child(mUser.getUid()).setValue(u);
                    Toast.makeText(getContext(), "The offer was deleted!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            }));
            builder.setNegativeButton("NO", null);
            builder.show();

        });

        return convertView;
    }

}