package com.project.technion.appark.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
import com.project.technion.appark.R;
import com.project.technion.appark.adapters.OffersAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchParkingsActivity extends AppCompatActivity {
    private static final String TAG = "SearchParkingsActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private TextView text_start, text_end;
    private ListView mSearchResList;
    private int dayStart, monthStart, yearStart, hourStart, minuteStart;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private boolean startTimeWasSet, finishTimeWasSet;
    private OffersAdapter mAdapter;
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

        mSearchButton.setOnClickListener(v -> {
            if (!startTimeWasSet || !finishTimeWasSet) {
                Toast.makeText(SearchParkingsActivity.this, "Fill all the fields before you search", Toast.LENGTH_SHORT).show();
                return;
            }
            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarFinish = Calendar.getInstance();
            calendarStart.set(yearStart, monthStart - 1, dayStart, hourStart, minuteStart);
            calendarFinish.set(yearFinal, monthFinal - 1,dayFinal,hourFinal, minuteFinal);
            long calSrtMillis = calendarStart.getTimeInMillis();
            long calEndMillis = calendarFinish.getTimeInMillis();
            String address = mAddress.getText().toString();
            Geocoder gc = new Geocoder(getApplicationContext());
            mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<Offer> offers = new ArrayList<>();
                    Log.d("SearchParkingsActivity", "list length: "+ offers.size());
                    Location location = null;
                    if(gc.isPresent()) {
                        List<Address> list = null;
                        try {
                            list = gc.getFromLocationName(address, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address found_address = list.get(0);
                        location = new Location("");
                        location.setLatitude(found_address.getLatitude());
                        location.setLongitude(found_address.getLongitude());
                    }
                    for(DataSnapshot offer : dataSnapshot.getChildren()){
                        Offer o = offer.getValue(Offer.class);
                        long offerSrtMillis = o.startCalenderInMillis;
                        long offerEndMillis = o.endCalenderInMillis;
                        if(offerSrtMillis <= calSrtMillis && calEndMillis <= offerEndMillis){
                            double delta = 5.0;
                            Location thisLocation = new Location("");
                            thisLocation.setLatitude(o.lat);
                            thisLocation.setLongitude(o.lng);
                            if(location == null || thisLocation.distanceTo(location) <= delta)
                                offers.add(offer.getValue(Offer.class));
                        }
                    }
                    if(getApplicationContext() != null) {
                        mAdapter = new OffersAdapter(getApplicationContext(), new ArrayList<>(offers));
                        mSearchResList.setAdapter(mAdapter);
                        /*
                        TextView noOffers = findViewById(R.id.textView_no_offers);
                        if (offers.size() == 0) {
                            noOffers.setVisibility(View.VISIBLE);
                        } else {
                            noOffers.setVisibility(View.INVISIBLE);
                        }
                        */
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
                    startTimeWasSet = true;
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
                    finishTimeWasSet = true;
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                tpg.show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpg.show();
        });
    }
    
}
