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
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.project.technion.appark.RepeatEvery;
import com.project.technion.appark.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OfferPopActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private String parkingSpotId;
    private Time startTime = null, endTime = null;
    private TextView startText, endText;
    private String mRepeatTimes = "1";
    private int mTimesToRepeat = 1;
    private RadioGroup radioGroup;
    private Spinner mSpinner;
    private RepeatEvery mRepeatEvery = RepeatEvery.NO_REPEAT; // default
    private TextView mRepeatText;
    private ParkingSpot p = null;

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
        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.6));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(R.id.radioButton_noRepeat); // by default noRepeat is selected

        mSpinner = findViewById(R.id.times_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.repeat_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setVisibility(View.GONE);
        mRepeatText = findViewById(R.id.textView_repeatNumber);
        mRepeatText.setVisibility(View.GONE);

        Button bOffer = findViewById(R.id.make_offer_button);
        bOffer.setOnClickListener(getOnClickListener());
        addStartTime();
        addFinishTime();

    }

    @NonNull
    private View.OnClickListener getOnClickListener() {
        return v -> {
            if (startTime == null || endTime == null) {
                Toast.makeText(OfferPopActivity.this, "Fill start & end time!", Toast.LENGTH_SHORT).show();
                return;
            }

            long delta = endTime.getInMillis() - startTime.getInMillis();
            if (delta <= 0) {
                Toast.makeText(OfferPopActivity.this, "End time must be later than start time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mRepeatEvery != RepeatEvery.NO_REPEAT) {
                long maxDelta = mRepeatEvery.getDaysNumber() * TimeUnit.DAYS.toMillis(1);
                if (delta > maxDelta) {
                    Toast.makeText(OfferPopActivity.this, "The offers are overlapping", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mTimesToRepeat = Integer.valueOf(mRepeatTimes);
            String[] startTimesStrings = new String[mTimesToRepeat];
            String[] endTimesStrings = new String[mTimesToRepeat];
            Calendar start = startTime.getCalendar();
            Calendar end = endTime.getCalendar();
            for (int i = 0; i < mTimesToRepeat; i++) {
                startTimesStrings[i] = new Time(start).asString();
                endTimesStrings[i] = new Time(end).asString();
                start.add(Calendar.DAY_OF_YEAR, mRepeatEvery.getDaysNumber());
                end.add(Calendar.DAY_OF_YEAR, mRepeatEvery.getDaysNumber());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Offer Confirmation");
            String rangesMessage = "Are you sure you want to offer this parking spot\n";
            for (int i = 0; i < mTimesToRepeat - 1; i++) {
                rangesMessage += "from  " + startTimesStrings[i] + "\n" +
                        "to       " + endTimesStrings[i] + ",\n";
            }
            rangesMessage += "from  " + startTimesStrings[mTimesToRepeat - 1] + "\n" +
                    "to       " + endTimesStrings[mTimesToRepeat - 1] + "?";

            builder.setMessage(rangesMessage);

            builder.setPositiveButton("YES", (dialog, which) -> {
                mDatabaseReference.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        parkingSpotId = getIntent().getStringExtra("parking_spot_id");

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

                        addRecurringOffers(u);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Toast.makeText(OfferPopActivity.this, "the offer was published", Toast.LENGTH_SHORT).show();
                finish();
            });
            builder.setNegativeButton("NO", (dialog, which) -> {
            });
            builder.show();
        };
    }

    private void addRecurringOffers(User u) {
        Calendar start = startTime.getCalendar();
        Calendar end = endTime.getCalendar();
        for (int i = 1; i <= mTimesToRepeat; i++) {
            String offerId = mDatabaseReference.push().getKey();
            mDatabaseReference.child("Offers").child(Objects.requireNonNull(offerId)).setValue(new Offer(offerId, p.id, mUser.getUid(), start.getTimeInMillis(), end.getTimeInMillis(), p.lat, p.lng, p.price));
            p.offers.add(offerId);
            mDatabaseReference.child("Users").child(mUser.getUid()).setValue(u);

            start.add(Calendar.DAY_OF_YEAR, mRepeatEvery.getDaysNumber());
            end.add(Calendar.DAY_OF_YEAR, mRepeatEvery.getDaysNumber());
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mRepeatTimes = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        mRepeatTimes = "1";
    }

    public void checkButton(View view) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        RadioButton mRadioButton = findViewById(radioId);
        if (mRadioButton.getText().equals("No Repeat")) {
            mRepeatTimes = "1";
            mSpinner.setVisibility(View.GONE);
            mRepeatText.setVisibility(View.GONE);
            mRepeatEvery = RepeatEvery.NO_REPEAT;
        }
        if (mRadioButton.getText().equals("Days")) {
            mSpinner.setVisibility(View.VISIBLE);
            mRepeatText.setVisibility(View.VISIBLE);
            mRepeatEvery = RepeatEvery.DAY;
        }
        if (mRadioButton.getText().equals("Weeks")) {
            mSpinner.setVisibility(View.VISIBLE);
            mRepeatText.setVisibility(View.VISIBLE);
            mRepeatEvery = RepeatEvery.WEEK;
        }
    }


    private class Time {
        private Calendar calendar;
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY, HH:mm");

        Time(int year, int month, int day, int hour, int minute) {
            this.calendar = Calendar.getInstance();
            this.calendar.set(year, month, day, hour, minute);
        }

        Time(Calendar c) {
            this.calendar = c;
        }

        String asString() {
            return dateFormat.format(this.calendar.getTime());
        }

        long getInMillis() {
            return this.calendar.getTimeInMillis();
        }

        Calendar getCalendar() {
            Calendar newC = Calendar.getInstance();
            newC.setTimeInMillis(this.getInMillis());
            return newC;
        }
    }
}

