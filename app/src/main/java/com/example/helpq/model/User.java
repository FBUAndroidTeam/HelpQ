package com.example.helpq.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class User {

    public static String TAG = "User";
    public static String KEY_IS_ADMIN = "isAdmin";
    public static String KEY_FULL_NAME = "fullName";
    public static String KEY_ADMIN_NAME = "adminName";
    public static String KEY_WAIT_TIMES = "waitTimes";

    private static final int BLOCKING_INDEX = 0;
    private static final int STRETCH_INDEX = 1;
    private static final int CURIOSITY_INDEX = 2;

    // Is the user an admin?
    public static boolean isAdmin(ParseUser user) {
        return user.getBoolean(KEY_IS_ADMIN);
    }

    // Set whether or not the user is an admin.
    public static void setIsAdmin(boolean isAdmin, ParseUser user) {
        user.put(KEY_IS_ADMIN, isAdmin);
    }

    // Returns the user's full name.
    public static String getFullName(ParseUser user) {
        return user.getString(KEY_FULL_NAME);
    }

    // Set the user's full name.
    public static void setFullName(String fullName, ParseUser user) {
        user.put(KEY_FULL_NAME, fullName);
    }

    // Returns the name of the current user's admin.
    public static String getAdminName(ParseUser user) {
        return user.getString(KEY_ADMIN_NAME);
    }

    // Set the user's admin name.
    public static void setAdminName(String adminName, ParseUser user) {
        user.put(KEY_ADMIN_NAME, adminName);
    }

    public static long getBlockingTime(ParseUser user) {
        try {
            return user.getJSONArray(KEY_WAIT_TIMES).getLong(BLOCKING_INDEX);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static long getStretchTime(ParseUser user) {
        try {
            return user.getJSONArray(KEY_WAIT_TIMES).getLong(STRETCH_INDEX);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static long getCuriosityTime(ParseUser user) {
        try {
            return user.getJSONArray(KEY_WAIT_TIMES).getLong(CURIOSITY_INDEX);
        } catch (JSONException e) {
            return 0;
        }
    }

    public static void setBlockingTime(ParseUser user, long waitTime) {
        try {
            JSONArray array = user.getJSONArray(KEY_WAIT_TIMES);
            array.put(BLOCKING_INDEX, waitTime);
        } catch (JSONException e) {
            calculateWaitTimes(user);
        }
    }

    public static void setStretchTime(ParseUser user, long waitTime) {
        try {
            JSONArray array = user.getJSONArray(KEY_WAIT_TIMES);
            array.put(STRETCH_INDEX, waitTime);
        } catch (JSONException e) {
            calculateWaitTimes(user);
        }
    }

    public static void setCuriosityTime(ParseUser user, long waitTime) {
        try {
            JSONArray array = user.getJSONArray(KEY_WAIT_TIMES);
            array.put(BLOCKING_INDEX, waitTime);
        } catch (JSONException e) {
            calculateWaitTimes(user);
        }
    }

    private static void calculateWaitTimes(final ParseUser user) {
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

                List<Question> questions = new ArrayList<>();

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
                        questions.add(question);
                    }
                }

                WaitTimeCalculator calculator = new WaitTimeCalculator(questions);
                user.add(KEY_WAIT_TIMES, calculator.getBlockingWaitTime());
                user.add(KEY_WAIT_TIMES, calculator.getStretchWaitTime());
                user.add(KEY_WAIT_TIMES, calculator.getCuriosityWaitTime());
            }
        });
    }
}