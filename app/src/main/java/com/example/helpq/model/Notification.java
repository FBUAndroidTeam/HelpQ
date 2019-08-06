package com.example.helpq.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_TAB = "tab";
    public static final String KEY_QUESTION_ID = "questionId";
    public static final String KEY_WORKSHOP_ID = "workshopId";

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

    public String getQuestionId() {
        return getString(KEY_QUESTION_ID);
    }

    public void setQuestionId(String id) {
        put(KEY_QUESTION_ID, id);
    }

    public String getWorkshopId() {
        return getString(KEY_WORKSHOP_ID);
    }

    public void setWorkshopId(String id) {
        put(KEY_WORKSHOP_ID, id);
    }
}
