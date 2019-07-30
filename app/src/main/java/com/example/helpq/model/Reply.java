package com.example.helpq.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reply")
public class Reply extends ParseObject {
    public static final String KEY_TEXT = "text";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_USER = "user";
    public static final String KEY_VERIFY = "isVerified";

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setQuestion(Question question) {
        put(KEY_QUESTION, question);
    }

    public Question getQuestion() {
        return (Question) get(KEY_QUESTION);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public ParseUser getUser() {
        return (ParseUser) get(KEY_USER);
    }

    public void setVerified(boolean verified) {
        put(KEY_VERIFY, verified);
    }

    public boolean getVerification(){
        return getBoolean(KEY_VERIFY);
    }
}
