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

import com.example.helpq.R;
import com.example.helpq.controller.QueueAdapter;
import com.example.helpq.model.Question;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class QueueFragment extends Fragment {

    public static final String TAG = "QueueFragment";
    private RecyclerView rvQuestions;
    private List<Question> mQuestions;
    private QueueAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        queryQuestions(getString(R.string.PRIORITY_BLOCKING));
        queryQuestions(getString(R.string.PRIORITY_STRETCH));
        queryQuestions(getString(R.string.PRIORITY_CURIOSITY));


    }


    private void queryQuestions (final String priorityEmoji) {
        final ParseQuery<Question> questionQuery = new ParseQuery<Question>(Question.class);
        questionQuery.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }
                for (int i = 0; i < objects.size(); i++) {
                    Question q = objects.get(i);
                    if (q.getPriority().equals(priorityEmoji)) {
                        mQuestions.add(q);
                        Log.d(TAG, q.getPriority() + ": " + q.getText() + " " + priorityEmoji);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}

