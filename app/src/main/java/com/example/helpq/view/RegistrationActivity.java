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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etNewUsername;
    private EditText etNewPassword;
    private EditText etInstructor;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        etInstructor = findViewById(R.id.etInstructor);
        etNewPassword = findViewById(R.id.etNewPassword);
        etNewUsername = findViewById(R.id.etNewUsername);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        ParseUser newUser = new ParseUser();
        final String username = etNewUsername.getText().toString();
        final String password = etNewPassword.getText().toString();
        newUser.setUsername(username);
        newUser.setPassword(password);
        if(!etInstructor.getText().toString().equals("")){
            newUser.put("isInstructor", false);
            newUser.put("instructorName", etInstructor.getText().toString());
        } else {
            newUser.put("isInstructor", true);
        }
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

    //logs user in
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
}
