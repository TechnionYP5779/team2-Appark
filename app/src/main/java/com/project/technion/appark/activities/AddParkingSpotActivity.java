package com.project.technion.appark.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.TimeSlot;
import com.project.technion.appark.User;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class AddParkingSpotActivity extends AppCompatActivity {
    private static final String TAG = "AddParkingSpotActivity";

    private DataBase db;
    private ParkingSpot mParkingSpot;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Button pick_start, pick_end;
    private TextView text_start, text_end;
    private int dayStart, monthStart, yearStart, hourStart, minuteStart;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private boolean startTimeWasSet, finishTimeWasSet;
    private EditText etPrice, etAddress;
    private DatabaseReference mDB;

    Calendar calendarStart, calendarFinish;

    private boolean isViewEmpty(TextView view) {
        return view.getText().toString().isEmpty();
    }

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
                Double price = Double.parseDouble(etPrice.getText().toString());
                String address = etAddress.getText().toString();
                Geocoder gc = new Geocoder(getApplicationContext());
                if(gc.isPresent()) {
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocationName(address, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address found_address = list.get(0);
                    double lat = found_address.getLatitude();
                    double lng = found_address.getLongitude();
                    Log.d("AddParkingSpotActivity","this is what i want" + found_address.getAddressLine(0));
                    mParkingSpot = new ParkingSpot(mUser.getUid(), price, found_address.getAddressLine(0), lat, lng);
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
