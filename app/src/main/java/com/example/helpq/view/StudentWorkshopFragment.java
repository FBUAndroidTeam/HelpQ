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
import android.widget.Button;

import com.example.helpq.R;
import com.example.helpq.controller.StudentWorkshopAdapter;
import com.example.helpq.model.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class StudentWorkshopFragment extends Fragment {
    public static final String TAG = "StudentWorkshopFragment";
    private RecyclerView rvWorkshops;
    private StudentWorkshopAdapter adapter;
    private List<Workshop> mWorkshops;

    public static StudentWorkshopFragment newInstance() {
        return new StudentWorkshopFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_workshop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWorkshops = new ArrayList<>();
        adapter = new StudentWorkshopAdapter(getContext(), mWorkshops);
        rvWorkshops = view.findViewById(R.id.rvStudentWorkshops);
        rvWorkshops.setAdapter(adapter);
        rvWorkshops.setLayoutManager(new LinearLayoutManager(getContext()));

        queryWorkshops();
    }

    private void queryWorkshops() {
        ParseQuery<Workshop> workshopQuery = ParseQuery.getQuery("Workshop");
        workshopQuery.include("creator");
        workshopQuery.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                for(int i = 0; i < objects.size(); i++) {
                    String name = objects.get(i).getCreator().getUsername();
                    ParseUser user = ParseUser.getCurrentUser();
                    String name2 = user.getString("adminName");
                    if(name.equals(name2)) {
                        mWorkshops.add(objects.get(i));
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "adapter notified");
                    }
                }
            }
        });
    }

}
