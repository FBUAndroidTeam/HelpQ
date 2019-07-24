package com.example.helpq.model;

import android.content.Context;

import com.example.helpq.R;

import java.util.ArrayList;
import java.util.Date;
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

    private String mBlockingTime;
    private String mStretchTime;
    private String mCuriosityTime;

    public WaitTimeCalculator(Context context, List<Question> questions) {
        mContext = context;
        mBlockingQuestions = new ArrayList<>();
        mStretchQuestions = new ArrayList<>();
        mCuriosityQuestions = new ArrayList<>();

        for (Question question : questions) {
            addQuestion(question);
        }

        mBlockingTime = calculateWaitTime(mBlockingQuestions);
        mStretchTime = calculateWaitTime(mStretchQuestions);
        mCuriosityTime = calculateWaitTime(mCuriosityQuestions);
    }

    public String getBlockingWaitTime() {
        return mContext.getResources().getString(R.string.blocking_wait_time)
                + " " + mBlockingTime;
    }

    public String getStretchWaitTime() {
        return mContext.getResources().getString(R.string.stretch_wait_time)
                + " " + mStretchTime;
    }

    public String getCuriosityWaitTime() {
        return mContext.getResources().getString(R.string.curiosity_wait_time)
                + " " + mCuriosityTime;
    }

    // Add question to one of three lists, based on priority.
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

    // Calculate the average wait time over all questions.
    private String calculateWaitTime(List<Question> questions) {
        // Calculate total time to answer all questions
        long totalTime = 0;
        for (Question question : questions) {
            Date asked = question.getCreatedAt();
            Date answered = question.getAnsweredAt();
            long diff = answered.getTime() - asked.getTime();
            totalTime += diff;
        }

        // Average over the number of questions
        int size = questions.size();
        if (size == 0) {
            return mContext.getResources().getString(R.string.default_wait_time);
        }
        long averageTime = (totalTime + (size / 2)) / size;

        // Convert the time to average time to hours and minutes
        long averageMinutes = averageTime / (60 * 1000) % 60;
        long averageHours = averageTime / (60 * 60 * 1000);
        String time = null;
        if (averageHours != 0) {
            time = String.format(mContext.getResources().getString(R.string.format_hours),
                    averageHours, averageMinutes);
        } else {
            time = String.format(mContext.getResources().getString(R.string.format_minutes),
                    averageMinutes);
        }
        return time;
    }
}
