package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;

import java.util.List;

public class AdminIndividualQuestionsAdapter extends
        RecyclerView.Adapter<AdminIndividualQuestionsAdapter.ViewHolder> {

    private List<Question> mQuestions;
    private Context mContext;

    public AdminIndividualQuestionsAdapter(Context context, List<Question> questions) {
        this.mContext = context;
        this.mQuestions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_question, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mQuestions.get(i));
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvStudentName;
        private TextView tvPriorityEmoji;
        private TextView tvHelpEmoji;
        private TextView tvDescription;
        private TextView tvStartTime;
        private TextView tvLikes;
        private TextView tvSeeMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvHelpEmoji = itemView.findViewById(R.id.tvHelpEmoji);
            tvDescription = itemView.findViewById(R.id.tvQuestion);
            tvStartTime = itemView.findViewById(R.id.tvAnswerTime);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
        }

        public void bind(Question q) {
            if(getItemCount() == 0) {
                Toast.makeText(mContext, R.string.no_questions_student, Toast.LENGTH_LONG).show();
            } else {
                tvStudentName.setVisibility(tvStudentName.INVISIBLE);
                tvPriorityEmoji.setText(q.getPriority());
                tvHelpEmoji.setText(q.getHelpType());
                tvDescription.setText(q.getText());
                tvStartTime.setText(q.getCreatedTimeAgo());
                tvLikes.setText(Integer.toString(q.getLikeCount()) + " " +
                        mContext.getResources().getString(R.string.likes));
                tvSeeMore.setVisibility(tvSeeMore.INVISIBLE);
            }
        }
    }
}
