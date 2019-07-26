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
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentProfileFragment extends Fragment {

    public static final String TAG = "StudentProfileFragment";

    private TextView tvFullName;
    private TextView tvAdmin;
    private TextView tvUsername;
    private Button btnLogout;
    private ProfilePictureView ppvPicture;

    public static StudentProfileFragment newInstance() {
        return new StudentProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    //reloads landscape/portrait version of layout faster on config change
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
        ppvPicture = view.findViewById(R.id.ppvPicture);
        tvAdmin = view.findViewById(R.id.tvAdmin);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvAdmin.setText(User.getAdminName(ParseUser.getCurrentUser()));
        tvFullName.setText(User.getFullName(ParseUser.getCurrentUser()));
        setupLogout();
        getFacebookPicture();
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

    private void getFacebookPicture() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id  = String.valueOf(object.getString("id"));
                    ppvPicture.setProfileId(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        request.executeAsync();
    }


}
