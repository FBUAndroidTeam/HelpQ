package com.example.helpq.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;

public class StudentFragment extends Fragment {

    public static final String TAG = "StudentFragment";
    private FragmentPagerAdapter mAdapterViewPager;

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
        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        mAdapterViewPager = new StudentPagerAdapter(getFragmentManager(), getContext());
        vpPager.setAdapter(mAdapterViewPager);
    }

    public static class StudentPagerAdapter extends FragmentPagerAdapter {

        // Number of pages
        private static int NUM_ITEMS = 4;
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
                    return StudentQueueFragment.newInstance();
                case 1:
                    return StudentWorkshopFragment.newInstance();
                case 2:
                    return StudentInboxFragment.newInstance();
                case 3:
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
                    return mContext.getResources().getString(R.string.queue_tab);
                case 1:
                    return mContext.getResources().getString(R.string.workshops_tab);
                case 2:
                    return mContext.getResources().getString(R.string.inbox_tab);
                case 3:
                    return mContext.getResources().getString(R.string.board_tab);
                default:
                    return null;
            }
        }
    }
}
