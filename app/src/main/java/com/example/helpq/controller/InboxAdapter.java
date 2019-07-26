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
import com.example.helpq.model.User;
import com.parse.ParseUser;

import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private static final String TAG = "InboxAdapter";
    private Context mContext;
    private List<Question> mMessages;

    // Constructor
    public InboxAdapter(Context context, List<Question> mMessages) {
        this.mContext = context;
        this.mMessages = mMessages;
    }

    @NonNull
    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new InboxAdapter.ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_message, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(mMessages.get(i));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mMessages.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Question> list) {
        mMessages.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Layout fields of item_message
        private TextView tvQuestion;
        private TextView tvPriorityEmoji;
        private TextView tvAnswerTime;
        private TextView tvAdminName;
        private TextView tvAnswer;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvAnswerTime = itemView.findViewById(R.id.tvAnswerTime);
            tvAdminName = itemView.findViewById(R.id.tvAdminName);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
        }

        // Bind the view elements to the message.
        public void bind(Question message) {
            tvPriorityEmoji.setText(message.getPriority());
            tvQuestion.setText(message.getText());
            tvAnswerTime.setText(message.getAnsweredTimeAgo());
            if (User.isAdmin(ParseUser.getCurrentUser())) {
                tvAdminName.setText(mContext.getResources().getString(R.string.your_answer));
            } else {
                tvAdminName.setText(User.getAdminName(ParseUser.getCurrentUser()) +
                        mContext.getResources().getString(R.string.admin_answer));
            }
            tvAnswer.setText(message.getAnswer());
        }
    }
}
