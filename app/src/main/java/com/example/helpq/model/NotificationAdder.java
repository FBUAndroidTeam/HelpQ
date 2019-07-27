package com.example.helpq.model;

import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.helpq.R;

public class NotificationAdder {

    private BottomNavigationView mNavigationView;
    private Context mContext;
    private final int MAX_NOTIFICATIONS = 100;

    public NotificationAdder(BottomNavigationView navigationView, Context context) {
        mNavigationView = navigationView;
        mContext = context;
    }

    public void setProfileTabNotification(int number) {
        setNotificationBadge(R.id.action_profile, number);
    }

    public void setEnrolledTabNotification(int number) {
        setNotificationBadge(R.id.action_enrolled, number);
    }

    public void setQueueTabNotification(int number) {
        setNotificationBadge(R.id.action_queue, number);
    }

    public void setWorkshopTabNotification(int number) {
        setNotificationBadge(R.id.action_workshop, number);
    }

    public void setBoardTabNotification(int number) {
        setNotificationBadge(R.id.action_board, number);
    }

    public void setInboxTabNotification(int number) {
        setNotificationBadge(R.id.action_inbox, number);
    }

    private void setNotificationBadge(int layoutId, int number)
    {
        BottomNavigationItemView notificationsTab = mNavigationView.findViewById(layoutId);
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
}
