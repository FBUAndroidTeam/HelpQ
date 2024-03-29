package com.example.helpq.view.admin_views;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.controller.AdminListAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Sound;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminListFragment extends DialogFragment {

    public static final String TAG = "AdminListFragment";
    private static final String KEY_TITLE = "title";

    private RecyclerView rvAdmins;
    private List<ParseUser> mAdmins;
    private AdminListAdapter adapter;
    private Button btnSelect;
    private ProgressBar pbLoading;
    private ImageButton ibCancel;

    public static AdminListFragment newInstance(String title) {
        AdminListFragment frag = new AdminListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibCancel = view.findViewById(R.id.ibCancel);
        setCancelButton();
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        btnSelect = view.findViewById(R.id.btnSelect);
        rvAdmins = view.findViewById(R.id.rvAdmins);
        mAdmins = new ArrayList<>();
        adapter = new AdminListAdapter(getContext(), mAdmins);
        rvAdmins.setAdapter(adapter);
        rvAdmins.setLayoutManager(new LinearLayoutManager(getContext()));
        findAllAdmins();

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelectedPosition() == adapter.NOTHING_SELECTED) {
                    Toast.makeText(getContext(), R.string.select_admin,
                            Toast.LENGTH_LONG).show();
                } else {
                    Sound.actionDone(getContext());
                    DialogDismissListener listener =
                            (DialogDismissListener) getTargetFragment();
                    listener.onDismiss();
                    dismiss();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FragmentManager m = getFragmentManager();
        FragmentTransaction transaction = m.beginTransaction();
        AdminListFragment frag =
                AdminListFragment.newInstance(TAG);
        transaction.detach(this).attach(this).show(this);
        frag.show(transaction, TAG);
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

    public ParseUser getSelectedAdmin() {
        return mAdmins.get(adapter.getSelectedPosition());
    }

    private void findAllAdmins() {
        ParseQuery<ParseUser> query = QueryFactory.Users.getAllAdmins();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                mAdmins.addAll(objects);
                adapter.notifyDataSetChanged();
                pbLoading.setVisibility(View.INVISIBLE);
                Log.d(TAG, "Admins found");
            }
        });
    }
}
