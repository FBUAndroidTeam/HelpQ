package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminWorkshopAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
