package com.project.technion.appark.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.technion.appark.Offer;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.Reservation;
import com.project.technion.appark.User;
import com.project.technion.appark.activities.OfferActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.startActivity;


public class OffersAdapter extends ArrayAdapter<Offer> {
    private final FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private Context mContext;
    private StorageReference mStorageRef;
    private RelativeLayout itemLayout;

    private static Location lastLocation;


    public OffersAdapter(Context context, ArrayList<Offer> offers) {
        super(context, 0, offers);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDB = FirebaseDatabase.getInstance().getReference();
        mContext = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Offer offer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.all_offers_list_item, parent, false);
        }
        final TextView textViewLocation = convertView.findViewById(R.id.tvAddress);
        final TextView textViewPrice = convertView.findViewById(R.id.textView_price);
        itemLayout = convertView.findViewById(R.id.offerItemLayer);

        itemLayout.setOnClickListener(v -> {
            getContext().startActivity(new Intent(getContext(), OfferActivity.class));
        });

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
        final TextView timeField = convertView.findViewById(R.id.tvTimeAndDate);

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(offer.startCalenderInMillis);
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(offer.endCalenderInMillis);
        timeField.setText(format.format(start.getTime()) + " to " + format.format(end.getTime()));
        final TextView textViewDistanceFromMe = convertView.findViewById(R.id.distance_from_me);

        if(lastLocation != null){
            Location offer_location = new Location("");
            offer_location.setLatitude(offer.lat);
            offer_location.setLongitude(offer.lng);
            float dist = lastLocation.distanceTo(offer_location)/1000;
            textViewDistanceFromMe.setText(String.format("%.2f KM", dist));
        }


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

        final ImageView imageView = convertView.findViewById(R.id.imageView);


        StorageReference storageRef = mStorageRef.child("Images").child(mAuth.getUid()).child(offer.parkingSpotId);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            Picasso.with(getContext()).load(uri.toString()).into(imageView);
            Picasso.with(getContext()).load(uri.toString())
                    .resize(100,100)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            imageView.setImageDrawable(imageDrawable);
                        }
                        @Override
                        public void onError() {
                            imageView.setImageResource(R.mipmap.ic_launcher);
                        }
                    });

            Log.d("beebo",uri.toString());

        }).addOnFailureListener(exception -> {
            Log.d("beebo","error");
            imageView.setImageResource(R.mipmap.ic_launcher);
        });

        setDistanceFromMe(convertView, offer);


        return convertView;
    }

    private void setDistanceFromMe(View convertView, Offer offer) {
        final TextView textViewDistanceFromMe = convertView.findViewById(R.id.distance_from_me);
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //TODO: consider 'append'
                lastLocation = location;
//                Toast.makeText(getContext(), "updating!", Toast.LENGTH_SHORT).show();
                Location offer_location = new Location("");
                offer_location.setLatitude(offer.lat);
                offer_location.setLongitude(offer.lng);
                float dist = location.distanceTo(offer_location)/1000;
                textViewDistanceFromMe.setText(String.format("%.2f KM", dist));
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