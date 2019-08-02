package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Workshop;
import com.example.helpq.view.admin_views.AdminWorkshopFragment;
import com.parse.ParseException;

import java.util.List;

public class AdminWorkshopAdapter extends RecyclerView.Adapter<AdminWorkshopAdapter.ViewHolder> {

    private Context mContext;
    private List<Workshop> mWorkshops;
    private AdminWorkshopFragment mAdminWorkshopFragment;
    private static ClickListener mClickListener;


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

    // Clean all elements of the recycler
    public void clear() {
        mWorkshops.clear();
        notifyDataSetChanged();
    }

    // Deletes this workshop from parse
    public void deleteWorkshop(Workshop w) {
        try {
            w.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        w.saveInBackground();
    }

    // Removes question at this position
    public void removeAt(int position) {
        mWorkshops.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mWorkshops.size());
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        AdminWorkshopAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements
            View.OnClickListener, View.OnLongClickListener {
        private TextView tvTitle;
        private TextView tvStartTime;
        private TextView tvLocation;
        private TextView tvAttendees;
        private View vAdminWorkshopView;
        private ImageButton ibDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            tvAttendees = itemView.findViewById(R.id.tvAttendees);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStartTime = itemView.findViewById(R.id.tvAnswerTime);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            vAdminWorkshopView = itemView.findViewById(R.id.clWorkshop);
            ibDelete = itemView.findViewById(R.id.ibAdminDeleteWorkshop);
        }

        public void bind(Workshop workshop) {
            tvTitle.setText(workshop.getTitle());
            tvStartTime.setText(mContext.getResources().getString(R.string.workshop_when) +
                    " " + workshop.getDate());
            tvLocation.setText(mContext.getResources().getString(R.string.workshop_where) +
                    " " + workshop.getLocation());
            int attendees = workshop.getAttendees().length();
            if (attendees == 1) {
                tvAttendees.setText(attendees + " " +
                        mContext.getResources().getString(R.string.attendee));
            } else {
                tvAttendees.setText(attendees + " " +
                        mContext.getResources().getString(R.string.attendees));
            }
        }

        @Override
        public void onClick(View v) {
            TranslateAnimation animate = new TranslateAnimation(
                    itemView.getX(),
                    0,
                    0,
                    0
            );
            animate.setDuration(300);
            animate.setFillAfter(true);
            vAdminWorkshopView.startAnimation(animate);
            ibDelete.setVisibility(ibDelete.GONE);
        }


        @Override
        public boolean onLongClick(View v) {
            mClickListener.onItemLongClick(getAdapterPosition(), v);
            TranslateAnimation animate = new TranslateAnimation(
                    v.getX(),
                    -150,
                    0,
                    0
            );
            animate.setDuration(300);
            animate.setFillAfter(true);
            vAdminWorkshopView.startAnimation(animate);
            ibDelete.setVisibility(ibDelete.VISIBLE);
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWorkshop(mWorkshops.get(getAdapterPosition()));
                    removeAt(getAdapterPosition());
                }
            });
            return true;
        }
    }
}
