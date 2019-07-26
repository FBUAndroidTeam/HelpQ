package com.example.helpq.view;

import android.util.Log;
import android.view.View;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class StudentBoardFragment extends InboxFragment {

    public static final String TAG = "StudentBoardFragment";

    public static StudentBoardFragment newInstance() {
        return new StudentBoardFragment();
    }

    // Query for public messages intended for all students
    protected void queryMessages() {
        final ParseQuery<Question> messageQuery = new ParseQuery<Question>(Question.class);
        messageQuery.include(Question.KEY_ASKER)
                .whereEqualTo(Question.KEY_ARCHIVED, true)
                .whereEqualTo(Question.KEY_IS_PRIVATE, false)
                .whereEqualTo(Question.KEY_HELP_TYPE,
                        getContext().getResources().getString(R.string.written))
                .orderByDescending(Question.KEY_ANSWERED_AT);

        messageQuery.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with message query");
                    e.printStackTrace();
                    return;
                }

                tvNotice.setVisibility(View.GONE);
                for (Question question : objects) {
                    // Admin of current user
                    String userAdmin = User.getAdminName(ParseUser.getCurrentUser());
                    // Admin of asker
                    String askerAdmin = User.getAdminName(question.getAsker());

                    if (askerAdmin.equals(userAdmin) && question.getAnswer() != null) {
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
}
