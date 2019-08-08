package com.example.helpq.view.admin_views;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.User;
import com.example.helpq.model.WaitTime;
import com.example.helpq.view.LoginActivity;
import com.example.helpq.view.MainActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class AdminProfileFragment extends Fragment {

    public static final String TAG = "AdminProfileFragment";

    private SimpleDraweeView ppvPicture;
    private TextView tvFullName;
    private TextView tvUsername;
    private TextView tvAnsweredQuestions;
    private Button btnLogout;

    public static AdminProfileFragment newInstance() {
        return new AdminProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFullName = view.findViewById(R.id.tvFullName);
        btnLogout = view.findViewById(R.id.btnLogout);
        ppvPicture = view.findViewById(R.id.ppvPicture);
        tvAnsweredQuestions = view.findViewById(R.id.tvAnsweredQuestions);
        tvFullName.setText(User.getFullName(ParseUser.getCurrentUser()));
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        setupLogout();
        ppvPicture.setImageURI(Uri.parse("http://graph.facebook.com/"+
                User.getProfilePicture(ParseUser.getCurrentUser())+"/picture?type=large"));

        setupStatsText();
    }

    private void setupStatsText() {
        ParseQuery<WaitTime> query = QueryFactory.WaitTimes.getAdminWaitTimes();
        query.findInBackground(new FindCallback<WaitTime>() {
            @Override
            public void done(List<WaitTime> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying for admin wait time!");
                    return;
                }
                if (objects.size() > 1) {
                    Log.e(TAG, "Every admin should only have one WaitTime dataset!");
                    return;
                }
                WaitTime waitTime;
                if (objects.size() == 0) {
                    waitTime = new WaitTime();
                    waitTime.setAdminName(User.getAdminName(ParseUser.getCurrentUser()));
                    waitTime.saveInBackground();
                } else {
                    waitTime = objects.get(0);
                }
                long totalAnswered = waitTime.getBlockingSize() + waitTime.getStretchSize() +
                        waitTime.getCuriositySize();
                tvAnsweredQuestions.setText(String.format("%d answered questions", totalAnswered));
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    // Handle logic for logging out.
    private void setupLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogout.setVisibility(View.INVISIBLE);
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                ((MainActivity) getActivity()).finish();
            }
        });
    }
}
