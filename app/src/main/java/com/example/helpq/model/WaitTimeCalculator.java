package com.example.helpq.model;

import android.content.Context;
import android.util.Log;

import com.example.helpq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class WaitTimeCalculator {

    private static final String TAG = "WaitTimeCalculator";
    private static final String BLOCKING = "\uD83D\uDED1";
    private static final String STRETCH = "\uD83D\uDE80";
    private static final String CURIOSITY = "\uD83D\uDD0D";

    private Context mContext;
    private List<Question> mBlockingQuestions;
    private List<Question> mStretchQuestions;
    private List<Question> mCuriosityQuestions;

    public WaitTimeCalculator(Context context) {
        mContext = context;
        mBlockingQuestions = new ArrayList<>();
        mStretchQuestions = new ArrayList<>();
        mCuriosityQuestions = new ArrayList<>();
        queryQuestions();
    }

    private void queryQuestions() {
        final ParseQuery<Question> questionQuery = new ParseQuery<Question>(Question.class);
        questionQuery.whereEqualTo(Question.KEY_ARCHIVED, true)
                .include(Question.KEY_ASKER);

        questionQuery.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with question wait time query");
                    e.printStackTrace();
                    return;
                }

                for (Question question : objects) {

                    // User who asked the question
                    ParseUser asker = question.getAsker();
                    // User who is currently logged in
                    String currUser = ParseUser.getCurrentUser().getUsername();

                    // Admin of current user
                    String currUserAdmin = null;
                    if (!User.isAdmin(ParseUser.getCurrentUser())) {
                        currUserAdmin = User.getAdminName(ParseUser.getCurrentUser());
                    }
                    // Admin of asker
                    String askerAdmin = User.getAdminName(asker);

                    if (currUser.equals(askerAdmin) || askerAdmin.equals(currUserAdmin)) {
                        addQuestion(question);
                    }
                }
            }
        });
    }

    // Add question to one of three lists, based on priority
    private void addQuestion(Question question) {
        switch (question.getPriority()) {
            case BLOCKING:
                mBlockingQuestions.add(question);
                break;
            case STRETCH:
                mStretchQuestions.add(question);
                break;
            case CURIOSITY:
                mCuriosityQuestions.add(question);
                break;
            default:
                break;
        }
    }

    public String getBlockingWaitTime() {
        return mContext.getResources().getString(R.string.blocking_wait_time) + " " + 0;
    }

    public String getStretchWaitTime() {
        return mContext.getResources().getString(R.string.stretch_wait_time) + " " + 0;
    }

    public String getCuriosityWaitTime() {
        return mContext.getResources().getString(R.string.curiosity_wait_time) + " " + 0;
    }
}
