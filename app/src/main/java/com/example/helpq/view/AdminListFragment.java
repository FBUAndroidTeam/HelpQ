package com.example.helpq.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.controller.AdminListAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminListFragment extends DialogFragment {

    public static final String TAG = "AdminListFragment";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IS_ADMIN = "isAdmin";


    private RecyclerView rvAdmins;
    private List<ParseUser> mAdmins;
    private AdminListAdapter adapter;
    private Button btnSelect;

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
                    DialogDismissListener listener =
                            (DialogDismissListener) getTargetFragment();
                    listener.onDismiss();
                    dismiss();
                }
            }
        });
    }

    public ParseUser getSelectedAdmin() {
        return mAdmins.get(adapter.getSelectedPosition());
    }

    private void findAllAdmins() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(KEY_IS_ADMIN, true);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                mAdmins.addAll(objects);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Admins found");
            }
        });
    }
}
