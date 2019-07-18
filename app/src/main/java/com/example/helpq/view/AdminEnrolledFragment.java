package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.controller.EnrolledStudentsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminEnrolledFragment extends Fragment {

    public static final String TAG = "AdminEnrolledFragment";
    private RecyclerView rvEnrolledStudents;
    private EnrolledStudentsAdapter adapter;
    private List<String> mStudents;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_enrolled, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEnrolledStudents = view.findViewById(R.id.rvEnrolledStudents);
        mStudents = new ArrayList<>();
        adapter = new EnrolledStudentsAdapter(getContext(), mStudents);
        rvEnrolledStudents.setAdapter(adapter);
        rvEnrolledStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        queryEnrolledStudents();
    }

    private void queryEnrolledStudents() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("instructorName", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                for(int i = 0; i < objects.size(); i++) {
                    String name = objects.get(i).getString("fullName");
                    mStudents.add(name);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, name + ParseUser.getCurrentUser());
                }
            }
        });
    }


}
