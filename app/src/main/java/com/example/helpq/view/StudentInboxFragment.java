package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.InboxAdapter;
import com.example.helpq.model.Question;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class StudentInboxFragment extends Fragment {

    public static final String TAG = "StudentInboxFragment";
    private RecyclerView rvMessages;
    protected List<Question> mMessages;
    protected InboxAdapter mAdapter;
    private TextView tvNotice;

    public static StudentInboxFragment newInstance() {
        return new StudentInboxFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);
        // Create data source, adapter, and layout manager
        mMessages = new ArrayList<>();
        mAdapter = new InboxAdapter(getContext(), mMessages);
        rvMessages = view.findViewById(R.id.rvMessages);
        rvMessages.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(layoutManager);
        queryMessages();
    }

    // Query for messages intended for this student (the current user)
    protected void queryMessages() {
        final ParseQuery<Question> messageQuery = new ParseQuery<Question>(Question.class);
        messageQuery.include(Question.KEY_ASKER)
                .whereEqualTo(Question.KEY_ASKER, ParseUser.getCurrentUser())
                .whereEqualTo(Question.KEY_HELP_TYPE,
                        getContext().getResources().getString(R.string.written))
                .whereEqualTo(Question.KEY_ARCHIVED, true)
                .orderByDescending(Question.KEY_ANSWERED_AT);
        messageQuery.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with message query");
                    e.printStackTrace();
                    return;
                }
                addQuestionsToAdapter(objects);
            }
        });
    }

    private void addQuestionsToAdapter(List<Question> objects) {
        if(objects.size() == 0) {
            tvNotice.setVisibility(View.VISIBLE);
        } else {
            tvNotice.setVisibility(View.GONE);
            for (Question question : objects) {
                if (question.getAnswer() != null) {
                    mMessages.add(question);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
