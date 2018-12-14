package com.project.technion.appark.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.technion.appark.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button user1 = findViewById(R.id.button_user1);
        user1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("user_id",1);
                startActivity(i);
            }
        });
        Button user2 = findViewById(R.id.button_user2);
        user2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MasterActivity.class);
                i.putExtra("user_id",2);
                startActivity(i);
            }
        });
        Button user3 = findViewById(R.id.button_user3);
        user3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MasterActivity.class);
                i.putExtra("user_id",3);
                startActivity(i);
            }
        });
    }
}