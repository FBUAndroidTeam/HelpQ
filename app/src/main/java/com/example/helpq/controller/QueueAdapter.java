package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Question;

import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private static final String TAG = "QueueAdapter";
    private Context context;
    private List<Question> mQuestions;

    // Constructor
    public QueueAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.mQuestions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Question question = mQuestions.get(i);
        viewHolder.bind(question);
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Layout fields of item_question
        private TextView tvStudentName;
        private TextView tvPriorityEmoji;
        private TextView tvDescription;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        // Bind the view elements to the Question.
        public void bind(Question question) {

            String name = null;
            try {
                name = question.getAsker().fetchIfNeeded().getUsername();
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            tvStudentName.setText(name);
            tvPriorityEmoji.setText(question.getPriority());
            tvDescription.setText(question.getText());
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mQuestions.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Question> list) {
        mQuestions.addAll(list);
        notifyDataSetChanged();
    }
}
