package com.project.technion.appark.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.project.technion.appark.Offer;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.adapters.OffersAdapter;
import com.project.technion.appark.adapters.ParkingSpotsOfferAdapter;
import com.project.technion.appark.utils.MyDatePickerFragment;
import com.project.technion.appark.utils.MyTimePickerFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SearchParkingsActivity extends AppCompatActivity {

    private static final String TAG = "SearchParkingsActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private TextView text_start, text_end;
    private ListView mSearchResList;
    private int dayStart, monthStart, yearStart, hourStart, minuteStart;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private OffersAdapter mAdapter;

    private static final int RENT_PARKING_RETURN_CODE = 0;
    private Button mSearchButton;
    private EditText mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_parkings);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mSearchButton = findViewById(R.id.searchButton);
        mAddress = findViewById((R.id.addressInput));
        text_start = findViewById(R.id.chooseStartTnd);
        text_end = findViewById(R.id.chooseEndTnd);
        mSearchResList = findViewById(R.id.lvSearchRes);
        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarFinish = Calendar.getInstance();

        dayStart = 30;
        monthStart = 12;
        yearStart = 2050;
        hourStart = 23;
        minuteStart = 59;
        calendarStart.set(yearStart, monthStart-1, dayStart, hourStart, minuteStart);
        dayFinal = 1;
        monthFinal = 1;
        yearFinal = 2018;
        hourFinal = 0;
        minuteFinal = 0;
        calendarFinish.set(yearFinal, monthFinal-1,dayFinal,hourFinal, minuteFinal);


        mSearchButton.setOnClickListener(v -> {
            calendarStart.set(yearStart, monthStart-1, dayStart, hourStart, minuteStart);
            calendarFinish.set(yearFinal, monthFinal-1,dayFinal,hourFinal, minuteFinal);

            long calSrtMillis = calendarStart.getTimeInMillis();
            long calEndMillis = calendarFinish.getTimeInMillis();

            String address = mAddress.getText().toString();
            Geocoder gc = new Geocoder(getApplicationContext());

            mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Offer> offers = new ArrayList<>();
                    Location location = null;
                    if(gc.isPresent()) {
                        List<Address> list = null;
                        try {
                            if(address != "" && address != null)
                                list = gc.getFromLocationName(address, 1);
                        } catch (IOException e) {
                            //e.printStackTrace();
                        }
                        Address found_address = null;
                        if (list != null){
                            location = new Location("");
                            found_address = list.get(0);
                            location.setLatitude(found_address.getLatitude());
                            location.setLongitude(found_address.getLongitude());
                        }
                    }
                    for(DataSnapshot offer : dataSnapshot.getChildren()){
                        Offer o = offer.getValue(Offer.class);
                        long offerSrtMillis = o.startCalenderInMillis;
                        long offerEndMillis = o.endCalenderInMillis;
                        String offerUserID = o.userId;
                        String offerPsID = o.parkingSpotId;
                        if(offerSrtMillis <= calSrtMillis && calEndMillis <= offerEndMillis){
                            double delta = 5.0;
                            Location thisLocation = new Location("");
                            thisLocation.setLatitude(o.lat);
                            thisLocation.setLongitude(o.lng);
                            if(location == null || thisLocation.distanceTo(location) <= delta) {
                                offers.add(offer.getValue(Offer.class));
                            }
                        }
                    }
                    if(getApplicationContext() != null) {
                        mAdapter = new OffersAdapter(getApplicationContext(), new ArrayList<>(offers));
                        if (offers.size() != 0)
                            mSearchResList.setAdapter(mAdapter);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //TODO: change this to something more UI-ish
            Toast.makeText(SearchParkingsActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
            //finish();
        });
        addStartTime();
        addFinishTime();
    }

    public void addStartTime() {
        text_start.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpg = new DatePickerDialog(SearchParkingsActivity.this, (view, year, month, dayOfMonth) -> {
                TimePickerDialog tpg = new TimePickerDialog(SearchParkingsActivity.this, (view1, hourOfDay, minute) -> {
                    dayStart = dayOfMonth;
                    monthStart = month + 1;
                    yearStart = year;
                    hourStart = hourOfDay;
                    minuteStart = minute;
                    String add_zero = "";
                    if (minuteStart < 10)
                        add_zero = "0";
                    text_start.setText(dayStart + "/" + monthStart + "/" + yearStart + " , " + hourStart + ":" + add_zero + minuteStart);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                tpg.show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpg.show();
        });

    }

    public void addFinishTime() {
        text_end.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpg = new DatePickerDialog(SearchParkingsActivity.this, (view, year, month, dayOfMonth) -> {
                TimePickerDialog tpg = new TimePickerDialog(SearchParkingsActivity.this, (view1, hourOfDay, minute) -> {
                    dayFinal = dayOfMonth;
                    monthFinal = month + 1;
                    yearFinal = year;
                    hourFinal = hourOfDay;
                    minuteFinal = minute;
                    String add_zero = "";
                    if (minuteFinal < 10)
                        add_zero = "0";
                    text_end.setText(dayFinal + "/" + monthFinal + "/" + yearFinal + " , " + hourFinal + ":" + add_zero + minuteFinal);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                tpg.show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpg.show();
        });
    }
    
}
