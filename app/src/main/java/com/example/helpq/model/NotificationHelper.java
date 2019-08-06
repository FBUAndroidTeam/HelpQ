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

    // Delete the current user's notifications on this tab.
    public static void deleteNotifications(int tab) {
        ParseQuery<Notification> query = QueryFactory.Notifications.getNotificationsForTab(tab);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying for notifications");
                    return;
                }
                for (Notification notification : objects) {
                    try {
                        notification.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    notification.saveInBackground();
                }
            }
        });
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
}
