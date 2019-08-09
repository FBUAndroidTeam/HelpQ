package com.example.helpq.view.student_views;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Reply;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.example.helpq.view.LoginActivity;
import com.example.helpq.view.SettingsFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class StudentProfileFragment extends Fragment {

    public static final String TAG = "StudentProfileFragment";
    private static final String KEY_PROFILE_PIC = "ProfilePic";

    private TextView tvFullName;
    private TextView tvAdmin;
    private TextView tvUsername;
    private Button btnLogout;
    private SimpleDraweeView ppvPicture;
    private String mProfile;
    private TextView tvStat;
    private ImageButton ibSetting;

    public static StudentProfileFragment newInstance() {
        return new StudentProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        mProfile = User.getProfilePicture(ParseUser.getCurrentUser());
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    //gets retained instance on configuration change
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setClickable(false);
        getNumberOfVerifiedReplies();
        ibSetting = view.findViewById(R.id.ibSetting);
        ppvPicture = view.findViewById(R.id.ppvPicture);
        tvAdmin = view.findViewById(R.id.tvAdmin);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvStat = view.findViewById(R.id.tvStat);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvAdmin.setText(User.getAdminName(ParseUser.getCurrentUser()));
        tvFullName.setText(User.getFullName(ParseUser.getCurrentUser()));
        ppvPicture.setImageURI(Uri.parse("http://graph.facebook.com/"+
                mProfile+"/picture?type=large"));
        setupLogout();
        setUpSettingsButton();
    }

    // Handle logic for logging out.
    private void setupLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogout.setVisibility(View.INVISIBLE);
                ParseUser.logOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }

    private void getNumberOfVerifiedReplies() {
        QueryFactory.Replies.getVerifiedReplies().findInBackground(new FindCallback<Reply>() {
            @Override
            public void done(List<Reply> objects, ParseException e) {
                int numRepliesVerified = 0;
                for (Reply reply : objects) {
                    if (reply.getUser().getObjectId()
                            .equals(ParseUser.getCurrentUser().getObjectId())) {
                        numRepliesVerified++;
                    }

                }
                if (numRepliesVerified == 1) {
                    tvStat.setText(numRepliesVerified + " " +
                            getContext().getResources().getString(R.string.verified_reply));
                } else {
                    tvStat.setText(numRepliesVerified + " " +
                            getContext().getResources().getString(R.string.verified_replies));
                }
                btnLogout.setClickable(true);
            }
        });
    }

    private void setUpSettingsButton() {
        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sound.openDialogWindow(getContext());
                FragmentManager manager = getChildFragmentManager();
                SettingsFragment frag = SettingsFragment.newInstance();
                frag.show(manager, SettingsFragment.TAG);
            }
        });
    }
}
