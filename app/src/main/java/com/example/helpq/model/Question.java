package com.example.helpq.model;

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

    public static final String KEY_TEXT = "questionText";
    public static final String KEY_ASKER = "student";
    public static final String KEY_PRIORITY = "priorityEmoji";
    public static final String KEY_ARCHIVED = "isArchived";
    public static final String KEY_HELP_TYPE = "helpType";

    //stretch keys
    private static final String KEY_ANSWER = "answerText";
    private static final String KEY_ANSWERED_AT = "answeredAt";

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

    //stretch getters/setters
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

    // Compare questions by their priority.
    @Override
    public int compareTo(Question o) {
        String priority1 = this.getPriority();
        String priority2 = o.getPriority();
        return (priority2.compareTo(priority1));
    }

}
