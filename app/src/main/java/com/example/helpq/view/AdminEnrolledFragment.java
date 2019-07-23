package com.example.helpq.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;
import com.example.helpq.controller.EnrolledStudentsAdapter;
import com.example.helpq.model.User;
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

    public static AdminEnrolledFragment newInstance() {
        return new AdminEnrolledFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_enrolled, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvEnrolledStudents = view.findViewById(R.id.rvEnrolledStudents);
        mStudents = new ArrayList<>();
        adapter = new EnrolledStudentsAdapter(getContext(), mStudents);
        rvEnrolledStudents.setAdapter(adapter);
        rvEnrolledStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        queryEnrolledStudents();
    }



    private void queryEnrolledStudents() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(User.KEY_ADMIN_NAME, ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                for(int i = 0; i < objects.size(); i++) {
                    String name = User.getFullName(objects.get(i));
                    mStudents.add(name);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, name + ParseUser.getCurrentUser());
                }
            }
        });
    }

}
