package com.project.technion.appark.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.XYLocation;

public class AddParkingSpotActivity extends AppCompatActivity {
    private static final String  TAG = "AddParkingSpotActivity";

    private DataBase db;
    private ParkingSpot mParkingSpot;
    private User mUser;

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

    }
}
