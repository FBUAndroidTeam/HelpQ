package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Workshop;
import com.example.helpq.view.student_views.StudentWorkshopFragment;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;
import java.util.List;

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
        private TextView tvWorkshopDate;
        private TextView tvWorkshopLocation;
        private TextView tvWorkshopAttendanceCount;
        private Switch swSignUp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkshopName = itemView.findViewById(R.id.tvStudentWorkshopName);
            tvWorkshopDate = itemView.findViewById(R.id.tvStudentWorkshopDate);
            tvWorkshopLocation = itemView.findViewById(R.id.tvStudentWorkshopLocation);
            tvWorkshopAttendanceCount = itemView.findViewById(R.id.tvStudentWorkshopSignedUpCount);
            swSignUp = itemView.findViewById(R.id.swSignUp);
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
            tvWorkshopName.setText(workshop.getTitle());
            tvWorkshopDate.setText(workshop.getDate());
            tvWorkshopLocation.setText(workshop.getLocation());
            setSwitchStatus(workshop);
            setAttendeeText(workshop);

            swSignUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked) {
                        workshop.unsignUp(ParseUser.getCurrentUser());
                        workshop.saveInBackground();
                        setButtonText(workshop);
                        setAttendeeText(workshop);
                        Toast.makeText(mContext,
                                R.string.unenrolled,
                                Toast.LENGTH_LONG).show();
                    } else {
                        workshop.setAttendee(ParseUser.getCurrentUser());
                        Date time = workshop.getStartTime();
                        mStudentWorkshopFragment.onTimeSet(time);
                        workshop.saveInBackground();
                        setButtonText(workshop);
                        setAttendeeText(workshop);
                        Toast.makeText(mContext,
                                R.string.enrolled,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}

