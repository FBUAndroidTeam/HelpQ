package com.example.helpq.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegistrationFragment extends Fragment implements DialogDismissListener {

    public static final String TAG = "RegistrationFragment";
    private static final String KEY_FULL_NAME = "fullName";


    private EditText etNewUsername;
    private EditText etNewPassword;
    private TextView tvAdmin;
    private EditText etFullName;
    private Button btnRegister;
    private FragmentManager fragmentManager;
    private AdminListFragment adminListFragment;
    private String adminUsername;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvAdmin = view.findViewById(R.id.tvAdmin);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etNewUsername = view.findViewById(R.id.etNewUsername);
        etFullName = view.findViewById(R.id.etFullName);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validRegistrationCheck();
            }
        });
        tvAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getFragmentManager();
                adminListFragment = AdminListFragment.newInstance("adminListFragment");
                adminListFragment.setTargetFragment(RegistrationFragment.this,
                        300);
                adminListFragment.show(fragmentManager, AdminListFragment.TAG);
            }
        });
    }

    //checks to see if information user inputted is valid
    private void validRegistrationCheck() {
        final String username = etNewUsername.getText().toString();
        final String password = etNewPassword.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(getContext(), R.string.edge_case_empty_username, Toast.LENGTH_LONG).show();
            return;
        }
        else if (password.isEmpty()) {
            Toast.makeText(getContext(), R.string.edge_case_empty_password, Toast.LENGTH_LONG).show();
            return;
        }
        else if (etFullName.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), R.string.edge_case_empty_name,
                    Toast.LENGTH_LONG).show();
            return;
        } else {
            register(username, password);
        }
    }

    //starts registration process
    private void register(final String username, final String password) {
        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);
        User.setFullName(etFullName.getText().toString(), newUser);
        User.setIsAdmin(false, newUser);
        User.setAdminName(adminUsername, newUser);
        signUp(newUser, username, password);
    }

    //logs user in after signup
    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Login successful");
                    startActivity(new Intent(getContext(), MainActivity.class));
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
                    Toast.makeText(getContext(), R.string.username_taken,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Signup failed");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDismiss() {
        adminUsername = adminListFragment.getSelectedAdmin().getUsername();
        tvAdmin.setText("Admin: " + adminListFragment.getSelectedAdmin().getString(KEY_FULL_NAME));
        btnRegister.setEnabled(true);
    }
}
