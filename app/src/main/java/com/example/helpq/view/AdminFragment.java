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

public class AdminFragment extends Fragment {

    public static final String TAG = "AdminFragment";
    private FragmentPagerAdapter mAdapterViewPager;

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
        ViewPager vpPager = (ViewPager) view.findViewById(R.id.vpPager);
        mAdapterViewPager = new AdminPagerAdapter(getFragmentManager(), getContext());
        vpPager.setAdapter(mAdapterViewPager);
    }

    public static class AdminPagerAdapter extends FragmentPagerAdapter {

        private static int NUM_ITEMS = 3; // Number of pages
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
                    return AdminQueueFragment.newInstance();
                case 1:
                    return AdminEnrolledFragment.newInstance();
                case 2:
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
                    return mContext.getResources().getString(R.string.queue_tab);
                case 1:
                    return mContext.getResources().getString(R.string.enrolled_tab);
                case 2:
                    return mContext.getResources().getString(R.string.workshops_tab);
                default:
                    return null;
            }
        }
    }
}
