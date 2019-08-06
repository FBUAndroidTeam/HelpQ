package com.example.helpq.view;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button btnFacebookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        setContentView(R.layout.activity_login);
        // Allow user to persist between app restarts
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            if(User.getFullName(currentUser) != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                ParseUser.logOut();
            }
        }
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        btnFacebookLogin.setVisibility(View.VISIBLE);
        faceBookLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void faceBookLogin() {
        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFacebookLogin.setVisibility(View.INVISIBLE);
            ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this,
                null, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (err != null) {
                            Log.d(TAG, "Error occurred" + err.toString());
                            err.printStackTrace();
                            printKeyHash();
                            btnFacebookLogin.setVisibility(View.VISIBLE);
                        } else if (user == null) {
                            Log.d(TAG, "The user cancelled the Facebook login.");
                            btnFacebookLogin.setVisibility(View.VISIBLE);
                        } else {
                            handleValidUser(user);
                        }
                    }
                }
            );
            }
        });
    }

    //handles what page the user is taken to depending on whether they are a new user or not
    private void handleValidUser(ParseUser user) {
        if (user.isNew() || User.getFullName(user) == null) {
            startActivity(new Intent(LoginActivity.this,
                    RegistrationActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this,
                    MainActivity.class));
        }
        finish();
    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.helpq",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }
}
