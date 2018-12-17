package com.project.technion.appark.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.project.technion.appark.Reservation;
import com.project.technion.appark.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.support.v4.app.ActivityCompat.requestPermissions;


public class OffersAdapter extends ArrayAdapter<Offer> {
    private final FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private Context mContext;


    public OffersAdapter(Context context, ArrayList<Offer> offers) {
        super(context, 0, offers);
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();
        mContext = context;
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
                for (ParkingSpot p : u.parkingSpots) {
                    if (p.id.equals(offer.parkingSpotId)) {
                        textViewLocation.setText(p.address);
                        textViewPrice.setText(p.price + " $");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        SimpleDateFormat format = new SimpleDateFormat("dd/MM , hh:mm");
        final TextView startTime = convertView.findViewById(R.id.tv_start_time);
        final TextView endTime = convertView.findViewById(R.id.tv_end_time);

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(offer.startCalenderInMillis);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(offer.endCalenderInMillis);
        startTime.setText(format.format(start.getTime()));
        endTime.setText(format.format(end.getTime()));

        Button bookNow = convertView.findViewById(R.id.button_booking);
        bookNow.setOnClickListener(view -> {
            mDB.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (offer.userId.equals(mAuth.getUid())) {
                        Toast.makeText(getContext(), "You can't book your own offer!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User seller = dataSnapshot.child(offer.userId).getValue(User.class);
                    User buyer = dataSnapshot.child(mAuth.getUid()).getValue(User.class);

                    Reservation reservation = new Reservation(offer.userId, mAuth.getUid(),
                            offer.parkingSpotId, offer.startCalenderInMillis, offer.endCalenderInMillis);

                    seller.reservations.add(reservation);
                    buyer.reservations.add(reservation);

                    seller.removeOfferById(offer.id);
                    buyer.removeOfferById(offer.id);

                    mDB.child("Offers").child(offer.id).removeValue();
                    mDB.child("Users").child(offer.userId).setValue(seller);
                    mDB.child("Users").child(mAuth.getUid()).setValue(buyer);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });

        setDistanceFromMe(convertView);


        return convertView;
    }

    private void setDistanceFromMe(View convertView) {
        final TextView textViewDistanceFromMe = convertView.findViewById(R.id.distance_from_me);
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //TODO: consider 'append'
                Toast.makeText(getContext(), "updating!", Toast.LENGTH_SHORT).show();
                textViewDistanceFromMe.setText(location.getLatitude()+","+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //TODO: change the arguments
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions((Activity)getContext(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            Toast.makeText(getContext(), "We need location permission...", Toast.LENGTH_SHORT).show();
            return;
        }
//        Toast.makeText(getContext(), "oh oh", Toast.LENGTH_SHORT).show();
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener,null);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);

    }
}