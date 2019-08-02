package com.example.helpq.view.admin_views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;
import com.example.helpq.view.QueueFragment;

public class AdminQueueFragment extends Fragment {

    public static final String TAG = "AdminQueueFragment";

    public static AdminQueueFragment newInstance() {
        return new AdminQueueFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_queue, parent, false);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.flAdminQueueContainer, new QueueFragment(), QueueFragment.TAG)
                .commit();

        return view;
    }
}
