package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.InboxAdapter;
import com.example.helpq.model.Question;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {

    public static final String TAG = "InboxFragment";
    private RecyclerView rvMessages;
    protected List<Question> mMessages;
    protected InboxAdapter mAdapter;
    protected TextView tvNotice;

    public static InboxFragment newInstance() {
        return new InboxFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);

        // Create data source, adapter, and layout manager
        mMessages = new ArrayList<>();
        mAdapter = new InboxAdapter(getContext(), mMessages);
        rvMessages = view.findViewById(R.id.rvMessages);
        rvMessages.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(layoutManager);
        queryMessages();
    }

    // Query for messages intended for the current user
    protected void queryMessages() {
    }
}
