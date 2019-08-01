package com.example.helpq.view;

import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Search;
import com.example.helpq.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class StudentBoardFragment extends InboxFragment {

    public static final String TAG = "StudentBoardFragment";

    public static StudentBoardFragment newInstance() {
        return new StudentBoardFragment();
    }

    // Query for public messages intended for all students
    protected void queryMessages() {
        final ParseQuery<Question> query = QueryFactory.QuestionQuery.getStudentBoardMessages();
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with message query");
                    e.printStackTrace();
                    return;
                }
                tvNotice.setVisibility(View.GONE);
                mMessages.addAll(getBoardMessages(objects));
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
                        QueryFactory.QuestionQuery.getStudentBoardMessages();
                boardQuestions.findInBackground(new FindCallback<Question>() {
                    @Override
                    public void done(List<Question> objects, ParseException e) {
                        List<Question> result = Search.mSearch(getBoardMessages(objects), query);
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

    // Return the list of questions that have been asked by the current user or by a
    // classmate of the current user from the given list of Question objects.
    private List<Question> getBoardMessages(List<Question> objects) {
        List<Question> messages = new ArrayList<>();
        for (Question question : objects) {
            // Admin of current user
            String userAdmin = User.getAdminName(ParseUser.getCurrentUser());
            // Admin of asker
            String askerAdmin = User.getAdminName(question.getAsker());

            if (askerAdmin.equals(userAdmin) && question.getAnswer() != null) {
                messages.add(question);
            }
        }
        return messages;
    }
}
