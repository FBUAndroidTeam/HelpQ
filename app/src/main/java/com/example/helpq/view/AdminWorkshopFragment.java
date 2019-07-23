package com.example.helpq.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;
import com.example.helpq.controller.AdminWorkshopAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdminWorkshopFragment extends Fragment implements DialogDismissListener {
    public static final String TAG = "AdminWorkshopFragment";
    private RecyclerView rvAdminWorkshops;
    private List<Workshop> mWorkshops;
    private AdminWorkshopAdapter adapter;
    private FloatingActionButton fabAddWorkshop;
    private SwipeRefreshLayout swipeContainer;
    private FragmentManager fm;

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
        fm = getFragmentManager();
        fabAddWorkshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                CreateWorkshopFragment createWorkshopFragment =
                        com.example.helpq.view.CreateWorkshopFragment.newInstance("Some Title");
                createWorkshopFragment.setTargetFragment(AdminWorkshopFragment.this,
                        300);
                createWorkshopFragment.show(fm, createWorkshopFragment.TAG);

            }
        });
        queryWorkshops();
        setupSwipeRefreshing(view);
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchQueueAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Refresh the queue, and load workshops.
    protected void fetchQueueAsync() {
        adapter.clear();
        queryWorkshops();
        swipeContainer.setRefreshing(false);
    }

    private void queryWorkshops() {
        ParseQuery<Workshop> query = ParseQuery.getQuery("Workshop");
        query.whereEqualTo("creator", ParseUser.getCurrentUser());
        query.whereGreaterThan("startTime", new Date(System.currentTimeMillis()));
        query.addAscendingOrder("startTime");
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

    @Override
    public void onDismiss() {
        fetchQueueAsync();
    }
}
