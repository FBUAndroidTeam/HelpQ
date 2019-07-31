package com.example.helpq.model;

import android.content.Context;

import com.example.helpq.R;

import java.util.ArrayList;
import java.util.List;

public class WaitTimeCalculator {

    private static final String TAG = "WaitTimeCalculator";
    private static final String BLOCKING = "\uD83D\uDED1";
    private static final String STRETCH = "\uD83D\uDE80";
    private static final String CURIOSITY = "\uD83D\uDD0D";

    private static Context mContext;
    private List<Question> mBlockingQuestions;
    private List<Question> mStretchQuestions;
    private List<Question> mCuriosityQuestions;

    private long mBlockingTime;
    private long mStretchTime;
    private long mCuriosityTime;

    private WaitTimeHelper mHelper;

    public WaitTimeCalculator(Context context, List<Question> questions) {
        mContext = context;
        mBlockingQuestions = new ArrayList<>();
        mStretchQuestions = new ArrayList<>();
        mCuriosityQuestions = new ArrayList<>();
        mHelper = new WaitTimeHelper(context);

        for (Question question : questions) {
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

        mBlockingTime = calculateWaitTime(mBlockingQuestions);
        mStretchTime = calculateWaitTime(mStretchQuestions);
        mCuriosityTime = calculateWaitTime(mCuriosityQuestions);
    }

    public String getBlockingWaitTime() {
        return mContext.getResources().getString(R.string.PRIORITY_BLOCKING)
                + " " + mHelper.getWaitTime(mBlockingTime, true);
    }

    public String getStretchWaitTime() {
        return mContext.getResources().getString(R.string.PRIORITY_STRETCH)
                + " " + mHelper.getWaitTime(mStretchTime, true);
    }

    public String getCuriosityWaitTime() {
        return mContext.getResources().getString(R.string.PRIORITY_CURIOSITY)
                + " " + mHelper.getWaitTime(mCuriosityTime, true);
    }

    // Return the wait time for one question.
    public String getQuestionWaitTime(Question question, String priority) {
        return mHelper.getWaitTime(calculateWaitTime(question, priority), false);
    }

    // Calculate wait time for one question.
    private long calculateWaitTime(Question question, String priority) {
        long diff = System.currentTimeMillis() - question.getCreatedAt().getTime();
        long waitTime = 0;
        switch (priority) {
            case BLOCKING:
                waitTime = mBlockingTime - diff;
                break;
            case STRETCH:
                waitTime = mStretchTime - diff;
                break;
            case CURIOSITY:
                waitTime = mCuriosityTime - diff;
                break;
        }
        return (waitTime < 0 ? 0 : waitTime);
    }

    // Calculate the average wait time over all questions.
    private long calculateWaitTime(List<Question> questions) {
        // Calculate total time to answer all questions
        long totalTime = 0;
        for (Question question : questions) {
            totalTime += question.getTimeDifference();
        }

        // Average over the number of questions
        int size = questions.size();
        if (size == 0) {
            return 0;
        }
        return getRoundedQuotient(totalTime, size);
    }

    private long getRoundedQuotient(long dividend, int divisor) {
        return (dividend + (divisor / 2)) / divisor;
    }
}
