package com.project.technion.appark.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.project.technion.appark.User;
import com.project.technion.appark.XYLocation;

import java.util.Calendar;

public class AddParkingSpotActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String  TAG = "AddParkingSpotActivity";

    private DataBase db;
    private ParkingSpot mParkingSpot;
    private User mUser;

    private Button  pick_start;
    private TextView text_start;
    private int day, month, year, hour, minute;
    private int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parking_spot);

        db = DummyDB.getInstance();
        int id = getIntent().getIntExtra("user_id",-1);
        Log.d(TAG,"user id is "+id);
        mUser = db.getUser(id);

        final TextView priceTextView = findViewById(R.id.ps_price);
        final TextView xTextView = findViewById(R.id.ps_x);
        final TextView yTextView = findViewById(R.id.ps_y);

        Button offerButton = findViewById(R.id.button_offer);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(priceTextView.getText().toString().isEmpty() ||xTextView.getText().toString().isEmpty() || yTextView.getText().toString().isEmpty())
                    return;
                Double price = Double.parseDouble(priceTextView.getText().toString());
                Double x = Double.parseDouble(xTextView.getText().toString());
                Double y = Double.parseDouble(yTextView.getText().toString());
                XYLocation location = new XYLocation(x,y);
                mParkingSpot = new ParkingSpot(db.getNextParkingSpotID(),mUser, price,location);
                db.add(mParkingSpot);
                Toast.makeText(AddParkingSpotActivity.this,"the offer was published",Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        pick_start = findViewById(R.id.choose_start_tnd);
        text_start = findViewById(R.id.tnd_start_text);


        pick_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dpg = new DatePickerDialog(AddParkingSpotActivity.this, AddParkingSpotActivity.this, year, month, day);
                dpg.show();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        yearFinal = year;
        monthFinal = month + 1;
        dayFinal = dayOfMonth;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog tpg = new TimePickerDialog(AddParkingSpotActivity.this, AddParkingSpotActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        tpg.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hourFinal = hourOfDay;
        minuteFinal = minute;

        // "dd MMM yyyy HH:mm"
        text_start.setText(dayFinal + "/" + monthFinal + "/" + yearFinal + " , " + hourFinal + ":" + minuteFinal);
    }
}
