package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.helpq.R;
import com.example.helpq.controller.RemindTimesAdapter;
import com.example.helpq.model.Sound;

public class SettingsFragment extends DialogFragment {

    public static final String TAG = "SettingsFragment";

    private RecyclerView rvTimes;
    private RemindTimesAdapter adapter;
    private int[] mTimes;
    private ImageButton ibCancel;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibCancel = view.findViewById(R.id.ibCancel);
        mTimes = new int[]{5, 10, 15};
        rvTimes = view.findViewById(R.id.rvTimes);
        adapter = new RemindTimesAdapter(getContext(), mTimes);
        rvTimes.setAdapter(adapter);
        rvTimes.setLayoutManager(new LinearLayoutManager(getContext()));
        setCancelButton();
    }

    private void setCancelButton() {
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sound.closeDialogWindow(getContext());
                dismiss();
            }
        });
    }
}
