package com.project.technion.appark.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
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

import java.util.Calendar;
import java.util.Objects;

public class OfferPopActivity extends Activity {
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private String parkingSpotId;
    private Time startTime = null, endTime = null;
    private TextView startText, endText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_pop);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.6), (int) (height * 0.5));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        Button bOffer = findViewById(R.id.make_offer_button);
        bOffer.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make offer?");
            builder.setMessage("Are you sure you want to offer this parking spot\n" +
                    "from " + startTime.asString() + "\n" +
                    "to " + endTime.asString() + "?");
            builder.setPositiveButton("YES", (dialog, which) -> {
                mDatabaseReference.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        parkingSpotId = getIntent().getStringExtra("parking_spot_id");
                        ParkingSpot p = null;
                        for (ParkingSpot ps : Objects.requireNonNull(u).parkingSpots) {
                            if (ps.getId().equals(parkingSpotId)) {
                                p = ps;
                                break;
                            }
                        }

                        if (p == null) {
                            Toast.makeText(getApplicationContext(), "Should not happen!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String offerId = mDatabaseReference.push().getKey();
                        long start_time = startTime.getInMillis();
                        long end_time = endTime.getInMillis();
                        mDatabaseReference.child("Offers").child(Objects.requireNonNull(offerId)).setValue(new Offer(offerId, p.id, mUser.getUid(), start_time, end_time, p.lat, p.lng, p.price));
                        p.offers.add(offerId);
                        mDatabaseReference.child("Users").child(mUser.getUid()).setValue(u);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Toast.makeText(OfferPopActivity.this, "the offer was published", Toast.LENGTH_SHORT).show();
                finish();
            });
            builder.setNegativeButton("NO", (dialog, which) -> finish());
            builder.show();
        });
        addStartTime();
        addFinishTime();
    }

    public void addStartTime() {
        Button pick_start = findViewById(R.id.choose_start_tnd);
        startText = findViewById(R.id.tnd_start_text);
        pick_start.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpg = new DatePickerDialog(
                    OfferPopActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        TimePickerDialog tpg = new TimePickerDialog(OfferPopActivity.this, (view1, hourOfDay, minute) -> {
                            startTime = new Time(year, month, dayOfMonth, hourOfDay, minute);
                            startText.setText(startTime.asString());
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                        tpg.show();
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dpg.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
            if (endTime != null) {
                dpg.getDatePicker().setMaxDate(endTime.getInMillis());
            }
            dpg.show();
        });
    }

    public void addFinishTime() {
        Button pick_end = findViewById(R.id.choose_end_tnd);
        endText = findViewById(R.id.tnd_end_text);
        pick_end.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpg = new DatePickerDialog(
                    OfferPopActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        TimePickerDialog tpg = new TimePickerDialog(OfferPopActivity.this, (view1, hourOfDay, minute) -> {
                            endTime = new Time(year, month, dayOfMonth, hourOfDay, minute);
                            endText.setText(endTime.asString());
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                        tpg.show();
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            if (startTime != null) {
                dpg.getDatePicker().setMinDate(startTime.getInMillis());
            } else {
                dpg.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
            }
            dpg.show();
        });
    }

    private class Time {
        private int year, month, day, hour, minute;

        Time(int year, int month, int day, int hour, int minute) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = minute;
        }

        String asString() {
            String minuteString = ((minute < 10) ? "0" : "") + minute;
            String hourString = ((hour < 10) ? "0" : "") + hour;
            return day + "/" + (month + 1) + "/" + year + " , " + hourString + ":" + minuteString;
        }

        long getInMillis() {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day, hour, minute);
            return c.getTimeInMillis();
        }
    }
}

