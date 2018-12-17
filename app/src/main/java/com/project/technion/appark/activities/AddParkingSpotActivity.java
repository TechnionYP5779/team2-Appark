package com.project.technion.appark.activities;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import java.io.IOException;
import java.util.List;

public class AddParkingSpotActivity extends AppCompatActivity {
    private static final String TAG = "AddParkingSpotActivity";
    private ParkingSpot mParkingSpot;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText etPrice, etAddress;
    private DatabaseReference mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_spot);
        mAuth = FirebaseAuth.getInstance();
        etPrice = findViewById(R.id.ps_price);
        etAddress = findViewById(R.id.ps_address);
        mDB = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();

        Button offerButton = findViewById(R.id.button_offer);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPrice = etPrice.getText().toString();
                if(TextUtils.isEmpty(strPrice)) {
                    etPrice.setError(getString(R.string.error_please_price));
                    etPrice.requestFocus();
                    return;
                }
                Double price = Double.parseDouble(strPrice);
                String address = etAddress.getText().toString();
                if(TextUtils.isEmpty(address)) {
                    etAddress.setError(getString(R.string.error_please_address));
                    etAddress.requestFocus();
                    return;
                }
                Geocoder gc = new Geocoder(getApplicationContext());
                if(gc.isPresent()) {
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocationName(address, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(list.isEmpty()) {// no such address
                        etAddress.setError(getString(R.string.error_no_such_address));
                        etAddress.requestFocus();
                        return;
                    }
                    Address found_address = list.get(0);

                    double lat = found_address.getLatitude();
                    double lng = found_address.getLongitude();
                    Log.d("AddParkingSpotActivity","Full Address Data: " + found_address.toString());
                    mParkingSpot = new ParkingSpot(mUser.getUid(), price, found_address.getAddressLine(0), lat, lng,mDB.push().getKey());
                    mDB.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);
                            u.parkingSpots.add(mParkingSpot);
                            mDB.child("Users").child(mUser.getUid()).setValue(u);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    Toast.makeText(AddParkingSpotActivity.this, "Parking Spot Added!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}
