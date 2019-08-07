package com.example.helpq.view.admin_views;

import android.content.Context;
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
import com.example.helpq.controller.EnrolledStudentsAdapter;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Sound;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminEnrolledFragment extends Fragment {

    public static final String TAG = "AdminEnrolledFragment";
    private RecyclerView rvEnrolledStudents;
    private EnrolledStudentsAdapter mAdapter;
    private List<ParseUser> mStudents;
    private TextView tvNotice;
    private SwipeRefreshLayout mSwipeContainer;
    private ProgressBar pbLoading;

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
        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);
        rvEnrolledStudents = view.findViewById(R.id.rvEnrolledStudents);
        mStudents = new ArrayList<>();
        mAdapter = new EnrolledStudentsAdapter(getContext(), mStudents, this);
        rvEnrolledStudents.setAdapter(mAdapter);
        rvEnrolledStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);

        setupSwipeToRefresh(view);
        queryEnrolledStudents();
    }

    // Handle logic for swipe to refresh.
    private void setupSwipeToRefresh(View view) {
        // Lookup the swipe container view
        mSwipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Sound.refreshPage(getContext());
                fetchEnrolledAsync();
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    // Reload inbox.
    protected void fetchEnrolledAsync() {
        mAdapter.clear();
        tvNotice.setVisibility(View.GONE);
        queryEnrolledStudents();
        mSwipeContainer.setRefreshing(false);
    }

    private void queryEnrolledStudents() {
        ParseQuery<ParseUser> query = QueryFactory.Users.getStudentList();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                for(int i = 0; i < objects.size(); i++) {
                    mStudents.add(objects.get(i));
                    mAdapter.notifyDataSetChanged();
                    pbLoading.setVisibility(View.INVISIBLE);
                }
                isPageEmpty();
                runLayoutAnimation();
            }
        });
    }

    private void isPageEmpty() {
        if(mStudents.size() == 0) {
            tvNotice.setVisibility(View.VISIBLE);
        } else {
            tvNotice.setVisibility(View.GONE);
        }
    }

    // Animate RecyclerView items falling onto the screen.
    protected void runLayoutAnimation() {
        final Context context = rvEnrolledStudents.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_enter);

        rvEnrolledStudents.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        rvEnrolledStudents.scheduleLayoutAnimation();
    }
}
