package com.example.helpq.view.student_views;

import android.util.Log;
import android.view.View;

import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
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
    protected void queryMessages(final String input) {
        pbLoading.setVisibility(View.VISIBLE);
        final ParseQuery<Question> query = QueryFactory.Questions.getStudentInboxMessages();
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
                List<Question> messages = getInboxMessages(objects);
                mAllMessages.addAll(messages);
                findMatches(input);

                // Display a notice if the user has no messages
                if (messages.size() == 0) {
                    tvNotice.setVisibility(View.VISIBLE);
                }
                runLayoutAnimation();
            }
        });
    }

    // Return the list of questions that have been asked or liked by the current user
    // from the given list of Question objects.
    private List<Question> getInboxMessages(List<Question> objects) {
        List<Question> messages = new ArrayList<>();
        for (Question question : objects) {
            boolean forCurrentUser =
                    question.getAsker().getUsername()
                            .equals(ParseUser.getCurrentUser().getUsername()) ||
                            question.isLiked();
            if (question.getAnswer() != null && forCurrentUser) {
                messages.add(question);
            }
        }
        return messages;
    }
}
