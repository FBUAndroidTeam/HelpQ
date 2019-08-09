package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.parse.ParseUser;

public class RemindTimesAdapter extends RecyclerView.Adapter<RemindTimesAdapter.ViewHolder> {

    private Context mContext;
    private int[] mTimes;
    private ImageView lastSelected;

    public RemindTimesAdapter(Context context, int[] times) {
        mContext = context;
        mTimes = times;
    }


    @NonNull
    @Override
    public RemindTimesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RemindTimesAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_remind_times, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RemindTimesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(mTimes[i]);
    }

    @Override
    public int getItemCount() {
        return mTimes.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView ivSelected;
        private TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSelected = itemView.findViewById(R.id.ivSelected);
            tvTime = itemView.findViewById(R.id.tvTime);
            itemView.setOnClickListener(this);
        }

        public void bind(int time) {
            tvTime.setText(time + " minutes");
            if(((Integer) User.getReminderTime(ParseUser.getCurrentUser())).intValue() == time) {
                ivSelected.setVisibility(View.VISIBLE);
                lastSelected = ivSelected;
            } else {
                ivSelected.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if(ivSelected.getVisibility() != View.VISIBLE) {
                Sound.actionDone(mContext);
                lastSelected.setVisibility(View.INVISIBLE);
                ivSelected.setVisibility(View.VISIBLE);
                lastSelected = ivSelected;
                User.setReminderTime(mTimes[getAdapterPosition()], ParseUser.getCurrentUser());
                ParseUser.getCurrentUser().saveInBackground();
            }
        }
    }
}
