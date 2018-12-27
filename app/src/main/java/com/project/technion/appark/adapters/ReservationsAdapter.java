package com.project.technion.appark.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.Reservation;
import com.project.technion.appark.User;
import com.project.technion.appark.activities.ReservationActivity;
import com.project.technion.appark.utils.Constants;

import java.util.ArrayList;
import java.util.Objects;


public class ReservationsAdapter extends ArrayAdapter<Reservation> {
    private DatabaseReference mDB;


    public ReservationsAdapter(Context context, ArrayList<Reservation> reservations) {
        super(context, 0, reservations);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Reservation reservation = Objects.requireNonNull(getItem(position));
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_reservations_list_item, parent, false);
        }
        final TextView textViewLocation = convertView.findViewById(R.id.textView_location);
        final TextView textViewPrice = convertView.findViewById(R.id.textView_price);

        RelativeLayout itemLayout = convertView.findViewById(R.id.reservationItemLayer);

        itemLayout.setOnClickListener(v -> mDB.child("Users").child(reservation.sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (u != null) {
                    for (ParkingSpot p : u.parkingSpots) {
                        if (p.id.equals(reservation.parkingSpotId)) {
                            Intent i = new Intent(getContext(), ReservationActivity.class);
                            i.putExtra("lat", p.lat);
                            i.putExtra("lng", p.lng);
                            i.putExtra("Address", textViewLocation.getText().toString());
                            i.putExtra("price", p.price);
                            i.putExtra("userId", p.userId);
                            i.putExtra("reservationId", reservation.id);
                            i.putExtra("PSID", reservation.parkingSpotId);
                            i.putExtra("startMillis", reservation.startCalenderInMillis);
                            i.putExtra("endMillis", reservation.endCalenderInMillis);
                            getContext().startActivity(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        }));

        mDB.child("Users").child(reservation.sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User seller = dataSnapshot.getValue(User.class);
                if (seller != null) {
                    for (ParkingSpot p : seller.parkingSpots) {
                        if (p.id.equals(reservation.parkingSpotId)) {
                            textViewLocation.setText(p.address);
                            textViewPrice.setText(p.price + " " + Constants.CURRENCY);
                            break;
                        }
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