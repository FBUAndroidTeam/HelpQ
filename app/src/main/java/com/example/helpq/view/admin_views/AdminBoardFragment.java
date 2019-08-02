package com.example.helpq.view.admin_views;

import android.util.Log;
import android.view.View;

import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.view.student_views.InboxFragment;
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
        final ParseQuery<Question> query = QueryFactory.Questions.getAdminBoardMessages();
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
