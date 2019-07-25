package com.example.helpq.model;

import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

public class User {

    public static String KEY_IS_ADMIN = "isAdmin";
    public static String KEY_FULL_NAME = "fullName";
    public static String KEY_ADMIN_NAME = "adminName";
    public static String KEY_WAIT_TIMES = "waitTimes";

    private static int BLOCKING_INDEX = 0;
    private static int STRETCH_INDEX = 1;
    private static int CURIOSITY_INDEX = 2;
    private static int SIZE_OFFSET = 3;

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

    public static void initWaitTimes(ParseUser user, long blockingTime, long blockingSize,
                                     long stretchTime, long stretchSize, long curiosityTime,
                                     long curiositySize) {
        user.add(KEY_WAIT_TIMES, blockingTime);
        user.add(KEY_WAIT_TIMES, stretchTime);
        user.add(KEY_WAIT_TIMES, curiosityTime);
        user.add(KEY_WAIT_TIMES, blockingSize);
        user.add(KEY_WAIT_TIMES, stretchSize);
        user.add(KEY_WAIT_TIMES, curiositySize);
    }

    public static long getBlockingMillis(ParseUser user) {
        return getLongFromArray(user, BLOCKING_INDEX);
    }

    public static long getStretchMillis(ParseUser user) {
        return getLongFromArray(user, STRETCH_INDEX);
    }

    public static long getCuriosityMillis(ParseUser user) {
        return getLongFromArray(user, CURIOSITY_INDEX);
    }

    public static long getBlockingSize(ParseUser user) {
        return getLongFromArray(user, BLOCKING_INDEX + SIZE_OFFSET);
    }

    public static long getStretchSize(ParseUser user) {
        return getLongFromArray(user, STRETCH_INDEX + SIZE_OFFSET);
    }

    public static long getCuriositySize(ParseUser user) {
        return getLongFromArray(user, CURIOSITY_INDEX + SIZE_OFFSET);
    }

    private static long getLongFromArray(ParseUser user, int index) {
        JSONArray array = getWaitTimes(user);
        if (array == null) return -1;
        try {
            return array.getLong(index);
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void setBlockingWaitTime(ParseUser user, long waitTime, long size) {
        JSONArray array = getWaitTimes(user);
        try {
            array.put(0, waitTime);
            array.put(3, size);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray getWaitTimes(ParseUser user) {
        return user.getJSONArray(KEY_WAIT_TIMES);
    }
}