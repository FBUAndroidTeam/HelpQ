package com.example.helpq.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.example.helpq.view.admin_views.AdminFragment;
import com.example.helpq.view.student_views.StudentFragment;
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

    //hides the keyboard when switching between fragments
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService
                (Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.clearFocus();
    }
}
