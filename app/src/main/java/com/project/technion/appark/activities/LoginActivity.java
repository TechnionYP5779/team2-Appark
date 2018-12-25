package com.project.technion.appark.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.technion.appark.R;
import com.facebook.FacebookException;
import com.project.technion.appark.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private Button bLogin;
    private com.facebook.login.widget.LoginButton loginButton;
    private EditText etEmail, etPassword;
    private TextView tvRegister;

    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    private CallbackManager mCallbackManager;
    private DatabaseReference mDatabaseReference;



    private static final String EMAIL = "email";




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
                }
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNewUser) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String phone = "0546536925"; //user.getPhoneNumber()
                                mDatabaseReference.child("Users").child(mAuth.getCurrentUser().getUid())
                                        .setValue(new User(user.getDisplayName(), phone));
                            }
                            finish();
                            startActivity(new Intent(getApplicationContext(), MasterActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
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

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        if(mAuth.getCurrentUser() != null){//user is already logged in
            //profile activity here
            finish();
            Intent i = new Intent(getApplicationContext(), MasterActivity.class);
            i.putExtra("FROM", "NOTLOGIN");
            startActivity(i);
        }

        bLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);

//        try{
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.project.technion.appark", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:",Base64.encodeToString(md.digest(), Base64.DEFAULT));
//
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}