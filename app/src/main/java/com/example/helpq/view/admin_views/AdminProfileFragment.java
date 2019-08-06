package com.example.helpq.view.admin_views;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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
import com.example.helpq.view.LoginActivity;
import com.example.helpq.view.MainActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseUser;

public class AdminProfileFragment extends Fragment {

    public static final String TAG = "AdminProfileFragment";

    private SimpleDraweeView ppvPicture;
    private TextView tvFullName;
    private TextView tvUsername;
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
        tvFullName.setText(User.getFullName(ParseUser.getCurrentUser()));
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        setupLogout();
        ppvPicture.setImageURI(Uri.parse("http://graph.facebook.com/"+
                User.getProfilePicture(ParseUser.getCurrentUser())+"/picture?type=large"));
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
