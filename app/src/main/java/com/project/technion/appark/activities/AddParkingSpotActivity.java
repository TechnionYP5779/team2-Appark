package com.project.technion.appark.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AddParkingSpotActivity extends AppCompatActivity {
    private static final String TAG = "AddParkingSpotActivity";
    private ParkingSpot mParkingSpot;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText etPrice, etAddress;
    private DatabaseReference mDB;
    private StorageReference mStorageRef;

    final private int IMAGE_PICKER_RESULT = 2;
    Uri imageDataUri;

    private void checkFilePermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permmisonCheck = AddParkingSpotActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
            permmisonCheck += AddParkingSpotActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
            if(permmisonCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
            else{
                Log.e("tag","no permission");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_PICKER_RESULT && resultCode == RESULT_OK){
            imageDataUri = data.getData();
            Button offerButton = findViewById(R.id.button_offer);
            offerButton.setEnabled(true);
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(imageDataUri);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_spot);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        etPrice = findViewById(R.id.ps_price);
        etAddress = findViewById(R.id.ps_address);
        mDB = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();

        checkFilePermissions();


        Button offerButton = findViewById(R.id.button_offer);
        offerButton.setOnClickListener(view -> {
            offerButton.setEnabled(false);
            String strPrice = etPrice.getText().toString();
            String address = etAddress.getText().toString();

            boolean emptyEtPrice = TextUtils.isEmpty(strPrice);
            boolean emptyEtAddress = TextUtils.isEmpty(address);

            if(emptyEtPrice) {
                etPrice.setError(getString(R.string.error_please_price));
                etPrice.requestFocus();
            }
            if(emptyEtAddress) {
                etAddress.setError(getString(R.string.error_please_address));
                etAddress.requestFocus();
            }
            if(emptyEtPrice || emptyEtAddress){
                offerButton.setEnabled(true);
                return;
            }
            Double price = Double.parseDouble(strPrice);

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
                    offerButton.setEnabled(true);
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

                if(imageDataUri != null) {
                    try {
                        StorageReference filepath = mStorageRef.child("Images").child(mUser.getUid()).child(mParkingSpot.id);

                        filepath.putFile(imageDataUri).addOnSuccessListener(taskSnapshot -> {
                        }).addOnCanceledListener(() -> {
                        });
                    }catch (Exception e){}


                }
//                else{
//                    offerButton.setEnabled(true);
//                }
            }
            Toast.makeText(AddParkingSpotActivity.this, "Parking Spot Added!", Toast.LENGTH_SHORT).show();
            finish();
        });

        Button pickImage = findViewById(R.id.button_pick_image);
        pickImage.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,IMAGE_PICKER_RESULT);
        });

    }
}
