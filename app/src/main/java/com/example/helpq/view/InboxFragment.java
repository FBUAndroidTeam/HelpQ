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
import android.widget.ProgressBar;
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
    protected List<Question> mAllMessages;
    protected InboxAdapter mAdapter;
    protected TextView tvNotice;
    protected TextView tvSearchNotice;
    protected SearchView svSearch;
    protected TextView tvSearch;
    protected ProgressBar pbLoading;

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
        tvSearchNotice = view.findViewById(R.id.tvSearchNotice);
        tvNotice.setVisibility(View.GONE);
        tvSearchNotice.setVisibility(View.GONE);

        svSearch = view.findViewById(R.id.svSearch);
        Search.setSearchUi(svSearch, getContext());
        tvSearch = view.findViewById(R.id.tvSearch);

        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);

        // Create data source, adapter, and layout manager
        mMessages = new ArrayList<>();
        mAllMessages = new ArrayList<>();
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
                fetchInboxAsync(svSearch.getQuery().toString());
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void search() {
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvSearch.setVisibility(View.VISIBLE);
                return false;
            }
        });
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                tvSearch.setVisibility(View.GONE);
                findMatches(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    mMessages.clear();
                    queryMessages();
                    return false;
                } else {
                    tvSearch.setVisibility(View.GONE);
                }
                findMatches(newText);
                return false;
            }
        });
    }

    // Search for matches, and add them to the list to be displayed.
    protected void findMatches(String query) {
        List<Question> result = Search.mSearch(mAllMessages, query);
        mMessages.clear();
        mMessages.addAll(result);
        mAdapter.notifyDataSetChanged();
        if (mMessages.size() == 0) {
            tvSearchNotice.setVisibility(View.VISIBLE);
        }
        else {
            tvSearchNotice.setVisibility(View.GONE);
        }
        pbLoading.setVisibility(View.INVISIBLE);
    }

    // Reload inbox.
    protected void fetchInboxAsync(String query) {
        mAdapter.clear();
        tvNotice.setVisibility(View.GONE);
        tvSearchNotice.setVisibility(View.GONE);
        if ((query.isEmpty())) queryMessages();
        else findMatches(query);
        swipeContainer.setRefreshing(false);
    }

    // Query for messages intended for the current user
    protected void queryMessages() {
    }
}
