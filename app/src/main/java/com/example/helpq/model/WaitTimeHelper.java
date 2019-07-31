package com.example.helpq.model;

import android.content.Context;

import com.example.helpq.R;

public class WaitTimeHelper {

    private Context mContext;
    public static final String BLOCKING = "\uD83D\uDED1";
    public static final String STRETCH = "\uD83D\uDE80";
    public static final String CURIOSITY = "\uD83D\uDD0D";

    public WaitTimeHelper(Context context) {
        mContext = context;
    }

    public String getBlockingWaitTime(long blockingTime) {
        return mContext.getResources().getString(R.string.PRIORITY_BLOCKING)
                + " " + getWaitTime(blockingTime, true);
    }

    public String getStretchWaitTime(long stretchTime) {
        return mContext.getResources().getString(R.string.PRIORITY_STRETCH)
                + " " + getWaitTime(stretchTime, true);
    }

    public String getCuriosityWaitTime(long curiosityTime) {
        return mContext.getResources().getString(R.string.PRIORITY_CURIOSITY)
                + " " + getWaitTime(curiosityTime, true);
    }

    public String getWaitTime(long averageTime, boolean showDefault) {
        if (averageTime == 0 && showDefault) {
            return mContext.getResources().getString(R.string.default_wait_time);
        }
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

    // Calculate wait time for one question.
    private long calculateWaitTime(Question question, String priority, WaitTime waitTime) {
        long diff = System.currentTimeMillis() - question.getCreatedAt().getTime();
        long time = 0;
        switch (priority) {
            case BLOCKING:
                time = waitTime.getBlockingTime() - diff;
                break;
            case STRETCH:
                time = waitTime.getStretchTime() - diff;
                break;
            case CURIOSITY:
                time = waitTime.getCuriosityTime() - diff;
                break;
        }
        return (time < 0 ? 0 : time);
    }

    public long updateWaitTime(long oldAverage, long oldSize, long newTime) {
        return oldAverage + (newTime - oldAverage) / (oldSize + 1);
    }
}
