package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.helpq.R;
import com.parse.ParseUser;

import java.util.List;

public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.ViewHolder> {

    public static final int NOTHING_SELECTED = -1;
    private static final String KEY_FULL_NAME = "fullName";

    private Context mContext;
    private List<ParseUser> mAdmins;
    private int selected;

    public AdminListAdapter(Context context, List<ParseUser> admins) {
        mAdmins = admins;
        mContext = context;
        selected = NOTHING_SELECTED;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AdminListAdapter.ViewHolder(LayoutInflater.from(mContext)
        .inflate(R.layout.item_admin_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mAdmins.get(i));
    }

    @Override
    public int getItemCount() {
        return mAdmins.size();
    }

    public int getSelectedPosition() {
        return selected;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAdmin;
        private RadioButton rbSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdmin = itemView.findViewById(R.id.tvAdmin);
            rbSelect = itemView.findViewById(R.id.rbSelect);
            rbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected = getAdapterPosition();
                }
            });
        }

        public void bind(ParseUser admin) {
            tvAdmin.setText(admin.getString(KEY_FULL_NAME));
        }
    }
}
