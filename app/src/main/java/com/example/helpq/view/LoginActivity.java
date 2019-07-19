package com.example.helpq.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.helpq.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnAdmin;
    private Button btnStudent;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);
        btnAdmin = findViewById(R.id.btnAdmin);
        btnStudent = findViewById(R.id.btnStudent);
        btnRegister = findViewById(R.id.btnRegister);
        btnSubmit();
        startRegistrationActivity();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // do stuff with the user
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    //launches registration page for new users
    private void startRegistrationActivity() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,
                        RegistrationActivity.class));
                finish();
            }
        });
    }

    //handles onclick listeners for login
    private void btnSubmit() {
        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                queryValidUsers(username, password, false);
            }
        });

        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                queryValidUsers(username, password, true);
            }
        });
    }

    //logs user in
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Login successful");
                    startActivity(new Intent(LoginActivity.this,
                            MainActivity.class));
                    finish(); // finishes login so user cannot press back button to go back to login
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid username/password",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //checks backend if such user exists
    private void queryValidUsers(final String username, final String password,
                                 final boolean isAdmin) {
        if(username.equals("")){
            Toast.makeText(LoginActivity.this,
                    "Please enter a username.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if(password.equals("")){
            Toast.makeText(LoginActivity.this,
                    "Please enter a password.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("isAdmin", isAdmin);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(objects.size() != 0) {
                    for(int i = 0; i < objects.size(); i++) {
                        ParseUser user = objects.get(i);
                        if(user.getUsername().equals(username)) {
                            login(username, password);
                            return;
                        }
                    }
                }
                Toast.makeText(LoginActivity.this,
                        "The user does not exist.",
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}
