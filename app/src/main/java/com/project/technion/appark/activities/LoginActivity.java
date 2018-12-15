package com.project.technion.appark.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.project.technion.appark.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private Button bLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegister;

    private FirebaseAuth mAuth;
    private ProgressDialog pd;


    private void UserLogin(){
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please fill email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill password", Toast.LENGTH_LONG).show();
            return;
        }
        pd.setMessage("Processing...");
        pd.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    finish();
                    startActivity(new Intent(getApplicationContext(), MasterActivity.class));
                }
                else{
                    Toast.makeText(LoginActivity.this, "Wrong Credentials, try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bLogin = findViewById(R.id.loginButton);
        etEmail = findViewById(R.id.emailInput);
        etPassword = findViewById(R.id.passwordInput);
        tvRegister = findViewById(R.id.textViewSignup);
        pd = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){//user is already logged in
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(), MasterActivity.class));
        }

        bLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        if(view == bLogin){
            UserLogin();
        }
        if(view == tvRegister){
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }
}