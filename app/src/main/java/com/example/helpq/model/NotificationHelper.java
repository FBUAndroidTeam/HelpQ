package com.example.helpq.model;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.helpq.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class NotificationHelper {

    private static String TAG = "NotificationHelper";
    private BottomNavigationView mNavigationView;
    private Context mContext;
    private final int MAX_NOTIFICATIONS = 100;

    // Constructor
    public NotificationHelper(BottomNavigationView navigationView, Context context) {
        mNavigationView = navigationView;
        mContext = context;
    }

    // Add a notification badge to the item with given id.
    public void addBadge(int itemId, int number)
    {
        BottomNavigationItemView notificationsTab = mNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(mContext)
                .inflate(R.layout.notification_badge, notificationsTab, false);
        TextView notificationBadgeTextView = badge.findViewById(R.id.tvNotificationBadge);
        if (number < MAX_NOTIFICATIONS) {
            notificationBadgeTextView.setText(String.format("%d", number));
        } else {
            notificationBadgeTextView.setText(
                    mContext.getResources().getString(R.string.max_notifications));
        }
        notificationsTab.addView(badge);
    }

    // Remove the notification badge from the item with the given id, if it has a badge.
    public void removeBadge(int itemId) {
        BottomNavigationItemView notificationsTab = mNavigationView.findViewById(itemId);
        if (notificationsTab.getChildCount() > 2) {
            notificationsTab.removeViewAt(2);
        }
    }

    // Delete all notifications that points to the Question with this object id.
    public static void deleteNotificationsByQuestion(String questionId) {
        ParseQuery<Notification> query =
                QueryFactory.Notifications.getNotificationsByQuestion(questionId);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying for notifications by question");
                    return;
                }
                deleteNotifications(objects);
            }
        });
    }

    // Delete all notifications that points to the Question with this object id.
    public static void deleteNotificationsByWorkshop(String workshopId) {
        ParseQuery<Notification> query =
                QueryFactory.Notifications.getNotificationsByWorkshop(workshopId);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying for notifications by workshop");
                    return;
                }
                deleteNotifications(objects);
            }
        });
    }

    // Delete all notifications in the list of objects.
    private static void deleteNotifications(List<Notification> objects) {
        for (int i = 0; i < objects.size(); i++) {
            Notification notification = objects.get(i);
            try {
                notification.delete();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            Log.d(TAG, "Notification deleted successfully!");
            notification.saveInBackground();
        }
        Log.d(TAG, objects.size() + " notifications deleted!");
    }
}
