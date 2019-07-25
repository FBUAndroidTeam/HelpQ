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

public class StudentFragment extends Fragment {

    public static final String TAG = "StudentFragment";
    private FragmentPagerAdapter mAdapterViewPager;
    private ViewPager vpPager;
    private BottomNavigationView mNagivationView;

    public static StudentFragment newInstance() {
        return new StudentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vpPager = view.findViewById(R.id.vpPager);
        mNagivationView = view.findViewById(R.id.student_navigation);
        mAdapterViewPager = new StudentPagerAdapter(getFragmentManager(), getContext());

        vpPager.setAdapter(mAdapterViewPager);
        vpPager.setCurrentItem(2);
        mNagivationView.setSelectedItemId(R.id.action_queue);
        vpPager.setOnPageChangeListener(new StudentPageChanger());

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
                    case R.id.action_workshop:
                        vpPager.setCurrentItem(1);
                        return true;
                    case R.id.action_queue:
                        vpPager.setCurrentItem(2);
                        return true;
                    case R.id.action_inbox:
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

    public static class StudentPagerAdapter extends FragmentPagerAdapter {

        private static int NUM_ITEMS = 5; // Number of pages
        private Context mContext;

        public StudentPagerAdapter(FragmentManager fm, Context context) {
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
                    return StudentProfileFragment.newInstance();
                case 1:
                    return StudentWorkshopFragment.newInstance();
                case 2:
                    return StudentQueueFragment.newInstance();
                case 3:
                    return StudentInboxFragment.newInstance();
                case 4:
                    return StudentBoardFragment.newInstance();
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
                    return mContext.getResources().getString(R.string.workshops_tab);
                case 2:
                    return mContext.getResources().getString(R.string.queue_tab);
                case 3:
                    return mContext.getResources().getString(R.string.inbox_tab);
                case 4:
                    return mContext.getResources().getString(R.string.board_tab);
                default:
                    return null;
            }
        }
    }

    public class StudentPageChanger implements ViewPager.OnPageChangeListener {

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
                    mNagivationView.setSelectedItemId(R.id.action_workshop);
                    break;
                case 2:
                    mNagivationView.setSelectedItemId(R.id.action_queue);
                    break;
                case 3:
                    mNagivationView.setSelectedItemId(R.id.action_inbox);
                    break;
                case 4:
                    mNagivationView.setSelectedItemId(R.id.action_board);
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
