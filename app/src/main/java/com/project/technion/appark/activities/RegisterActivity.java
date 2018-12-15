package com.project.technion.appark.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.technion.appark.R;
import com.project.technion.appark.User;

public class RegisterActivity extends AppCompatActivity {
    private Button bRegister;
    private EditText etEmail, etPassword;
    private TextView tvLogin;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;


    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please fill email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill password", Toast.LENGTH_LONG).show();
            return;
        }
        pd.setMessage("Registering user...");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid())
                            .setValue(new User("my name","my contect info"));
                    finish();
                    startActivity(new Intent(getApplicationContext(), MasterActivity.class));
                } else {
                    Exception e = task.getException();
                    if (e!=null) {
                        if (e.getClass() == FirebaseAuthWeakPasswordException.class) {
                            etPassword.setError(getString(R.string.error_weak_password));
                            etPassword.requestFocus();
                        }
                    }
                    Toast.makeText(RegisterActivity.this, "Could not register... please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bRegister = findViewById(R.id.registerButton);
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        tvLogin = findViewById(R.id.textViewSignin);
        pd = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
