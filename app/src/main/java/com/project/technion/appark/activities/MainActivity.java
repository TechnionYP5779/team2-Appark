package com.project.technion.appark.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.R;
import com.project.technion.appark.User;

public class MainActivity extends AppCompatActivity {

    private static final String  TAG = "MainActivity";

    private DataBase db;
    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView_user);

        db = DummyDB.getInstance();

        int id = getIntent().getIntExtra("user_id",-1);
        Log.d(TAG,"user id is "+id);

        mUser = db.getUser(id);
        textView.setText(mUser.toString());

        setUpButtons();
    }

    private void setUpButtons(){
        Button offerButton = findViewById(R.id.button_offer);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddParkingSpotActivity.class);
                i.putExtra("user_id",mUser.getId());
                startActivity(i);
            }
        });

        Button viewOffersButtons = findViewById(R.id.button_view_offers);
        viewOffersButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ViewMyOffersActivity.class);
                i.putExtra("user_id",mUser.getId());
                startActivity(i);
            }
        });
    }
}
