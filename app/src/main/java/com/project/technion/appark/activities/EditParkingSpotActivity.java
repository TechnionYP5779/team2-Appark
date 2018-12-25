package com.project.technion.appark.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.project.technion.appark.User;
import com.project.technion.appark.adapters.ParkingSpotsOfferAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditParkingSpotActivity extends AppCompatActivity {
    private static final String TAG = "EditParkingSpotActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private ListView mListView;
    private ParkingSpotsOfferAdapter mAdapter;
    private Integer parkingSpotIndex;
    private ParkingSpot parkingSpot;
    private FloatingActionButton mFab;
    private TextView editTextAddress, editTextPrice;
    private ParkingSpot mParkingSpot;
    private StorageReference mStorageRef;


    final private int IMAGE_PICKER_RESULT = 2;
    Uri imageDataUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_parking_spot);

        // Firebase initalization
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        editTextAddress = findViewById(R.id.ps_address);
        editTextPrice = findViewById(R.id.ps_price);

        parkingSpotIndex = getIntent().getIntExtra("parking_spot_index", -1);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.child("Users").child(mUser.getUid()).getValue(User.class);
                parkingSpot = u.parkingSpots.get(parkingSpotIndex);
                //TODO: make sure it should not happen
                if (parkingSpot == null) {
                    Toast.makeText(getApplicationContext(), "Should not happen!", Toast.LENGTH_SHORT).show();
                    return;
                }

                editTextAddress.setText(parkingSpot.getAddress());
                editTextPrice.setText(Long.valueOf(Math.round(parkingSpot.getPrice())).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button updateButton = findViewById(R.id.button_update);
        updateButton.setOnClickListener(view -> {
            updateButton.setEnabled(false);
            String strPrice = editTextPrice.getText().toString();
            String address = editTextAddress.getText().toString();
            Geocoder gc = new Geocoder(getApplicationContext());
            if (gc.isPresent()) {
                List<Address> list = null;
                try {
                    list = gc.getFromLocationName(address, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (list.isEmpty()) {// no such address
                    Toast.makeText(getApplicationContext(), "No such address!", Toast.LENGTH_SHORT).show();
                    updateButton.setEnabled(true);
                    return;
                }
                Address found_address = list.get(0);

                double lat = found_address.getLatitude();
                double lng = found_address.getLongitude();

                Double price = Double.parseDouble(strPrice);
                mParkingSpot = new ParkingSpot(mUser.getUid(), price, found_address.getAddressLine(0), lat, lng, mDB.push().getKey());
                mDB.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        u.parkingSpots.set(parkingSpotIndex, mParkingSpot);
                        mDB.child("Users").child(mUser.getUid()).setValue(u);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                if (imageDataUri != null) {
                    try {
                        StorageReference filepath = mStorageRef.child("Images").child(mUser.getUid()).child(mParkingSpot.id);

                        filepath.putFile(imageDataUri).addOnSuccessListener(taskSnapshot -> {
                        }).addOnCanceledListener(() -> {
                        });
                    } catch (Exception e) {
                    }


                } else {
                    updateButton.setEnabled(true);
                }
            }
            Toast.makeText(getApplicationContext(), "Parking Spot info updated!", Toast.LENGTH_SHORT).show();
            finish();
        });

        Button pickImage = findViewById(R.id.button_pick_image);
        pickImage.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, IMAGE_PICKER_RESULT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICKER_RESULT && resultCode == RESULT_OK) {
            imageDataUri = data.getData();
            Button update = findViewById(R.id.button_update);
            update.setEnabled(true);
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(imageDataUri);
        }
    }
}
