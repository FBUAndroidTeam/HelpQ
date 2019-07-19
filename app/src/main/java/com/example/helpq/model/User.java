package com.example.helpq.model;

import com.parse.ParseUser;

public class User {

    public static String KEY_IS_ADMIN = "isAdmin";
    public static String KEY_FULL_NAME = "fullName";
    public static String KEY_ADMIN_NAME = "adminName";

    // Is the user an admin?
    public static boolean isAdmin(ParseUser user) {
        return user.getBoolean(KEY_IS_ADMIN);
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
}