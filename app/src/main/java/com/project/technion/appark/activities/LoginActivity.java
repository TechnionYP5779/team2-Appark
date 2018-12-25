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
import com.project.technion.appark.Experiments.ExperimentsActivity;
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
        boolean emptyEtEmail = TextUtils.isEmpty(email);
        boolean emptyEtPassword = TextUtils.isEmpty(password);
        if(emptyEtEmail){
            etEmail.setError(getString(R.string.error_please_email));
            etEmail.requestFocus();
        }
        if(emptyEtPassword){
            etPassword.setError(getString(R.string.error_please_password));
            etPassword.requestFocus();
        }
        if(emptyEtEmail || emptyEtPassword)  return;

        pd.setMessage("Processing...");
        pd.show();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    finish();
                    Intent i = new Intent(getApplicationContext(), MasterActivity.class);
                    i.putExtra("FROM", "LOGIN");
                    startActivity(i);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Wrong Credentials, try again", Toast.LENGTH_LONG).show();
                    finish();
                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    i.putExtra("FROM", "LOGIN");
                    startActivity(i);
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
            Intent i = new Intent(getApplicationContext(), MasterActivity.class);
            i.putExtra("FROM", "NOTLOGIN");
            startActivity(i);
        }

        //delete from here
        finish();
        Intent i = new Intent(getApplicationContext(), ExperimentsActivity.class);
        startActivity(i);
        //to here

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