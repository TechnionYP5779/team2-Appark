package com.project.technion.appark.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.ArrayList;


public class ParkingSpotsAdapter extends ArrayAdapter<ParkingSpot> {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDB;


    public ParkingSpotsAdapter(Context context, ArrayList<ParkingSpot> parkingSpots){
        super(context,0, parkingSpots);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ParkingSpot parkingSpot = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_parkings_list_item, parent, false);
        }
        TextView textViewLocation = convertView.findViewById(R.id.textView_location);
        TextView textViewPrice = convertView.findViewById(R.id.textView_price);
        textViewLocation.setText(parkingSpot.address);
        textViewPrice.setText(parkingSpot.price+" $");

        Button button = convertView.findViewById(R.id.button_make_offer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDB.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        ParkingSpot p = u.parkingSpots.get(position);
                        String offerId = mDB.push().getKey();
                        mDB.child("Offers").child(offerId).setValue(new Offer(p.id,mUser.getUid(),1000,1000));
                        p.offers.add(offerId);
                        mDB.child("Users").child(mUser.getUid()).setValue(u);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


            }
        });


        return convertView;
    }
}