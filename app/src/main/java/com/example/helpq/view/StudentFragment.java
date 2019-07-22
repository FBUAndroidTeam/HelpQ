package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;

public class StudentFragment extends Fragment {

    public static final String TAG = "StudentFragment";
    private FragmentManager fragmentManager;

    public static StudentFragment newInstance() {
        return new StudentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentManager = getFragmentManager();
        BottomNavigationView navigationView =
                (BottomNavigationView) view.findViewById(R.id.top_navigation);

        // Set top menu to navigate between "Queue" and "Students" tabs
        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_view_queue:
                        setFragment(StudentQueueFragment.newInstance(),
                                new String[]{StudentQueueFragment.TAG,
                                        StudentWorkshopFragment.TAG,
                                        StudentInboxFragment.TAG,
                                        StudentBoardFragment.TAG,});
                        return true;
                    case R.id.action_view_workshops:
                        setFragment(StudentWorkshopFragment.newInstance(),
                                new String[]{StudentWorkshopFragment.TAG,
                                        StudentQueueFragment.TAG,
                                        StudentInboxFragment.TAG,
                                        StudentBoardFragment.TAG,});
                        return true;
                    case R.id.action_view_inbox:
                        setFragment(StudentInboxFragment.newInstance(),
                                new String[]{StudentInboxFragment.TAG,
                                        StudentWorkshopFragment.TAG,
                                        StudentQueueFragment.TAG,
                                        StudentBoardFragment.TAG,});
                        return true;
                    case R.id.action_view_board:
                        setFragment(StudentBoardFragment.newInstance(),
                                new String[]{StudentBoardFragment.TAG,
                                        StudentInboxFragment.TAG,
                                        StudentWorkshopFragment.TAG,
                                        StudentQueueFragment.TAG});
                        return true;
                    default:
                        return true;
                }
            }
        });
        navigationView.setSelectedItemId(R.id.action_view_queue);
    }

    // Show/add the given fragment with tag stored in tags[0], and hide fragments
    // with all other tags.
    public void setFragment(Fragment fragment, String[] tags) {

        if (fragmentManager.findFragmentByTag(tags[0]) != null) {
            // if the fragment exists, show it.
            fragmentManager
                    .beginTransaction()
                    .show(fragmentManager.findFragmentByTag(tags[0]))
                    .commit();
        } else {
            // if the fragment does not exist, add it to fragment manager.
            fragmentManager
                    .beginTransaction()
                    .add(R.id.flStudentContainer, fragment, tags[0])
                    .commit();
        }

        for (int i = 1; i < tags.length; i++) {
            if(fragmentManager.findFragmentByTag(tags[i]) != null) {
                //if the other fragment is visible, hide it.
                fragmentManager
                        .beginTransaction()
                        .hide(fragmentManager.findFragmentByTag(tags[i]))
                        .commit();
            }
        }
    }
}
