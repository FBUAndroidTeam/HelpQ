package com.example.helpq.view;

import android.content.Intent;
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

public class AdminProfileFragment extends Fragment {

    public static final String TAG = "AdminProfileFragment";

    private ProfilePictureView ppvPicture;
    private TextView tvFullName;
    private  TextView tvUsername;
    private Button btnLogout;

    public static AdminProfileFragment newInstance() {
        return new AdminProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        getFacebookPicture();
    }

    // Handle logic for logging out.
    private void setupLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                ((MainActivity) getActivity()).finish();
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
