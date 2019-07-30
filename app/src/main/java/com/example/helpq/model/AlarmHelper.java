package com.example.helpq.model;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.helpq.R;
import com.parse.ParseUser;

import static android.app.Notification.VISIBILITY_PUBLIC;

public class AlarmHelper extends ContextWrapper {

    public static final String channelID = "com.example.helpq";
    public static final String channelName = ParseUser.getCurrentUser().getUsername();

    private NotificationManager mManager;

    public AlarmHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID,
                channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(getResources().getString(R.string.workshop_alarm))
                .setContentText(getResources().getString(R.string.workshop_alarm_description))
                .setSmallIcon(R.drawable.com_facebook_button_login_logo)
                .setAutoCancel(true);
    }
}