package com.example.helpq.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;

public class AdminQueueFragment extends Fragment {

    public static final String TAG = "AdminQueueFragment";
    private FragmentManager fragmentManager;

    public static AdminQueueFragment newInstance() {
        return new AdminQueueFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_queue, parent, false);

        fragmentManager = getFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.flAdminQueueContainer, new QueueFragment(), QueueFragment.TAG)
                .commit();

        return view;
    }
}
