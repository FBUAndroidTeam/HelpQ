package com.example.helpq.model;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.helpq.R;
import com.example.helpq.view.admin_views.CreateWorkshopFragment;
import com.example.helpq.view.student_views.StudentWorkshopFragment;
import com.parse.ParseUser;

import static android.app.Notification.PRIORITY_HIGH;
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
                channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.setShowBadge(true);
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
        String title = "notset";
        if(User.isAdmin(ParseUser.getCurrentUser())) {
            title = CreateWorkshopFragment.workshop;
        } else {
            title = StudentWorkshopFragment.workshop;
        }
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle(getResources().getString(R.string.workshop_alarm))
                .setContentText(title + " " +
                        getResources().getString(R.string.workshop_alarm_description))
                .setSmallIcon(R.drawable.notification_icon)
                .setPriority(PRIORITY_HIGH)
                .setAutoCancel(true);
    }
}