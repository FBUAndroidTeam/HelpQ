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
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button btnFacebookLogin;
    private int numUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Allow user to persist between app restarts
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        findNumUsers();
        faceBookLogin();
    }

    //gets number of users in parse database
    private void findNumUsers() {
        ParseQuery<ParseUser> queryNumUsers = ParseUser.getQuery();
        queryNumUsers.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null) {
                    numUsers = objects.size();
                } else {
                    Log.d(TAG, "couldn't determine number of users");
                }
            }
        });
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

                ArrayList<String> permissions = new ArrayList();
                permissions.add("email");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions,
                        new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException err) {
                                if (err != null) {
                                    Log.d(TAG, "Uh oh. Error occurred" + err.toString());
                                    err.printStackTrace();
                                    printKeyHash();
                                } else if (user == null) {
                                    Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
                                } else {
                                    queryExistingUser(user);
                                }
                            }
                        });
            }
        });
    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.helpq", PackageManager.GET_SIGNATURES);
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

    private void queryExistingUser(ParseUser user) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "error finding user in background");
                    e.printStackTrace();
                } else {
                    if (objects.size() > numUsers) {
                        startActivity(new Intent(LoginActivity.this,
                                RegistrationActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this,
                                MainActivity.class));
                    }
                }
                finish(); // finishes login so user cannot press back button to go back to login
            }
        });
    }
}
