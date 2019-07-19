package com.example.helpq.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        ParseUser user = ParseUser.getCurrentUser();

        if (User.isAdmin(user)) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.flMainContainer, AdminFragment.newInstance(), AdminFragment.TAG)
                    .commit();
        } else {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.flMainContainer, StudentFragment.newInstance(), StudentFragment.TAG)
                    .commit();
        }
    }
}
