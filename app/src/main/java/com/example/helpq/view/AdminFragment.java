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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;

public class AdminFragment extends Fragment {

    public static final String TAG = "AdminFragment";
    private FragmentPagerAdapter mAdapterViewPager;
    private ViewPager vpPager;
    private BottomNavigationView mNagivationView;

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
        mNagivationView = view.findViewById(R.id.admin_navigation);

        vpPager.setAdapter(mAdapterViewPager);
        vpPager.setCurrentItem(2);
        mNagivationView.setSelectedItemId(R.id.action_queue);
        vpPager.setOnPageChangeListener(new AdminPageChanger());

        setupNavigationView();
    }

    private void setupNavigationView() {
        mNagivationView.setOnNavigationItemSelectedListener(
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
                        }
                        return false;
                    }
                });
    }

    public static class AdminPagerAdapter extends FragmentPagerAdapter {

        private static int NUM_ITEMS = 4; // Number of pages
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
                    mNagivationView.setSelectedItemId(R.id.action_profile);
                    break;
                case 1:
                    mNagivationView.setSelectedItemId(R.id.action_enrolled);
                    break;
                case 2:
                    mNagivationView.setSelectedItemId(R.id.action_queue);
                    break;
                case 3:
                    mNagivationView.setSelectedItemId(R.id.action_workshop);
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
