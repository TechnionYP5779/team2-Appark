package com.project.technion.appark.activities;

import com.project.technion.appark.R;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button mLoginButton;
    private Button mLogoutButton;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mLoginButton = findViewById(R.id.loginButton);
        mEmailInput = findViewById(R.id.emailInput);
        mPasswordInput = findViewById(R.id.passwordInput);

        if (mAuth.getCurrentUser() != null) {
            setResult(RESULT_OK);
            finish();
        }

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        Button user1 = findViewById(R.id.button_user1);
        user1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, MasterActivity.class);
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

    private void doLogin() {
        mAuth.signInWithEmailAndPassword(mEmailInput.getText().toString(), mPasswordInput.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            switchToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            LoginActivity.this.setResult(RESULT_CANCELED);
                            finish();
                        }

                        // ...
                    }
                });
    }

    private void switchToMainActivity() {
        setResult(RESULT_OK);
        finish();
    }
}
