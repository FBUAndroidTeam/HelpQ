package com.example.helpq.model;

import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.Date;

public class Workshop extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ATTENDEES = "attendees";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public Date getStartTime() {
        return getDate(KEY_START_TIME);
    }

    public void setStartTime(Date startTime) {
        put(KEY_START_TIME, startTime);
    }

    public JSONArray getAttendees() {
        return getJSONArray(KEY_ATTENDEES);
    }

    public void setAttendees(JSONArray attendees) {
        put(KEY_ATTENDEES, attendees);
    }
}
