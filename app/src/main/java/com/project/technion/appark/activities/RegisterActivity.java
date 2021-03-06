package com.project.technion.appark.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.technion.appark.R;
import com.project.technion.appark.User;


public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Button bRegister;
    private EditText etEmail, etPassword, etName, etPhone;
    private TextView tvLogin;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;



    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final String phone = etPhone.getText().toString();
        final String name = etName.getText().toString();
        boolean emptyEtEmail = TextUtils.isEmpty(email);
        boolean emptyEtPassword = TextUtils.isEmpty(password);
        boolean emptyEtName = TextUtils.isEmpty(name);
        boolean emptyEtPhoneNumber = TextUtils.isEmpty(phone);
        if (emptyEtEmail) {
            etEmail.setError(getString(R.string.error_please_email));
            etEmail.requestFocus();
        }
        if (emptyEtPassword) {
            etPassword.setError(getString(R.string.error_please_password));
            etPassword.requestFocus();
        }
        if (emptyEtName) {
            etName.setError(getString(R.string.error_please_name));
            etName.requestFocus();
        }
        if (emptyEtPhoneNumber) {
            etPhone.setError(getString(R.string.error_please_phone_number));
            etPhone.requestFocus();
        }
        if(emptyEtEmail || emptyEtPassword || emptyEtName || emptyEtPhoneNumber) return;

        pd.setMessage("Registering user...");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pd.dismiss();
                if (task.isSuccessful()) {
                    mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid())
                            .setValue(new User(name, phone));
                    finish();
                    Intent i = new Intent(getApplicationContext(), MasterActivity.class);
                    i.putExtra("FROM", "REGISTER");
                    startActivity(i);
                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.d("RegisterActivity", "The execption class is: " + e.getClass().toString());
                        if (e.getClass() == FirebaseAuthWeakPasswordException.class) {
                            etPassword.setError(getString(R.string.error_weak_password));
                            etPassword.requestFocus();
                        } else if (e.getClass() == FirebaseAuthUserCollisionException.class) {
                            etEmail.setError(getString(R.string.error_email_taken));
                            etEmail.requestFocus();
                        } else if (e.getClass() == FirebaseAuthInvalidCredentialsException.class) {
                            etEmail.setError(getString(R.string.email_not_valid));
                            etEmail.requestFocus();
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
        etName = findViewById(R.id.nameInput);
        etPhone = findViewById(R.id.phoneInput);
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
