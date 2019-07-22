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
import com.example.helpq.model.User;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";

    private EditText etNewUsername;
    private EditText etNewPassword;
    private EditText etAdmin;
    private EditText etFullName;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        etAdmin = findViewById(R.id.etAdmin);
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
        final String username = etNewUsername.getText().toString();
        final String password = etNewPassword.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(RegistrationActivity.this,
                    R.string.edge_case_empty_username,
                    Toast.LENGTH_LONG).show();
            return;
        }
        else if (password.isEmpty()) {
            Toast.makeText(RegistrationActivity.this,
                    R.string.edge_case_empty_password,
                    Toast.LENGTH_LONG).show();
            return;
        }
        else if (etFullName.getText().toString().isEmpty()) {
            Toast.makeText(RegistrationActivity.this,
                    R.string.edge_case_empty_name,
                    Toast.LENGTH_LONG).show();
            return;
        }
        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        User.setFullName(etFullName.getText().toString(), newUser);
        if(!etAdmin.getText().toString().isEmpty()) { //student is attempting to register
            queryAdmin(etAdmin.getText().toString(), newUser, username, password);
        } else { //admin is attempting to register
            User.setIsAdmin(true, newUser);
            signUp(newUser, username, password);
        }
    }

    //logs user in after signup
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Login successful");
                    startActivity(new Intent(RegistrationActivity.this,
                            MainActivity.class));
                    finish(); // finishes login so user cannot press back button to go back to login
                }
            }
        });
    }

    //queries backend to see if inputted admin is valid
    private void queryAdmin(final String admin, final ParseUser newUser,
                                 final String username, final String password) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(User.KEY_IS_ADMIN, true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                for(int i = 0; i < objects.size(); i++){
                    ParseUser user = objects.get(i);
                    if(user.getUsername().equals(admin)){
                        User.setIsAdmin(false, newUser);
                        User.setAdminName(etAdmin.getText().toString(), newUser);
                        signUp(newUser, username, password);
                        return;
                    } else if(i == objects.size() - 1) {
                        Toast.makeText(RegistrationActivity.this,
                                R.string.edge_case_invalid_admin, Toast.LENGTH_LONG).show();
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
                    Log.d(TAG,"Signup successful!");
                    login(username, password);
                } else {
                    Toast.makeText(RegistrationActivity.this,
                            R.string.edge_case_invalid_username, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Signup failed");
                    e.printStackTrace();
                }
            }
        });
    }
}
