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

import com.example.helpq.R;
import com.example.helpq.controller.QueueAdapter;
import com.example.helpq.model.Question;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueFragment extends Fragment {

    public static final String TAG = "QueueFragment";
    private RecyclerView rvQuestions;
    private List<Question> mQuestions;
    private QueueAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private Button btLogout;

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
        adapter = new QueueAdapter(getContext(), mQuestions);
        rvQuestions = view.findViewById(R.id.rvQuestions);
        rvQuestions.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvQuestions.setLayoutManager(layoutManager);
        btLogout = view.findViewById(R.id.btLogoutButt);
        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        queryQuestions();
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

    // Refresh the queue, and load questions.
    protected void fetchQueueAsync() {
        adapter.clear();
        queryQuestions();
        swipeContainer.setRefreshing(false);
    }

    private void queryQuestions() {
        final ParseQuery<Question> questionQuery = new ParseQuery<Question>(Question.class);
        questionQuery.whereEqualTo(Question.KEY_ARCHIVED, false)
                .include(Question.KEY_ASKER)
                .whereNotEqualTo("isAdmin", true);
        questionQuery.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                for(Question question : objects) {
                    ParseUser asker = question.getAsker(); // who asked the question
                    String currUser = ParseUser.getCurrentUser().getUsername(); // user of who is currently logged in
                    String currUserAdmin = "";
                    if(!ParseUser.getCurrentUser().getBoolean("isAdmin")) {
                        currUserAdmin = ParseUser.getCurrentUser().getString("adminName");
                    }
                    String askerAdmin = asker.getString("adminName"); // admin of asker
                    if (currUser.equals(askerAdmin) || askerAdmin.equals(currUserAdmin)) {
                        mQuestions.add(question);
                    }
                }
                Collections.sort(mQuestions);
                adapter.notifyDataSetChanged();
            }
        });
    }
}


