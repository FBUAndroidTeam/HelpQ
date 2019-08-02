package com.example.helpq.view;

import android.util.Log;
import android.view.View;

import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
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

                // Hide notices and clear message lists
                tvNotice.setVisibility(View.GONE);
                tvSearchNotice.setVisibility(View.GONE);
                mMessages.clear();
                mAllMessages.clear();

                // Retrieve the messages that should be displayed
                List<Question> messages = getBoardMessages(objects);
                mMessages.addAll(messages);
                mAllMessages.addAll(messages);
                mAdapter.notifyDataSetChanged();

                // Display a notice if the user has no messages
                if (mMessages.size() == 0) {
                    tvNotice.setVisibility(View.VISIBLE);
                }
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
