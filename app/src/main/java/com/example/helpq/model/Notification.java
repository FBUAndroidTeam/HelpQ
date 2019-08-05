package com.example.helpq.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_TAB = "tab";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public int getTab() {
        return getNumber(KEY_TAB).intValue();
    }

    public void setTab(int tab) {
        Integer tabObject = new Integer(tab);
        put(KEY_TAB, tabObject);
    }
}
