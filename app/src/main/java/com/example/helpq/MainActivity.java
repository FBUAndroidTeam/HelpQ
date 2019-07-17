package com.example.helpq;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.top_navigation);

        // Set top menu to navigate between "Queue" and "Students" tabs
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_view_queue:
                        setFragment(new AdminQueueFragment(),
                                new String[]{AdminQueueFragment.TAG, AdminStudentsFragment.TAG});
                        return true;
                    case R.id.action_view_students:
                        setFragment(new AdminStudentsFragment(),
                                new String[]{AdminStudentsFragment.TAG, AdminQueueFragment.TAG});
                        return true;
                    default:
                        setFragment(new AdminQueueFragment(),
                                new String[]{AdminQueueFragment.TAG, AdminStudentsFragment.TAG});
                        return true;
                }
            }
        });
        navigationView.setSelectedItemId(R.id.action_view_queue);
    }

    // Show/add the given fragment with tag stored in tags[0], and hide fragments with all other tags.
    public void setFragment(Fragment fragment, String[] tags) {

        if(fragmentManager.findFragmentByTag(tags[0]) != null) {
            // if the fragment exists, show it.
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(tags[0])).commit();
        } else {
            // if the fragment does not exist, add it to fragment manager.
            // always add a new fragment if it is a PostDetailsFragment
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.flContainer, fragment, tags[0]).commit();
        }

        for (int i = 1; i < tags.length; i++) {
            if(fragmentManager.findFragmentByTag(tags[i]) != null) {
                //if the other fragment is visible, hide it.
                fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(tags[i])).commit();
            }
        }
    }
}
