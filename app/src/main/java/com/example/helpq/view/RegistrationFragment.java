package com.example.helpq.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RegistrationFragment extends Fragment implements DialogDismissListener {

    public static final String TAG = "RegistrationFragment";
    private static final String KEY_FULL_NAME = "fullName";


    private EditText etNewUsername;
    private TextView tvAdmin;
    private Button btnRegister;
    private FragmentManager fragmentManager;
    private AdminListFragment adminListFragment;
    private String adminUsername;
    private String fullName;
    private String profilePicId;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFacebookInformation();
        tvAdmin = view.findViewById(R.id.tvAdmin);
        etNewUsername = view.findViewById(R.id.etNewUsername);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnRegister.setEnabled(false);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validRegistrationCheck();
            }
        });
        tvAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getFragmentManager();
                adminListFragment = AdminListFragment.newInstance("adminListFragment");
                adminListFragment.setTargetFragment(RegistrationFragment.this,
                        300);
                adminListFragment.show(fragmentManager, AdminListFragment.TAG);
            }
        });
    }

    //checks to see if information user inputted is valid
    private void validRegistrationCheck() {
        final String username = etNewUsername.getText().toString();
        if (username.isEmpty()) {
            Toast.makeText(getContext(), R.string.edge_case_empty_username, Toast.LENGTH_LONG).show();
            return;
        } else {
            queryUsernameExists(username);
        }
    }

    //completes registration process if new user from facebook login
    private void completeRegister(ParseUser newUser, final String username) {
        newUser.setUsername(username);
        User.setFullName(fullName, newUser);
        User.setIsAdmin(false, newUser);
        User.setAdminName(adminUsername, newUser);
        User.setProfilePicture(profilePicId, newUser);
        newUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getContext(), R.string.registration_complete,
                        Toast.LENGTH_LONG).show();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(RegistrationFragment.this)
                        .commit();
            }
        });
    }

    //checks to see if username is taken already or not
    private void queryUsernameExists(final String username) {
        ParseQuery<ParseUser> query = QueryFactory.UserQuery.getUserByUsername(username);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e != null) {
                    Log.d(TAG, "error querying for username");
                    e.printStackTrace();
                } else if (objects.size() != 0){
                    Toast.makeText(getContext(), R.string.username_taken, Toast.LENGTH_LONG).show();
                } else {
                    completeRegister(ParseUser.getCurrentUser(), username);
                }
            }
        });
    }

    @Override
    public void onDismiss() {
        adminUsername = adminListFragment.getSelectedAdmin().getUsername();
        tvAdmin.setText("Admin: " + adminListFragment.getSelectedAdmin().getString(KEY_FULL_NAME));
        btnRegister.setEnabled(true);
    }

    //gets user's full name and profile picture from Facebook
    private void getFacebookInformation() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            fullName  = String.valueOf(object.getString("name"));
                            profilePicId  = String.valueOf(object.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        request.executeAsync();
    }
}
