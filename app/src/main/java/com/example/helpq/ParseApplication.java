package com.example.helpq;

import android.app.Application;

import com.example.helpq.controller.TypefaceUtil;
import com.example.helpq.model.Notification;
import com.example.helpq.model.Question;
import com.example.helpq.model.Reply;
import com.example.helpq.model.WaitTime;
import com.example.helpq.model.Workshop;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Question.class);
        ParseObject.registerSubclass(Workshop.class);
        ParseObject.registerSubclass(Notification.class);
        ParseObject.registerSubclass(Reply.class);
        ParseObject.registerSubclass(WaitTime.class);
        Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("help-q-id-secretsss")
                .clientKey("fbu-cass-julie-loni-keyy")
                .server("http://help-q.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
        ParseFacebookUtils.initialize(this);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "font_tweb.ttf");
    }
}
