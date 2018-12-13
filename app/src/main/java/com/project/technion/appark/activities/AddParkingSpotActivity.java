package com.project.technion.appark.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.TimeSlot;
import com.project.technion.appark.User;
import com.project.technion.appark.XYLocation;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddParkingSpotActivity extends AppCompatActivity {
    private static final String TAG = "AddParkingSpotActivity";

    private DataBase db;
    private ParkingSpot mParkingSpot;
    private User mUser;

    private Button pick_start, pick_end;
    private TextView text_start, text_end;
    private int dayStart, monthStart, yearStart, hourStart, minuteStart;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;
    private boolean startTimeWasSet, finishTimeWasSet;

    Calendar calendarStart, calendarFinish;

    private boolean isViewEmpty(TextView view) {
        return view.getText().toString().isEmpty();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_spot);

        db = DummyDB.getInstance();
        int id = getIntent().getIntExtra("user_id", -1);
        Log.d(TAG, "user id is " + id);
        mUser = db.getUser(id);

        final TextView priceTextView = findViewById(R.id.ps_price);
        final TextView xTextView = findViewById(R.id.ps_x);
        final TextView yTextView = findViewById(R.id.ps_y);

        Button offerButton = findViewById(R.id.button_offer);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, dayStart + " " + monthStart + " " + yearStart + " " + hourStart + " " + minuteStart);

                Log.d(TAG, dayFinal + " " + monthFinal + " " + yearFinal + " " + hourFinal + " " + minuteFinal);

                if (!startTimeWasSet || !finishTimeWasSet || isViewEmpty(priceTextView) || isViewEmpty(xTextView) || isViewEmpty(yTextView)) {
                    Toast.makeText(AddParkingSpotActivity.this, "Fill all the fields before you submit", Toast.LENGTH_SHORT).show();
                    return;
                }
                Double price = Double.parseDouble(priceTextView.getText().toString());
                Double x = Double.parseDouble(xTextView.getText().toString());
                Double y = Double.parseDouble(yTextView.getText().toString());
                XYLocation location = new XYLocation(x, y);

                Calendar startCal = new GregorianCalendar(yearStart,monthStart-1,dayStart,hourStart,minuteStart);
                Calendar endCal = new GregorianCalendar(yearFinal,monthFinal-1,dayFinal,hourFinal,minuteFinal);
                TimeSlot slot = new TimeSlot(startCal, endCal, false);

                mParkingSpot = new ParkingSpot(db.getNextParkingSpotID(), mUser, price, location, slot);
                db.add(mParkingSpot);
                Toast.makeText(AddParkingSpotActivity.this, "the offer was published", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        addStartTime();
        addFinishTime();
    }

    public void addStartTime() {
        pick_start = findViewById(R.id.choose_start_tnd);
        text_start = findViewById(R.id.tnd_start_text);
        pick_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpg = new DatePickerDialog(AddParkingSpotActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                                TimePickerDialog tpg = new TimePickerDialog(AddParkingSpotActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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
                                    }
                                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                                tpg.show();
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpg.show();
            }
        });
    }

    public void addFinishTime() {
        pick_end = findViewById(R.id.choose_end_tnd);
        text_end = findViewById(R.id.tnd_end_text);
        pick_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog dpg = new DatePickerDialog(AddParkingSpotActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                                TimePickerDialog tpg = new TimePickerDialog(AddParkingSpotActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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
                                    }
                                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                                tpg.show();
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dpg.show();
            }
        });
    }
}
