package com.project.technion.appark.activities;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.technion.appark.R;
import com.project.technion.appark.utils.MyDatePickerFragment;
import com.project.technion.appark.utils.MyTimePickerFragment;

import java.util.Calendar;
import java.util.Date;

public class SearchParkingsActivity extends AppCompatActivity implements
        MyTimePickerFragment.OnCallbackReceived, MyDatePickerFragment.OnCallbackReceived {

    private static final String TAG = "SearchParkingsActivity";
    private static final int RENT_PARKING_RETURN_CODE = 0;
    private TextView mChooseStartTime;
    private TextView mChooseEndTime;
    private Button mSendButton;
    private EditText mAddress;

    private TextView mChooseStartDate;
    private TextView mChooseEndDate;

    private Integer mStartHour;
    private Integer mEndHour;

    private Integer mStartYear;
    private Integer mEndYear;

    private Integer mStartMonth;
    private Integer mEndMonth;

    private Integer mStartDay;
    private Integer mEndDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_parkings);

        mChooseStartTime = findViewById(R.id.chooseStartTime);
        mChooseEndTime = findViewById(R.id.chooseEndTime);
        mChooseStartDate = findViewById(R.id.chooseStartDate);
        mChooseEndDate = findViewById(R.id.chooseEndDate);
        mSendButton = findViewById(R.id.sendButton);
        mAddress = findViewById((R.id.addressInput));
        mChooseStartTime.setOnClickListener(v -> showTimePickerDialog("start"));
        mChooseEndTime.setOnClickListener(v -> showTimePickerDialog("end"));
        mChooseStartDate.setOnClickListener(v -> showDatePickerDialog("start"));
        mChooseEndDate.setOnClickListener(v -> showDatePickerDialog("end"));

        mSendButton.setOnClickListener(v -> {
            String address = mAddress.getText().toString();
            boolean emptyEtAddress = TextUtils.isEmpty(address);
            boolean emptyEtStartTime = TextUtils.isEmpty(mChooseStartTime.getText().toString());
            boolean emptyEtEndTime = TextUtils.isEmpty(mChooseEndTime.getText().toString());
            boolean emptyEtStartDate = TextUtils.isEmpty(mChooseStartDate.getText().toString());
            boolean emptyEtEndDate = TextUtils.isEmpty(mChooseEndDate.getText().toString());
            if (emptyEtAddress) {
                mAddress.setError("Please fill address");
                mAddress.requestFocus();
            }
            if (emptyEtStartTime) {
                mChooseStartTime.setError("You need to choose start time");
                mChooseStartTime.requestFocus();
            } else {
                mChooseStartTime.setError(null);
            }

            if (emptyEtEndTime) {
                mChooseEndTime.setError("You need to choose end time");
                mChooseEndTime.requestFocus();
            } else {
                mChooseEndTime.setError(null);
            }
            if (emptyEtStartDate) {
                mChooseStartDate.setError("You need to choose start date");
                mChooseStartDate.requestFocus();
            } else {
                mChooseStartDate.setError(null);
            }

            if (emptyEtEndDate) {
                mChooseEndDate.setError("You need to choose end date");
                mChooseEndDate.requestFocus();
            } else {
                mChooseEndDate.setError(null);
            }
            if (emptyEtAddress || emptyEtStartTime || emptyEtEndTime || emptyEtStartDate || emptyEtEndDate)
                return;
            Date startDate = getDate(mStartYear, mStartMonth, mStartDay, mStartHour);
            Date endDate = getDate(mEndYear, mEndMonth, mEndDay, mEndHour);

            //TODO: dest class
            Intent i = new Intent(SearchParkingsActivity.this, SearchParkingsActivity.class);
            Toast.makeText(SearchParkingsActivity.this, "stop poking me!", Toast.LENGTH_SHORT).show();
            i.putExtra("address", address);
            i.putExtra("start-date", startDate);
            i.putExtra("end-date", endDate);
            startActivityForResult(i, RENT_PARKING_RETURN_CODE);
        });
    }


    public void showTimePickerDialog(String startOrEnd) {
        DialogFragment newFragment = new MyTimePickerFragment();
        Bundle b = new Bundle();
        b.putString("start-or-end", startOrEnd);
        newFragment.setArguments(b);
        newFragment.show(getSupportFragmentManager(), "time picker");
    }

    public void showDatePickerDialog(String startOrEnd) {
        DialogFragment newFragment = new MyDatePickerFragment();
        Bundle b = new Bundle();
        b.putString("start-or-end", startOrEnd);
        newFragment.setArguments(b);
        newFragment.show(getSupportFragmentManager(), "date picker");
    }


    public void setStartTime(int startHour) {
        mStartHour = startHour;
    }

    public void setStartDate(int startYear, int startMonth, int startDay) {
        mStartYear = startYear;
        mStartMonth = startMonth;
        mStartDay = startDay;
    }

    public void setEndDate(int endYear, int endMonth, int endDay) {
        mEndYear = endYear;
        mEndMonth = endMonth;
        mEndDay = endDay;
    }

    public void setEndTime(int endHour) {
        mEndHour = endHour;
    }

    @Override
    public void UpdateTime(String startOrEnd, Integer hour) {
        if (startOrEnd.equals("start")) {
            setStartTime(hour);
            mChooseStartTime.setText(mStartHour.toString() + ":00");
        } else {
            setEndTime(hour);
            mChooseEndTime.setText(mEndHour.toString() + ":00");
        }
    }

    @Override
    public void UpdateDate(String startOrEnd, Integer year, Integer month, Integer dayOfMonth) {
        if (startOrEnd.equals("start")) {
            setStartDate(year, month, dayOfMonth);
            mChooseStartDate.setText(mStartDay.toString() + "/" + mStartMonth.toString() + "/" + mStartYear.toString());
        } else {
            setEndDate(year, month, dayOfMonth);
            mChooseEndDate.setText(mEndDay.toString() + "/" + mEndMonth.toString() + "/" + mEndYear.toString());
        }
    }

    public static Date getDate(int year, int month, int day, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
