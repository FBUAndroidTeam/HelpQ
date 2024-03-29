package com.example.helpq.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Workshop")
public class Workshop extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_ATTENDEES = "attendees";
    public static final String KEY_CREATOR = "creator";

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

    public void setAttendee(ParseUser attendee) {
        add(KEY_ATTENDEES, attendee);
    }

    public void unsignUp(ParseUser parseUser) {
        ArrayList<ParseUser> arrToDelete = new ArrayList<ParseUser>();
        arrToDelete.add(parseUser);
        removeAll(KEY_ATTENDEES, arrToDelete);
    }

    public ParseUser getCreator() {
        return getParseUser(KEY_CREATOR);
    }

    public void setCreator(ParseUser creator) {
        put(KEY_CREATOR, creator);
    }

    public String getDate() {
        String date = "";
        DateFormat srcDf = new SimpleDateFormat("MMMd", Locale.ENGLISH);
        date = srcDf.format(this.getStartTime());
        return date;
    }

    public String getTime() {
        String time = "";
        DateFormat srcDf = new SimpleDateFormat("hh:mm aaa", Locale.ENGLISH);
        time = srcDf.format(this.getStartTime());
        return time;
    }
}
