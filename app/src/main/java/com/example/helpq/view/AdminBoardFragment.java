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

public class AdminBoardFragment extends InboxFragment {

    public static final String TAG = "AdminBoardFragment";

    public static AdminBoardFragment newInstance() {
        return new AdminBoardFragment();
    }

    // Query for all questions answered by this admin
    protected void queryMessages() {
        final ParseQuery<Question> query = QueryFactory.QuestionQuery.getAdminBoardMessages();
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
                        QueryFactory.QuestionQuery.getAdminBoardMessages();
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

    // Return the list of questions that have been answered by the current user
    // from the given list of Question objects.
    private List<Question> getBoardMessages(List<Question> objects) {
        List<Question> messages = new ArrayList<>();
        for (Question question : objects) {
            // Admin of asker
            String askerAdmin = User.getAdminName(question.getAsker());

            if (askerAdmin.equals(ParseUser.getCurrentUser().getUsername())
                    && question.getAnswer() != null) {
                messages.add(question);
            }
        }
        return messages;
    }
}
