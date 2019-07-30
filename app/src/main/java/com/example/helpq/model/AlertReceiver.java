package com.example.helpq.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmHelper alarmHelper = new AlarmHelper(context);
        NotificationCompat.Builder nb = alarmHelper.getChannelNotification();
        alarmHelper.getManager().notify(1, nb.build());
    }
}
