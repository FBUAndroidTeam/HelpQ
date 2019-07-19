package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Workshop;

import java.util.List;

public class AdminWorkshopAdapter extends RecyclerView.Adapter<AdminWorkshopAdapter.ViewHolder> {

    private Context mContext;
    private List<Workshop> mWorkshops;

    public AdminWorkshopAdapter(Context context, List<Workshop> workshops) {
        mContext = context;
        mWorkshops = workshops;
    }

    @NonNull
    @Override
    public AdminWorkshopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AdminWorkshopAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_admin_workshop, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminWorkshopAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(mWorkshops.get(i));
    }

    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvStartTime;
        private TextView tvLocation;
        private TextView tvAttendees;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAttendees = itemView.findViewById(R.id.tvAttendees);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void bind(Workshop workshop) {
            tvTitle.setText(workshop.getTitle());
            tvStartTime.setText("When: " + workshop.getDate());
            tvLocation.setText("Where: " + workshop.getLocation());
            int attendees = workshop.getAttendees().length();
            if (attendees == 1) {
                tvAttendees.setText(attendees + " attendee");
            } else {
                tvAttendees.setText(attendees + " attendees");
            }
        }
    }
}
