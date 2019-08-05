package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.view.admin_views.AdminIndividualQuestionsFragment;
import com.example.helpq.view.ReplyQuestionFragment;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class AdminIndividualQuestionsAdapter extends
        RecyclerView.Adapter<AdminIndividualQuestionsAdapter.ViewHolder> {

    private String TAG = "AdminIndividualQuestionsAdapter";
    private List<Question> mQuestions;
    private AdminIndividualQuestionsFragment mAdminQuestionsFragment;
    private Context mContext;

    public AdminIndividualQuestionsAdapter(Context context, List<Question> questions,
                                           AdminIndividualQuestionsFragment fragment) {
        this.mContext = context;
        this.mQuestions = questions;
        this.mAdminQuestionsFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_question, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        try {
            viewHolder.bind(mQuestions.get(i));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvStudentName;
        private TextView tvPriorityEmoji;
        private TextView tvHelpEmoji;
        private TextView tvDescription;
        private TextView tvStartTime;
        private TextView tvLikes;
        private TextView tvSeeMore;
        private TextView tvWaitTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvHelpEmoji = itemView.findViewById(R.id.tvHelpEmoji);
            tvDescription = itemView.findViewById(R.id.tvQuestion);
            tvStartTime = itemView.findViewById(R.id.tvAnswerTime);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
            tvWaitTime = itemView.findViewById(R.id.tvWaitTime);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d(TAG, "clicked: " + position);
            if(mQuestions.get(position).getHelpType().equals("in-person")) {
                Toast.makeText(mContext, mContext.getResources()
                        .getString(R.string.not_able_to_have_comments),Toast.LENGTH_LONG).show();
            } else {
                ReplyQuestionFragment fragment = ReplyQuestionFragment
                        .newInstance(mQuestions.get(position));
                fragment.setTargetFragment(mAdminQuestionsFragment, 300);
                FragmentManager manager = mAdminQuestionsFragment.getFragmentManager();
                fragment.show(manager, ReplyQuestionFragment.TAG);
            }
        }

        public void bind(Question q) throws ParseException {
            if(getItemCount() == 0) {
                Toast.makeText(mContext, R.string.no_questions_student,
                        Toast.LENGTH_LONG).show();
            } else {
                ParseUser student = q.getAsker();
                tvStudentName.setText(User.getFullName(student.fetchIfNeeded()));
                tvStudentName.setVisibility(tvStudentName.INVISIBLE);
                tvSeeMore.setVisibility(tvSeeMore.INVISIBLE);
                tvWaitTime.setVisibility(tvWaitTime.INVISIBLE);
                tvPriorityEmoji.setText(q.getPriority());
                setHelpType(q.getHelpType());
                tvDescription.setText(q.getText());
                tvStartTime.setText(q.getCreatedTimeAgo());
                tvLikes.setText(Integer.toString(q.getLikeCount()) + " " +
                        mContext.getResources().getString(R.string.likes));
            }
        }

        private void setHelpType(String helpType) {
            if (helpType.equals(mContext.getResources().getString(R.string.in_person))) {
                tvHelpEmoji.setText(R.string.EMOJI_IN_PERSON);
            } else if (helpType.equals(mContext.getResources().getString(R.string.written))) {
                tvHelpEmoji.setText(R.string.EMOJI_WRITTEN);
            }
        }
    }
}
