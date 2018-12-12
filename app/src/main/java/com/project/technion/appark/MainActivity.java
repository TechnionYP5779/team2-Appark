package com.project.technion.appark;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String  TAG = "MainActivity";

    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView_user);

        db = DummyDB.getInstance();

        int id = getIntent().getIntExtra("user_id",-1);
        Log.d(TAG,"user id is "+id);

        User user = db.getUser(id);
        textView.setText(user.toString());

    }
}
