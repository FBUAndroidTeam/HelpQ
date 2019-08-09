package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.NotificationHelper;
import com.example.helpq.model.Sound;
import com.example.helpq.model.Workshop;
import com.example.helpq.view.admin_views.AdminWorkshopFragment;
import com.parse.ParseException;

import java.util.List;

public class AdminWorkshopAdapter extends RecyclerView.Adapter<AdminWorkshopAdapter.ViewHolder> {

    private Context mContext;
    private List<Workshop> mWorkshops;
    private AdminWorkshopFragment mAdminWorkshopFragment;


    public AdminWorkshopAdapter(Context context, List<Workshop> workshops, Fragment frag) {
        mContext = context;
        mWorkshops = workshops;
        mAdminWorkshopFragment = (AdminWorkshopFragment) frag;
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

    // Clean all elements of the recycler
    public void clear() {
        mWorkshops.clear();
        notifyDataSetChanged();
    }

    // Deletes this workshop from parse
    public void deleteWorkshop(Workshop w) {
        String workshopId = w.getObjectId();
        try {
            w.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        NotificationHelper.deleteNotificationsByWorkshop(workshopId);
        w.saveInBackground();
    }

    // Removes question at this position
    public void removeAt(int position) {
        mWorkshops.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mWorkshops.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvMonth;
        private TextView tvLocation;
        private TextView tvAttendees;
        private ImageButton ibDelete;
        private TextView tvDate;
        private TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAttendees = itemView.findViewById(R.id.tvAttendees);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvMonth= itemView.findViewById(R.id.tvMonth);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            ibDelete = itemView.findViewById(R.id.ibAdminDeleteWorkshop);
            ibDelete.setEnabled(true);
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sound.delete(mContext);
                    mAdminWorkshopFragment.createSnackbar(getLayoutPosition(),
                            mWorkshops.get(getAdapterPosition()));
                    removeAt(getAdapterPosition());
                    ibDelete.setEnabled(false);
                }
            });
        }

        public void bind(Workshop workshop) {
            ibDelete.setEnabled(true);
            String wholeDate = workshop.getDate();
            tvTitle.setText(workshop.getTitle());
            tvMonth.setText(wholeDate.substring(0,3));
            tvDate.setText(wholeDate.substring(3));
            tvTime.setText(workshop.getTime());
            tvLocation.setText( workshop.getLocation());
            int attendees = workshop.getAttendees().length();
            if (attendees == 1) {
                tvAttendees.setText(attendees + " " +
                        mContext.getResources().getString(R.string.attendee));
            } else {
                tvAttendees.setText(attendees + " " +
                        mContext.getResources().getString(R.string.attendees));
            }
        }
    }
}