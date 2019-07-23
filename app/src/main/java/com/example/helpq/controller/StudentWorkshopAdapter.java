package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Workshop;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.List;

public class StudentWorkshopAdapter extends
        RecyclerView.Adapter<StudentWorkshopAdapter.ViewHolder> {

    public static final String TAG = "StudentWorkshopAdapter";
    private Context mContext;
    private List<Workshop> mWorkshops;

    public StudentWorkshopAdapter(Context context, List<Workshop> workshops) {
        this.mContext = context;
        this.mWorkshops = workshops;
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
        private Button btnSignUp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkshopName = itemView.findViewById(R.id.tvStudentWorkshopName);
            tvWorkshopDate = itemView.findViewById(R.id.tvStudentWorkshopDate);
            tvWorkshopLocation = itemView.findViewById(R.id.tvStudentWorkshopLocation);
            tvWorkshopAttendanceCount = itemView.findViewById(R.id.tvStudentWorkshopSignedUpCount);
            btnSignUp = itemView.findViewById(R.id.btnSignUp);
        }

        private boolean buttonText(final Workshop workshop) {
            //checks to see if user is signed up, will set button text accordingly
            JSONArray attendeesArr = workshop.getAttendees();
            Boolean isSignedUp = false;
            for(int i = 0; i < attendeesArr.length(); i++) {
                try {
                    if((ParseUser.getCurrentUser().getObjectId()).
                            equals(attendeesArr.getJSONObject(i).getString("objectId"))) {
                        isSignedUp = true;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(isSignedUp) {
                btnSignUp.setText(R.string.signed_up);
            } else {
                btnSignUp.setText(R.string.sign_up);
            }
            return isSignedUp;
        }

        private void setAttendeeText(Workshop workshop) {
            final JSONArray attendees = workshop.getAttendees();
            if(attendees.length() == 1) {
                tvWorkshopAttendanceCount.setText(attendees.length() + R.string.attendee);
            } else {
                tvWorkshopAttendanceCount.setText(attendees.length() + R.string.attendees);
            }
        }

        public void bind(final Workshop workshop) {
            tvWorkshopName.setText(workshop.getTitle());
            tvWorkshopDate.setText(workshop.getDate());
            tvWorkshopLocation.setText(workshop.getLocation());
            setAttendeeText(workshop);


            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (buttonText(workshop)) {

                        workshop.unsignUp(ParseUser.getCurrentUser());
                        workshop.saveInBackground();
                        buttonText(workshop);
                        setAttendeeText(workshop);
                        Toast.makeText(mContext,
                                R.string.unenrolled,
                                Toast.LENGTH_LONG).show();
                    } else {
                        workshop.setAttendee(ParseUser.getCurrentUser());
                        workshop.saveInBackground();
                        buttonText(workshop);
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
