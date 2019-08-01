package com.example.helpq.view;

import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Search;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class StudentInboxFragment extends InboxFragment {

    public static final String TAG = "StudentInboxFragment";

    public static StudentInboxFragment newInstance() {
        return new StudentInboxFragment();
    }

    // Query for messages intended for this student
    protected void queryMessages() {
        final ParseQuery<Question> query = QueryFactory.QuestionQuery.getStudentInboxMessages();
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with message query");
                    e.printStackTrace();
                    return;
                }
                tvNotice.setVisibility(View.GONE);
                for (Question question : objects) {
                    // Check if the current user asked or liked this question
                    boolean forCurrentUser =
                            question.getAsker().getUsername()
                                    .equals(ParseUser.getCurrentUser().getUsername()) ||
                                    question.isLiked();
                    if (question.getAnswer() != null && forCurrentUser) {
                        mMessages.add(question);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (mMessages.size() == 0) {
                    tvNotice.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    protected void search() {
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                final ParseQuery<Question> boardQuestions =
                        QueryFactory.QuestionQuery.getStudentInboxMessages();
                boardQuestions.findInBackground(new FindCallback<Question>() {
                    @Override
                    public void done(List<Question> objects, ParseException e) {

                        List<Question> questions = new ArrayList<>();
                        for (Question question : objects) {
                            // Check if the current user asked or liked this question
                            boolean forCurrentUser =
                                    question.getAsker().getUsername()
                                            .equals(ParseUser.getCurrentUser().getUsername()) ||
                                            question.isLiked();
                            if (question.getAnswer() != null && forCurrentUser) {
                                questions.add(question);
                            }
                        }

                        List<Question> result = Search.mSearch(questions, query);
                        mMessages.clear();
                        mMessages.addAll(result);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    mMessages.clear();
                    queryMessages();
                }
                return false;
            }
        });
    }
}
