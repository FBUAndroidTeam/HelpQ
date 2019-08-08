package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Sound;
import com.example.helpq.model.Workshop;
import com.example.helpq.view.student_views.StudentWorkshopFragment;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class StudentWorkshopAdapter extends
        RecyclerView.Adapter<StudentWorkshopAdapter.ViewHolder> {

    public static final String TAG = "StudentWorkshopAdapter";
    private Context mContext;
    private List<Workshop> mWorkshops;
    private StudentWorkshopFragment mStudentWorkshopFragment;

    public StudentWorkshopAdapter(Context context, List<Workshop> workshops,
                                  StudentWorkshopFragment fragment) {
        this.mContext = context;
        this.mWorkshops = workshops;
        this.mStudentWorkshopFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.item_student_workshop, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvWorkshopName;
        private TextView tvWorkshopLocation;
        private TextView tvWorkshopAttendanceCount;
        private Switch swSignUp;
        private ImageView ivMarker;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvMonth;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkshopName = itemView.findViewById(R.id.tvStudentWorkshopName);
            tvWorkshopLocation = itemView.findViewById(R.id.tvStudentWorkshopLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvWorkshopAttendanceCount = itemView.findViewById(R.id.tvStudentWorkshopSignedUpCount);
            swSignUp = itemView.findViewById(R.id.swSignUp);
            ivMarker = itemView.findViewById(R.id.ivMarker);
        }

        private boolean setButtonText(final Workshop workshop) {
            if(isSignedUp(workshop)) {
                swSignUp.setText(R.string.signed_up);
                return true;
            } else {
                swSignUp.setText(R.string.sign_up);
                return false;
            }
        }

        private boolean isSignedUp(final Workshop workshop) {
            //checks to see if user is signed up, will set button text accordingly
            JSONArray attendeesArr = workshop.getAttendees();
            String userID = ParseUser.getCurrentUser().getObjectId();
            for(int i = 0; i < attendeesArr.length(); i++) {
                try {
                    if(userID.equals(attendeesArr.getJSONObject(i).getString("objectId"))) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        private void setAttendeeText(Workshop workshop) {
            final JSONArray attendees = workshop.getAttendees();
            if(attendees.length() == 1) {
                tvWorkshopAttendanceCount.setText(attendees.length() + " " +
                        mContext.getResources().getString(R.string.attendee));
            } else {
                tvWorkshopAttendanceCount.setText(attendees.length() + " " +
                        mContext.getResources().getString(R.string.attendees));
            }
        }

        private void setSwitchStatus(final Workshop workshop) {
            if(setButtonText(workshop)) {
                swSignUp.setChecked(true);
            } else {
                swSignUp.setChecked(false);
            }
        }

        public void bind(final Workshop workshop) {
            String startDate = workshop.getDate();
            tvWorkshopName.setText(workshop.getTitle());
            tvMonth.setText(startDate.substring(0,3));
            tvDate.setText(startDate.substring(3));
            tvTime.setText(workshop.getTime());
            tvWorkshopLocation.setText(workshop.getLocation());
            setSwitchStatus(workshop);
            setAttendeeText(workshop);
            markNewWorkshop(workshop.getObjectId());
            setupSignUpSwitch(workshop);
        }

        private void setupSignUpSwitch(final Workshop workshop) {
            swSignUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ivMarker.setVisibility(View.GONE);
                    if(!isChecked) {
                        Sound.actionUndo(mContext);
                        workshop.unsignUp(ParseUser.getCurrentUser());
                        workshop.saveInBackground();
                        setButtonText(workshop);
                        setAttendeeText(workshop);
                    } else {
                        Sound.actionDone(mContext);
                        workshop.setAttendee(ParseUser.getCurrentUser());
                        Date time = workshop.getStartTime();
                        mStudentWorkshopFragment.onTimeSet(time);
                        workshop.saveInBackground();
                        setButtonText(workshop);
                        setAttendeeText(workshop);
                    }
                }
            });
        }

        // Place a marker on this workshop if a notification points to it.
        // Delete the notification.
        private void markNewWorkshop(String workshopId) {
            Set<String> notifications = mStudentWorkshopFragment.mNotifications;
            if (notifications.contains(workshopId)) {
                ivMarker.setVisibility(View.VISIBLE);
            }
            else {
                ivMarker.setVisibility(View.GONE);
            }
        }
    }
}

