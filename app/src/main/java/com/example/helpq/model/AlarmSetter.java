package com.example.helpq.model;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.helpq.view.admin_views.CreateWorkshopFragment;
import com.example.helpq.view.student_views.StudentWorkshopFragment;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

public class AlarmSetter {

    public static final String TAG = "AlarmSetter";
    public static final long ONE_MINUTE_IN_MILLIS = 60000;

    private static Question q = new Question();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void onTimeSet(Date workshopTime, String workshopName, Context context) {
        Calendar c = Calendar.getInstance();
        c.setTime(workshopTime);
        c.setTime(timeSetHelper(c));
        if(User.isAdmin(ParseUser.getCurrentUser())) {
            CreateWorkshopFragment.workshop = workshopName;
        } else {
            StudentWorkshopFragment.workshop = workshopName;
        }
        Log.d(TAG, "alarm time: " + q.getRelativeTimeAgo(c.getTimeInMillis()));
        startAlarm(c, context);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void startAlarm(Calendar c, Context context) {
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public static Date timeSetHelper(Calendar calendar) {
        long time = calendar.getTimeInMillis();
        Date alarmTime = new Date(time - (User.getReminderTime(ParseUser.getCurrentUser())
                * ONE_MINUTE_IN_MILLIS));
        return alarmTime;
    }
}
