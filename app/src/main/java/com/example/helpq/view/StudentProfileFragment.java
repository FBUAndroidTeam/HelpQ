package com.example.helpq.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

public class StudentProfileFragment extends Fragment {

    public static final String TAG = "StudentProfileFragment";
    private static final String KEY_PROFILE_PIC = "ProfilePic";

    private TextView tvFullName;
    private TextView tvAdmin;
    private TextView tvUsername;
    private Button btnLogout;
    private ProfilePictureView ppvPicture;
    private String mProfile;

    public static StudentProfileFragment newInstance() {
        return new StudentProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        mProfile = User.getProfilePicture(ParseUser.getCurrentUser());
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PROFILE_PIC, mProfile);

    }

    //gets retained instance on configuration change
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            getRetainInstance();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ppvPicture = view.findViewById(R.id.ppvPicture);
        tvAdmin = view.findViewById(R.id.tvAdmin);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvAdmin.setText(User.getAdminName(ParseUser.getCurrentUser()));
        tvFullName.setText(User.getFullName(ParseUser.getCurrentUser()));
        ppvPicture.setProfileId(mProfile);
        setupLogout();
    }

    // Handle logic for logging out.
    private void setupLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }
}
