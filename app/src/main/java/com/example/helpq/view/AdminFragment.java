package com.example.helpq.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;
import com.example.helpq.model.Notification;
import com.example.helpq.model.NotificationHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AdminFragment extends Fragment {

    public static final String TAG = "AdminFragment";
    private FragmentPagerAdapter mAdapterViewPager;
    private ViewPager vpPager;
    private BottomNavigationView mNavigationView;
    private NotificationHelper mHelper;

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vpPager = view.findViewById(R.id.vpPager);
        mAdapterViewPager = new AdminPagerAdapter(getFragmentManager(), getContext());
        mNavigationView = view.findViewById(R.id.admin_navigation);

        vpPager.setAdapter(mAdapterViewPager);
        vpPager.setCurrentItem(2);
        mNavigationView.setSelectedItemId(R.id.action_queue);
        vpPager.setOnPageChangeListener(new AdminPageChanger());

        mHelper = new NotificationHelper(mNavigationView, getContext());

        addNotificationBadges();
        setupNavigationView();
    }

    // Add badges to the tabs that have notifications.
    private void addNotificationBadges() {
        ParseQuery<Notification> notificationQuery =
                new ParseQuery<Notification>(Notification.class);
        notificationQuery.whereEqualTo(Notification.KEY_USER, ParseUser.getCurrentUser());
        notificationQuery.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error querying for notifications");
                }

                int profileCount = 0;
                int enrolledCount = 0;
                int queueCount = 0;
                int workshopsCount = 0;
                int boardCount = 0;

                for (Notification notification : objects) {
                    switch (notification.getTab()) {
                        case 0:
                            profileCount++;
                            break;
                        case 1:
                            enrolledCount++;
                            break;
                        case 2:
                            queueCount++;
                            break;
                        case 3:
                            workshopsCount++;
                            break;
                        case 4:
                            boardCount++;
                            break;
                    }
                }

                if (profileCount > 0) mHelper.addBadge(R.id.action_profile, profileCount);
                if (enrolledCount > 0) mHelper.addBadge(R.id.action_workshop, enrolledCount);
                if (queueCount > 0) mHelper.addBadge(R.id.action_queue, queueCount);
                if (workshopsCount > 0) mHelper.addBadge(R.id.action_inbox, workshopsCount);
                if (boardCount > 0) mHelper.addBadge(R.id.action_board, boardCount);
            }
        });
    }

    // Remove notifications badge from this tab, if one exists.
    private void removeNotificationBadges(int tab, final int itemId) {
        ParseQuery<Notification> notificationQuery =
                new ParseQuery<Notification>(Notification.class);
        notificationQuery.whereEqualTo(Notification.KEY_USER, ParseUser.getCurrentUser());
        notificationQuery.whereEqualTo(Notification.KEY_TAB, tab);
        notificationQuery.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                for (Notification notification : objects) {
                    try {
                        notification.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    notification.saveInBackground();
                }
                mHelper.removeBadge(itemId);
            }
        });
    }

    private void setupNavigationView() {
        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_profile:
                                vpPager.setCurrentItem(0);
                                return true;
                            case R.id.action_enrolled:
                                vpPager.setCurrentItem(1);
                                return true;
                            case R.id.action_queue:
                                vpPager.setCurrentItem(2);
                                return true;
                            case R.id.action_workshop:
                                vpPager.setCurrentItem(3);
                                return true;
                            case R.id.action_board:
                                vpPager.setCurrentItem(4);
                                return true;
                        }
                        return false;
                    }
                });
    }

    public static class AdminPagerAdapter extends FragmentPagerAdapter {

        private static int NUM_ITEMS = 5; // Number of pages
        private Context mContext;

        public AdminPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AdminProfileFragment.newInstance();
                case 1:
                    return AdminEnrolledFragment.newInstance();
                case 2:
                    return AdminQueueFragment.newInstance();
                case 3:
                    return AdminWorkshopFragment.newInstance();
                case 4:
                    return AdminBoardFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getResources().getString(R.string.profile_tab);
                case 1:
                    return mContext.getResources().getString(R.string.enrolled_tab);
                case 2:
                    return mContext.getResources().getString(R.string.queue_tab);
                case 3:
                    return mContext.getResources().getString(R.string.workshops_tab);
                case 4:
                    return mContext.getResources().getString(R.string.board_tab);
                default:
                    return null;
            }
        }
    }

    public class AdminPageChanger implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    mNavigationView.setSelectedItemId(R.id.action_profile);
                    removeNotificationBadges(0, R.id.action_profile);
                    break;
                case 1:
                    mNavigationView.setSelectedItemId(R.id.action_enrolled);
                    removeNotificationBadges(1, R.id.action_enrolled);
                    break;
                case 2:
                    mNavigationView.setSelectedItemId(R.id.action_queue);
                    removeNotificationBadges(2, R.id.action_queue);
                    break;
                case 3:
                    mNavigationView.setSelectedItemId(R.id.action_workshop);
                    removeNotificationBadges(3, R.id.action_workshop);
                    break;
                case 4:
                    mNavigationView.setSelectedItemId(R.id.action_board);
                    removeNotificationBadges(4, R.id.action_board);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
