package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Question;

import java.util.List;

public class AdminIndividualQuestionsAdapter extends
        RecyclerView.Adapter<AdminIndividualQuestionsAdapter.ViewHolder> {

    private List<Question> mQuestions;
    private Context mContext;

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
        private ImageButton ibView;
        private TextView tvSeeMore;
        private String questionText;
        private int originalLines;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvHelpEmoji = itemView.findViewById(R.id.tvHelpEmoji);
            tvDescription = itemView.findViewById(R.id.tvQuestion);
            tvStartTime = itemView.findViewById(R.id.tvAnswerTime);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
            ibView = itemView.findViewById(R.id.ibView);
        }

        public void bind(Question q) {

        }
    }
}
