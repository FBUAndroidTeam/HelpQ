package com.example.helpq.view.student_views;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.StudentWorkshopAdapter;
import com.example.helpq.model.AlertReceiver;
import com.example.helpq.model.Notification;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.example.helpq.model.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentWorkshopFragment extends Fragment {
    public static final String TAG = "StudentWorkshopFragment";
    public static final long ONE_MINUTE_IN_MILLIS = 60000;
    private RecyclerView rvWorkshops;
    private StudentWorkshopAdapter mAdapter;
    private List<Workshop> mWorkshops;
    private SwipeRefreshLayout swipeContainer;
    private TextView tvNotice;
    private Question q;
    private ProgressBar pbLoading;
    public static String workshop;

    // Set of strings representing the objectIds of workshops that have notifications
    public Set<String> mNotifications;

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

    //reloads landscape/portrait version of layout faster on config change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        q = new Question();
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);

        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);
        mWorkshops = new ArrayList<>();
        mAdapter = new StudentWorkshopAdapter(getContext(), mWorkshops, this);
        rvWorkshops = view.findViewById(R.id.rvStudentWorkshops);
        rvWorkshops.setAdapter(mAdapter);
        rvWorkshops.setLayoutManager(new LinearLayoutManager(getContext()));

        queryWorkshopsWithNotifications();
        setupSwipeRefreshing(view);
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Sound.refreshPage(getContext());
                fetchQueueAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setProgressBackgroundColorSchemeColor(
                getContext().getResources().getColor(R.color.colorAccent));
        swipeContainer.setColorSchemeResources(R.color.colorMint,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Refresh the queue, and load workshops.
    protected void fetchQueueAsync() {
        mAdapter.clear();
        queryWorkshopsWithNotifications();
        swipeContainer.setRefreshing(false);
    }

    // Clear the hashtable, and fill it with mappings from workshop object ids to
    // the current user's notifications that point to them. Query for all workshops.
    private void queryWorkshopsWithNotifications() {
        pbLoading.setVisibility(View.VISIBLE);
        mNotifications = new HashSet<>();
        ParseQuery<Notification> query = QueryFactory.Notifications.getNotifications();
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with notification query");
                    return;
                }

                // Get the workshop objectIds of the user's notifications,
                // and delete the notifications.
                for (int i = 0; i < objects.size(); i++) {
                    Notification notification = objects.get(i);
                    if (notification.getWorkshopId() != null) {
                        mNotifications.add(notification.getWorkshopId());
                        try {
                            notification.delete();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                queryWorkshops();
            }
        });
    }

    private void queryWorkshops() {
        ParseQuery<Workshop> query = QueryFactory.Workshops.getWorkshopsForStudent();
        query.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                addWorkshopsToAdapter(objects);
                runLayoutAnimation();
                pbLoading.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void addWorkshopsToAdapter(List<Workshop> objects) {
        for (int i = 0; i < objects.size(); i++) {
            String name = objects.get(i).getCreator().getUsername();
            String name2 = User.getAdminName(ParseUser.getCurrentUser());
            if(name.equals(name2)) {
                mWorkshops.add(objects.get(i));
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "mAdapter notified");
            }
        }
        pbLoading.setVisibility(View.INVISIBLE);
        if(mWorkshops.size() == 0) {
            tvNotice.setVisibility(View.VISIBLE);
        } else {
            tvNotice.setVisibility(View.GONE);
        }
    }

    // Animate RecyclerView items falling onto the screen.
    protected void runLayoutAnimation() {
        final Context context = rvWorkshops.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_enter);

        rvWorkshops.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        rvWorkshops.scheduleLayoutAnimation();
    }
}
