package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;
import com.example.helpq.controller.AdminWorkshopAdapter;
import com.example.helpq.model.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class AdminWorkshopFragment extends Fragment {
    public static final String TAG = "AdminWorkshopFragment";
    private RecyclerView rvAdminWorkshops;
    private List<Workshop> mWorkshops;
    private AdminWorkshopAdapter adapter;
    private FloatingActionButton fabAddWorkshop;

    public static AdminWorkshopFragment newInstance() {
        return new AdminWorkshopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_workshop, parent, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvAdminWorkshops = view.findViewById(R.id.rvAdminWorkshops);
        mWorkshops = new ArrayList<>();
        adapter = new AdminWorkshopAdapter(getContext(), mWorkshops);
        rvAdminWorkshops.setAdapter(adapter);
        rvAdminWorkshops.setLayoutManager(new LinearLayoutManager(getContext()));
        fabAddWorkshop = view.findViewById(R.id.fabAddWorkshop);
        queryWorkshops();
        fabAddWorkshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                CreateWorkshopFragment CreateWorkshopFragment =
                        com.example.helpq.view.CreateWorkshopFragment.newInstance("Some Title");
                CreateWorkshopFragment.show(fm, "fragment_create_workshop");
            }
        });

    }

    private void queryWorkshops() {
        ParseQuery<Workshop> query = ParseQuery.getQuery("Workshop");
        query.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if(e == null) {
                    mWorkshops.addAll(objects);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "adapter notified");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}