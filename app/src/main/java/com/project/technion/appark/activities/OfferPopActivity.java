package com.project.technion.appark.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
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

public class OfferPopActivity extends Activity {
    private static final String TAG = "OfferPopActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private Integer parkingSpotIndex;
    private Button pick_start, pick_end;
    private TextView text_start, text_end;
    private int dayStart, monthStart, yearStart, hourStart, minuteStart;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private boolean startTimeWasSet, finishTimeWasSet;
    Calendar calendarStart, calendarFinish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_pop);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width * 0.6),(int)(height * 0.5));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        Button bOffer = findViewById(R.id.make_offer_button);
        bOffer.setOnClickListener(v -> {
            if (!startTimeWasSet || !finishTimeWasSet) {
                Toast.makeText(OfferPopActivity.this, "Fill all the fields before you submit", Toast.LENGTH_SHORT).show();
                return;
            }
            mDatabaseReference.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    parkingSpotIndex = getIntent().getIntExtra("parking_spot_index",-1);
                    ParkingSpot p = u.parkingSpots.get(parkingSpotIndex);
                    String offerId = mDatabaseReference.push().getKey();
                    calendarStart = Calendar.getInstance();
                    calendarStart.set(yearStart, monthStart - 1, dayStart, hourStart, minuteStart);
                    calendarFinish = Calendar.getInstance();
                    calendarFinish.set(yearFinal, monthFinal - 1, dayFinal, hourFinal, minuteFinal);
                    long start_time = calendarStart.getTimeInMillis();
                    long end_time = calendarFinish.getTimeInMillis();
                    mDatabaseReference.child("Offers").child(offerId).setValue(new Offer(offerId,p.id,mUser.getUid(),start_time,end_time,p.lat,p.lng));
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
        addStartTime();
        addFinishTime();
    }

    public void addStartTime() {
        pick_start = findViewById(R.id.choose_start_tnd);
        text_start = findViewById(R.id.tnd_start_text);
        pick_start.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpg = new DatePickerDialog(OfferPopActivity.this, (view, year, month, dayOfMonth) -> {
                TimePickerDialog tpg = new TimePickerDialog(OfferPopActivity.this, (view1, hourOfDay, minute) -> {
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
        pick_end = findViewById(R.id.choose_end_tnd);
        text_end = findViewById(R.id.tnd_end_text);
        pick_end.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog dpg = new DatePickerDialog(OfferPopActivity.this, (view, year, month, dayOfMonth) -> {
                TimePickerDialog tpg = new TimePickerDialog(OfferPopActivity.this, (view1, hourOfDay, minute) -> {
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

