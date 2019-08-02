package com.example.helpq.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();

        ParseUser user = ParseUser.getCurrentUser();

        if (User.isAdmin(user)) {
            mFragmentManager
                    .beginTransaction()
                    .replace(R.id.flMainContainer, AdminFragment.newInstance(), AdminFragment.TAG)
                    .commit();
        } else {
            mFragmentManager
                    .beginTransaction()
                    .replace(R.id.flMainContainer, StudentFragment.newInstance(), StudentFragment.TAG)
                    .commit();
        }
    }
}
