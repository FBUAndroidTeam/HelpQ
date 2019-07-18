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
import com.parse.SignUpCallback;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etNewUsername;
    private EditText etNewPassword;
    private EditText etInstructor;
    private EditText etFullName;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        etInstructor = findViewById(R.id.etInstructor);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewUsername = findViewById(R.id.etNewUsername);
        etFullName = findViewById(R.id.etFullName);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    //starts registration process
    private void register() {
        ParseUser newUser = new ParseUser();
        final String username = etNewUsername.getText().toString();
        final String password = etNewPassword.getText().toString();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.put("fullName", etFullName.getText().toString());
        if(!etInstructor.getText().toString().equals("")) { //student is attempting to register
            queryInstructor(etInstructor.getText().toString(), newUser, username, password);
        } else { //admin is attempting to register
            newUser.put("isInstructor", true);
            signUp(newUser, username, password);
        }
    }

    //logs user in after signup
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    Intent toHome = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(toHome);
                    finish(); // finishes login so user cannot press back button to go back to login
                } else {
                    Toast.makeText(RegistrationActivity.this, "Invalid username/password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //queries backend to see if inputted instructor is valid
    private void queryInstructor(final String instructor, final ParseUser newUser, final String username, final String password) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("isInstructor", true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                for(int i = 0; i < objects.size(); i++){
                    ParseUser user = objects.get(i);
                    if(user.getUsername().equals(instructor)){
                        newUser.put("isInstructor", false);
                        newUser.put("instructorName", etInstructor.getText().toString());
                        signUp(newUser, username, password);
                        return;
                    } else if(i == objects.size() - 1) {
                        Toast.makeText(RegistrationActivity.this, "Invalid admin", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    //officially signs up new user in backend
    private void signUp(ParseUser newUser, final String username, final String password) {
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.d("SignUp","Signup successful!");
                    login(username, password);
                } else {
                    Toast.makeText(RegistrationActivity.this, "username already taken", Toast.LENGTH_LONG).show();
                    Log.d("SignUp", "Signup failed");
                    e.printStackTrace();
                }
            }
        });
    }
}
