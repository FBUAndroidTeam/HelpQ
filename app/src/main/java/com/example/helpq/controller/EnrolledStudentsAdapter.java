package com.example.helpq.controller;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;

import java.util.List;

public class EnrolledStudentsAdapter extends
        RecyclerView.Adapter<EnrolledStudentsAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mEnrolledStudents;

    public EnrolledStudentsAdapter(Context context, List<String> students) {
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEnrolledStudent = itemView.findViewById(R.id.tvEnrolledStudent);
        }

        public void bind(String student) {
            tvEnrolledStudent.setText(student);
        }
    }
}
