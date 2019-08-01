package com.example.helpq.view;

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
import android.widget.SearchView;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.InboxAdapter;
import com.example.helpq.model.Question;
import com.example.helpq.model.Search;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {

    public static final String TAG = "InboxFragment";
    private RecyclerView rvMessages;
    private SwipeRefreshLayout swipeContainer;
    protected List<Question> mMessages;
    protected InboxAdapter mAdapter;
    protected TextView tvNotice;
    protected SearchView svSearch;

    public static InboxFragment newInstance() {
        return new InboxFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);
        svSearch = view.findViewById(R.id.svSearch);
        Search.setSearchUi(svSearch);

        // Create data source, adapter, and layout manager
        mMessages = new ArrayList<>();
        mAdapter = new InboxAdapter(getContext(), mMessages, this);
        rvMessages = view.findViewById(R.id.rvMessages);
        rvMessages.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(layoutManager);

        setupSwipeToRefresh(view);
        queryMessages();
        search();

        mAdapter.setOnItemClickListener(new InboxAdapter.ClickListener() {
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

    // Handle logic for swipe to refresh.
    private void setupSwipeToRefresh(View view) {
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchInboxAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    protected void search() {
    }

    // Reload inbox.
    protected void fetchInboxAsync() {
        mAdapter.clear();
        queryMessages();
        swipeContainer.setRefreshing(false);
    }

    // Query for messages intended for the current user
    protected void queryMessages() {
    }
}
