package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import java.util.List;

public class EnrolledStudentsAdapter extends
        RecyclerView.Adapter<EnrolledStudentsAdapter.ViewHolder> {

    private Context mContext;
    private List<ParseUser> mEnrolledStudents;

    public EnrolledStudentsAdapter(Context context, List<ParseUser> students) {
        this.mContext = context;
        this.mEnrolledStudents = students;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.
                        from(mContext).
                        inflate(R.layout.item_student_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mEnrolledStudents.get(i));
    }

    @Override
    public int getItemCount() {
        return mEnrolledStudents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvEnrolledStudent;
        private ProfilePictureView ppvProfilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnrolledStudent = itemView.findViewById(R.id.tvEnrolledStudent);
            ppvProfilePic = itemView.findViewById(R.id.ppvProfilePic);
        }

        public void bind(ParseUser student) {
            tvEnrolledStudent.setText(User.getFullName(student));
            ppvProfilePic.setProfileId(User.getProfilePicture(student));
        }
    }
}
