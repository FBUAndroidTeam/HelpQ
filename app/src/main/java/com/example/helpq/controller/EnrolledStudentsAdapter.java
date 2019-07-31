package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.User;
import com.example.helpq.view.AdminEnrolledFragment;
import com.example.helpq.view.AdminIndividualQuestionsFragment;
import com.example.helpq.view.ReplyQuestionFragment;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseUser;

import java.util.List;

public class EnrolledStudentsAdapter extends
        RecyclerView.Adapter<EnrolledStudentsAdapter.ViewHolder> {

    private Context mContext;
    private List<ParseUser> mEnrolledStudents;
    private AdminEnrolledFragment mAdminEnrolledFragment;

    public EnrolledStudentsAdapter(Context context, List<ParseUser> students,
                                   AdminEnrolledFragment fragment) {
        this.mContext = context;
        this.mEnrolledStudents = students;
        this.mAdminEnrolledFragment = fragment;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminIndividualQuestionsFragment fragment = AdminIndividualQuestionsFragment
                            .newInstance(mEnrolledStudents.get(getAdapterPosition()));
                    fragment.setTargetFragment(mAdminEnrolledFragment, 300);
                    FragmentManager manager = mAdminEnrolledFragment.getFragmentManager();
                    fragment.show(manager, AdminIndividualQuestionsFragment.TAG);
                }
            });
        }

        public void bind(ParseUser student) {
            tvEnrolledStudent.setText(User.getFullName(student));
            ppvProfilePic.setProfileId(User.getProfilePicture(student));
        }
    }
}
