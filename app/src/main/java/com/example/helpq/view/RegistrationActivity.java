package com.example.helpq.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.helpq.R;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager
                .beginTransaction()
                .replace(R.id.flRegistrationContainer, RegistrationFragment.newInstance(),
                        RegistrationFragment.TAG)
                .commit();
    }
}
