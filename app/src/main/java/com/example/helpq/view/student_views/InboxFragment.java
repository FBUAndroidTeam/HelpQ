package com.example.helpq.view.student_views;

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
import android.widget.SearchView;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.InboxAdapter;
import com.example.helpq.model.Notification;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Search;
import com.example.helpq.model.Sound;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InboxFragment extends Fragment {

    public static final String TAG = "InboxFragment";

    // RecyclerView, data sets, and adapter
    private RecyclerView rvMessages;
    protected List<Question> mMessages;
    protected List<Question> mAllMessages;
    protected InboxAdapter mAdapter;

    // Text notices and search fields
    protected TextView tvNotice;
    protected TextView tvSearchNotice;
    protected TextView tvSearchHint;
    protected SearchView svSearch;

    // Swipe to refresh and progress bar
    private SwipeRefreshLayout mSwipeContainer;
    protected ProgressBar pbLoading;

    // Set of strings representing the objectIds of messages that have notifications
    public Set<String> mNotifications;

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

        // Initialize notices - initially invisible
        tvNotice = view.findViewById(R.id.tvNotice);
        tvSearchNotice = view.findViewById(R.id.tvSearchNotice);
        tvNotice.setVisibility(View.GONE);
        tvSearchNotice.setVisibility(View.GONE);

        // Initialize search fields
        svSearch = view.findViewById(R.id.svSearch);
        Search.setSearchUi(svSearch, getContext());
        tvSearchHint = view.findViewById(R.id.tvSearchHint);
        setSearchHintListener();

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
        queryMessagesWithNotifications("");
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

    private void setSearchHintListener() {
        tvSearchHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svSearch.setIconified(false);
            }
        });
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
                fetchInboxAsync(svSearch.getQuery().toString());
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setProgressBackgroundColorSchemeColor(
                getContext().getResources().getColor(R.color.colorAccent));
        mSwipeContainer.setColorSchemeResources(R.color.colorMint,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    // Animate RecyclerView items falling onto the screen.
    protected void runLayoutAnimation() {
        final Context context = rvMessages.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_enter);

        rvMessages.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        rvMessages.scheduleLayoutAnimation();
    }

    private void search() {
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvSearchHint.setVisibility(View.VISIBLE);
                return false;
            }
        });
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                tvSearchHint.setVisibility(View.GONE);
                findMatches(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    tvSearchHint.setVisibility(View.GONE);
                }
                findMatches(newText);
                return false;
            }
        });
    }

    // Search for matches, and add them to the list to be displayed.
    protected void findMatches(String input) {
        mMessages.clear();

        // If input is empty, display all messages; else, display all matches
        if (input.isEmpty()) {
            mMessages.addAll(mAllMessages);
        } else {
            List<Question> result = Search.searchInbox(mAllMessages, input);
            mMessages.addAll(result);
        }
        mAdapter.notifyDataSetChanged();

        // Display a notice if no results match the search
        if (mMessages.size() == 0 && mAllMessages.size() != 0) {
            tvSearchNotice.setVisibility(View.VISIBLE);
        }
        else tvSearchNotice.setVisibility(View.GONE);

        pbLoading.setVisibility(View.INVISIBLE);
    }

    // Reload inbox.
    protected void fetchInboxAsync(String input) {
        mAdapter.clear();
        tvNotice.setVisibility(View.GONE);
        tvSearchNotice.setVisibility(View.GONE);
        queryMessagesWithNotifications(input);
        mSwipeContainer.setRefreshing(false);
    }

    // Clear the hashtable, and fill it with mappings from question object ids to
    // the current user's notifications that point to them. Query for all messages.
    private void queryMessagesWithNotifications(final String input) {
        mNotifications = new HashSet<>();
        ParseQuery<Notification> query = QueryFactory.Notifications.getNotifications();
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with notification query");
                    return;
                }

                // Get the message objectIds of the user's notifications,
                // and delete the notifications.
                for (int i = 0; i < objects.size(); i++) {
                    Notification notification = objects.get(i);
                    if (notification.getQuestionId() != null) {
                        mNotifications.add(notification.getQuestionId());
                        try {
                            notification.delete();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                queryMessages(input);
            }
        });
    }

    // Query for messages intended for the current user
    protected void queryMessages(String input) {
    }
}
