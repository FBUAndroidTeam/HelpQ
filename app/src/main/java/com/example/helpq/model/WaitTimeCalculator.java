package com.example.helpq.model;

import android.content.res.Resources;

import com.example.helpq.R;

import java.util.ArrayList;
import java.util.List;

public class WaitTimeCalculator {

    private static final String TAG = "WaitTimeCalculator";
    private static final String BLOCKING = "\uD83D\uDED1";
    private static final String STRETCH = "\uD83D\uDE80";
    private static final String CURIOSITY = "\uD83D\uDD0D";

    private List<Question> mBlockingQuestions;
    private List<Question> mStretchQuestions;
    private List<Question> mCuriosityQuestions;

    private long mBlockingTime;
    private long mStretchTime;
    private long mCuriosityTime;

    public WaitTimeCalculator(List<Question> questions) {
        mBlockingQuestions = new ArrayList<>();
        mStretchQuestions = new ArrayList<>();
        mCuriosityQuestions = new ArrayList<>();

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

    public long getBlockingWaitTime() {
        return mBlockingTime;
    }

    public long getStretchWaitTime() {
        return mStretchTime;
    }
    public long getCuriosityWaitTime() {
        return mCuriosityTime;
    }

    public static String formatWaitTime(long averageTime) {
        if (averageTime == 0) {
            return Resources.getSystem().getString(R.string.default_wait_time);
        }
        // Convert the time to average time to hours and minutes
        long averageMinutes = averageTime / (60 * 1000) % 60;
        long averageHours = averageTime / (60 * 60 * 1000);
        String time = null;
        if (averageHours != 0) {
            time = String.format(Resources.getSystem().getString(R.string.format_hours),
                    averageHours, averageMinutes);
        } else {
            time = String.format(Resources.getSystem().getString(R.string.format_minutes),
                    averageMinutes);
        }
        return time;
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
