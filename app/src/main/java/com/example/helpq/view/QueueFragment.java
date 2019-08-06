package com.example.helpq.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import com.example.helpq.controller.QueueAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.Notification;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Search;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class QueueFragment extends Fragment implements DialogDismissListener {

    public static final String TAG = "QueueFragment";

    // RecyclerView, data sets, and adapter
    private RecyclerView rvQuestions;
    private List<Question> mQuestions;
    private List<Question> mAllQuestions;
    private QueueAdapter mAdapter;

    // Text notices and search fields
    private TextView tvNotice;
    private TextView tvSearchNotice;
    private TextView tvSearchHint;
    private SearchView svQueueSearch;

    // Swipe to refresh and progress bar
    private SwipeRefreshLayout mSwipeContainer;
    private ProgressBar pbLoading;

    // Maps Question objectIds to the notifications that point to them
    public Hashtable<String, Notification> mNotifications;

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

        // Initialize notices - initially invisible
        tvNotice = view.findViewById(R.id.tvNotice);
        tvSearchNotice = view.findViewById(R.id.tvSearchNotice);
        tvNotice.setVisibility(View.GONE);
        tvSearchNotice.setVisibility(View.GONE);

        // Initialize search fields
        svQueueSearch = view.findViewById(R.id.svQueueSearch);
        Search.setSearchUi(svQueueSearch, getContext());
        tvSearchHint = view.findViewById(R.id.tvSearchHint);
        setSearchHintListener();

        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);

        // Create data source, adapter, and layout manager
        mQuestions = new ArrayList<>();
        mAllQuestions = new ArrayList<>();
        mAdapter = new QueueAdapter(getContext(), mQuestions, this);
        rvQuestions = view.findViewById(R.id.rvQuestions);
        rvQuestions.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvQuestions.setLayoutManager(layoutManager);

        if (User.isAdmin(ParseUser.getCurrentUser())) findHighlightedQuestions();

        setupSwipeRefreshing(view);
        queryQuestions("");
        search();

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

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Sound.refreshPage(getContext());
                fetchQueueAsync(svQueueSearch.getQuery().toString());
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void findHighlightedQuestions() {
        mNotifications = new Hashtable<>();
        ParseQuery query = QueryFactory.Notifications.getNotifications();
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with notification query");
                    return;
                }
                for (int i = 0; i < objects.size(); i++) {
                    String questionId = objects.get(i).getQuestionId();
                    if (questionId != null) {
                        mNotifications.put(objects.get(i).getQuestionId(), objects.get(i));
                    }
                }
            }
        });
    }

    // Refresh the queue, and load questions.
    public void fetchQueueAsync(String input) {
        mAdapter.clear();
        tvNotice.setVisibility(View.GONE);
        tvSearchNotice.setVisibility(View.GONE);
        findHighlightedQuestions();
        queryQuestions(input);
        mSwipeContainer.setRefreshing(false);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    private void queryQuestions(final String input) {
        pbLoading.setVisibility(View.VISIBLE);
        final ParseQuery<Question> query = QueryFactory.Questions.getQuestionsForQueue();
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }

                // Hide notices and clear message lists
                tvNotice.setVisibility(View.GONE);
                tvSearchNotice.setVisibility(View.GONE);
                mQuestions.clear();
                mAllQuestions.clear();

                // Retrieve the questions that should be displayed
                List<Question> messages = getQueueQuestions(objects);
                mAllQuestions.addAll(messages);
                findMatches(input);

                // Display a notice if the user has no messages
                if (messages.size() == 0) {
                    tvNotice.setVisibility(View.VISIBLE);
                }
                runLayoutAnimation();
            }
        });
    }

    // Return the list of questions that should appear on the current user's queue
    // from the given list of objects.
    private List<Question> getQueueQuestions(List<Question> objects) {
        List<Question> questions = new ArrayList<>();
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
                questions.add(question);
            }
        }
        return questions;
    }

    @Override
    public void onDismiss() {
        fetchQueueAsync("");
    }

    //created at, asker, text, priority, help type, set archived to false
    public void createSnackbar(final int adapterpos, final Question q){

        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                q.setIsArchived(false);
                q.saveInBackground();
                mQuestions.add(q);
                Collections.sort(mQuestions);
                mAdapter.notifyItemInserted(adapterpos);
                rvQuestions.scrollToPosition(adapterpos);
            }
        };

        Snackbar.make(getView(), R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action, myOnClickListener)
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if(event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                            mAdapter.deleteQuestion(q);
                        }
                    }
                })
                .show();
    }

    private void setSearchHintListener() {
        tvSearchHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svQueueSearch.setIconified(false);
            }
        });
    }

    protected void search() {
        svQueueSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                tvSearchHint.setVisibility(View.VISIBLE);
                return false;
            }
        });

        svQueueSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        mQuestions.clear();

        // If input is empty, display all questions; else, display all matches
        if (input.isEmpty()) {
            mQuestions.addAll(mAllQuestions);
        } else {
            List<Question> result = Search.searchQueue(mAllQuestions, input);
            mQuestions.addAll(result);
        }
        mAdapter.notifyDataSetChanged();
        Collections.sort(mQuestions);

        // Display a notice if no results match the search
        if (mQuestions.size() == 0 && mAllQuestions.size() != 0) {
            tvSearchNotice.setVisibility(View.VISIBLE);
        }
        else tvSearchNotice.setVisibility(View.GONE);

        pbLoading.setVisibility(View.INVISIBLE);
    }

    // Animate RecyclerView items falling onto the screen.
    protected void runLayoutAnimation() {
        final Context context = rvQuestions.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_enter);

        rvQuestions.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        rvQuestions.scheduleLayoutAnimation();
    }
}
