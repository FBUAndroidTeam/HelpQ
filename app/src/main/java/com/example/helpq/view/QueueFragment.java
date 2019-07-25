package com.example.helpq.view;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.QueueAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.model.WaitTimeCalculator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueFragment extends Fragment implements DialogDismissListener {

    public static final String TAG = "QueueFragment";
    private RecyclerView rvQuestions;
    private List<Question> mQuestions;
    private QueueAdapter mAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private Button btLogout;

    // Wait time calculation fields
    private TextView tvBlockingWaitTime;
    private TextView tvStretchWaitTime;
    private TextView tvCuriosityWaitTime;

    public static QueueFragment newInstance() {
        return new QueueFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_queue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create data source, adapter, and layout manager
        mQuestions = new ArrayList<>();
        mAdapter = new QueueAdapter(getContext(), mQuestions, this);
        rvQuestions = view.findViewById(R.id.rvQuestions);
        rvQuestions.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvQuestions.setLayoutManager(layoutManager);

        setupLogout(view);
        setupWaitTimeCalculation(view);
        queryQuestions();
        setupSwipeRefreshing(view);

        mAdapter.setOnItemClickListener(new QueueAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d(TAG, "onItemClick position: " + position);
            }
            @Override
            public void onItemLongClick(int position, View v) {
                Log.d(TAG, "onItemLongClick position: " + position);
            }
        });
    }

    // Displays wait times above queue.
    private void setupWaitTimeCalculation(@NonNull View view) {
        tvBlockingWaitTime = view.findViewById(R.id.tvBlockingWaitTime);
        tvStretchWaitTime = view.findViewById(R.id.tvStretchWaitTime);
        tvCuriosityWaitTime = view.findViewById(R.id.tvCuriosityWaitTime);

        final ParseUser user = ParseUser.getCurrentUser();
        if (User.isAdmin(user)) {

            // Get the admin's wait times
            long blockingMillis = User.getBlockingMillis(user);
            long stretchMillis = User.getStretchMillis(user);
            long curiosityMillis = User.getCuriosityMillis(user);

            // If the admin's wait times do not exist, calculate them, and
            // store them in Parse.
            if (blockingMillis == -1 || stretchMillis == -1 || curiosityMillis == -1) {
                setAdminWaitTimes(user);
                return;
            }

            // Display the wait times.
            WaitTimeCalculator calculator = new WaitTimeCalculator(getContext(),
                    blockingMillis, stretchMillis, curiosityMillis);
            tvBlockingWaitTime.setText(calculator.getBlockingWaitTime());
            tvStretchWaitTime.setText(calculator.getStretchWaitTime());
            tvCuriosityWaitTime.setText(calculator.getCuriosityWaitTime());
        }
    }

    // Calculate the admin's wait times and save them in Parse.
    private void setAdminWaitTimes(final ParseUser user) {
        final ParseQuery<Question> questionQuery = new ParseQuery<Question>(Question.class);
        questionQuery.whereEqualTo(Question.KEY_ARCHIVED, true)
                .include(Question.KEY_ASKER);

        questionQuery.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with question wait time query");
                    e.printStackTrace();
                    return;
                }

                List<Question> questions = new ArrayList<>();

                for (Question question : objects) {

                    // User who asked the question
                    ParseUser asker = question.getAsker();
                    // User who is currently logged in
                    String currUser = ParseUser.getCurrentUser().getUsername();

                    // Admin of current user
                    String currUserAdmin = null;
                    if (!User.isAdmin(ParseUser.getCurrentUser())) {
                        currUserAdmin = User.getAdminName(ParseUser.getCurrentUser());
                    }
                    // Admin of asker
                    String askerAdmin = User.getAdminName(asker);

                    if (currUser.equals(askerAdmin) || askerAdmin.equals(currUserAdmin)) {
                        questions.add(question);
                    }
                }

                WaitTimeCalculator calculator = new WaitTimeCalculator(getContext(), questions);
                User.initWaitTimes(user, calculator.getBlockingMillis(), calculator.getBlockingSize(),
                        calculator.getStretchMillis(), calculator.getStretchSize(),
                        calculator.getCuriosityMillis(), calculator.getCuriositySize());
                user.saveInBackground();
                tvBlockingWaitTime.setText(calculator.getBlockingWaitTime());
                tvStretchWaitTime.setText(calculator.getStretchWaitTime());
                tvCuriosityWaitTime.setText(calculator.getCuriosityWaitTime());
            }
        });
    }

    // Handle logic for logging out.
    private void setupLogout(@NonNull View view) {
        btLogout = view.findViewById(R.id.btLogoutButt);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                ((MainActivity) getActivity()).finish();
            }
        });
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchQueueAsync();
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Refresh the queue, and load questions.
    public void fetchQueueAsync() {
        mAdapter.clear();
        queryQuestions();
        mSwipeContainer.setRefreshing(false);
    }

    private void queryQuestions() {
        final ParseQuery<Question> questionQuery = new ParseQuery<Question>(Question.class);
        questionQuery.whereEqualTo(Question.KEY_ARCHIVED, false)
                .include(Question.KEY_ASKER)
                .whereNotEqualTo(User.KEY_IS_ADMIN, true);
        questionQuery.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                for (Question question : objects) {
                    // who asked the question
                    ParseUser asker = question.getAsker();
                    // user of who is currently logged in
                    String currUser = ParseUser.getCurrentUser().getUsername();
                    String currUserAdmin = "";
                    if (!User.isAdmin(ParseUser.getCurrentUser())) {
                        currUserAdmin = User.getAdminName(ParseUser.getCurrentUser());
                    }
                    // admin of asker
                    String askerAdmin = User.getAdminName(asker);
                    if (currUser.equals(askerAdmin) || askerAdmin.equals(currUserAdmin)) {
                        mQuestions.add(question);
                    }
                }
                Collections.sort(mQuestions);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDismiss() {
        fetchQueueAsync();
    }
}


