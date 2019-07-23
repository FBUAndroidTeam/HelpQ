package com.example.helpq.model;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Question")
public class Question extends ParseObject implements Comparable<Question>{

    private static final String KEY_TEXT = "questionText";
    private static final String KEY_PRIORITY = "priorityEmoji";
    public static final String KEY_ASKER = "student";
    public static final String KEY_ARCHIVED = "isArchived";
    public static final String KEY_HELP_TYPE = "helpType";
    public static final String KEY_FULL_NAME = "fullName";
    public static final String KEY_CREATED_AT = "createdAt";

    // Stretch keys
    private static final String KEY_ANSWER = "answerText";
    private static final String KEY_ANSWERED_AT = "answeredAt";
    public static final String KEY_IS_PRIVATE = "isPrivate";

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public ParseUser getAsker() {
        return getParseUser(KEY_ASKER);
    }

    public void setAsker(ParseUser asker) {
        put(KEY_ASKER, asker);
    }

    public String getPriority() {
        return getString(KEY_PRIORITY);
    }

    public void setPriority(String priority) {
        put(KEY_PRIORITY, priority);
    }

    public boolean getIsArchived () {
        return getBoolean(KEY_ARCHIVED);
    }

    public void setIsArchived (boolean archived) {
        put(KEY_ARCHIVED, archived);
    }

    // Stretch getters/setters
    public String getAnswer() {
        return getString(KEY_ANSWER);
    }

    public void setAnswer(String answer) {
        put(KEY_ANSWER, answer);
    }

    public Date getAnsweredAt() {
        return getDate(KEY_ANSWERED_AT);
    }

    public void setAnsweredAt(Date date) {
        put(KEY_ANSWERED_AT, date);
    }

    public String getHelpType() {
        return getString(KEY_HELP_TYPE);
    }

    public void setHelpType(String helpType) {
        put(KEY_HELP_TYPE, helpType);
    }

    public boolean isPrivate() {
       return getBoolean(KEY_IS_PRIVATE);
    }

    public void setIsPrivate(boolean isPrivate) {
        put(KEY_IS_PRIVATE, isPrivate);
    }

    // Get the date that this question is asked.
    public String getDate() {
        String strDate = "";

        try {
            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

            // Parse the date string into Date object
            Date date = srcDf.parse(srcDf.format(this.getCreatedAt().getTime()));
            DateFormat destDf = new SimpleDateFormat("EEE, h:mm a");

            // Format the date into another format
            strDate = destDf.format(date);

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return strDate;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        long dateMillis = this.getCreatedAt().getTime();
        relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }

    // Compare questions first by priority, then by date.
    @Override
    public int compareTo(Question o) {
        String priority1 = this.getPriority();
        String priority2 = o.getPriority();
        int comparePriority = priority2.compareTo(priority1);
        if (comparePriority != 0) {
            return comparePriority;
        }
        else {
            Date date1 = this.getCreatedAt();
            Date date2 = o.getCreatedAt();
            return date1.compareTo(date2);
        }
    }

}
