package com.example.helpq;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("help-q-id-secretsss")
                .clientKey("fbu-cass-julie-loni-keyy")
                .server("http://help-q.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
