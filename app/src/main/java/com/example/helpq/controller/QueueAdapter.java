package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.view.AnswerQuestionFragment;
import com.example.helpq.view.MainActivity;
import com.example.helpq.view.QueueFragment;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private static final String TAG = "QueueAdapter";
    private Context mContext;
    private List<Question> mQuestions;
    private QueueFragment mQueueFragment;
    private static ClickListener mClickListener;

    // Constructor
    public QueueAdapter(Context context, List<Question> questions, QueueFragment fragment) {
        mContext = context;
        mQuestions = questions;
        mQueueFragment = fragment;
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

    // Clean all elements of the recycler
    public void clear() {
        mQuestions.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Question> list) {
        mQuestions.addAll(list);
        Collections.sort(mQuestions);
        notifyDataSetChanged();
    }

    // Answers this question
    private void answerQuestion(int adapterPosition) {
        Question question = mQuestions.get(adapterPosition);
        if (question.getHelpType().equals(mContext.getResources().getString(R.string.written))) {
            AnswerQuestionFragment fragment = AnswerQuestionFragment.newInstance(question);
            fragment.setTargetFragment(mQueueFragment, 300);
            FragmentManager manager = ((MainActivity) mContext).getSupportFragmentManager();
            List<Fragment> fragmentList = manager.getFragments();
            FragmentManager queueFragManager = fragmentList.get(1).getChildFragmentManager();
            fragment.show(queueFragManager, AnswerQuestionFragment.TAG);
        } else {
            Toast.makeText(mContext, R.string.request_in_person,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void replyToQuestion(int adapterPosition) {
        Question question = mQuestions.get(adapterPosition);
        if(question.getHelpType().equals(mContext.getResources().getString(R.string.written))) {

        }
    }

    // Archives this question
    private void archiveQuestion(final int adapterPosition) {
        Question question = mQuestions.get(adapterPosition);
        question.setIsArchived(true);
        question.setAnsweredAt(new Date(System.currentTimeMillis()));
        question.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(mContext, R.string.archive_question, Toast.LENGTH_LONG).show();
                    removeAt(adapterPosition);
                } else {
                    Log.d(TAG, "Failed to archive question");
                    e.printStackTrace();
                }
            }
        });
    }

    // Deletes this question from parse
    public void deleteQuestion(Question q) {
        try {
            q.delete();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q.saveInBackground();
        notifyDataSetChanged();
//        removeAt(adapterPosition);
    }

    // Removes question at this position
    public void removeAt(int position) {
        mQuestions.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mQuestions.size());
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        QueueAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        // Layout fields of item_question
        private TextView tvStudentName;
        private TextView tvPriorityEmoji;
        private TextView tvHelpEmoji;
        private TextView tvDescription;
        private TextView tvStartTime;
        private View vQuestionView;
        private ImageButton ibDelete;
        private ImageButton ibReply;
        private TextView tvSeeMore;
        private String questionText;
        private int originalLines;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvHelpEmoji = itemView.findViewById(R.id.tvHelpEmoji);
            tvDescription = itemView.findViewById(R.id.tvQuestion);
            vQuestionView = itemView.findViewById(R.id.clQuestion);
            tvStartTime = itemView.findViewById(R.id.tvAnswerTime);
            ibDelete = itemView.findViewById(R.id.ibDelete);
            ibReply = itemView.findViewById(R.id.ibReply);
            tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
        }

        private void adminSlideMenu(View v) {
            TranslateAnimation animate = new TranslateAnimation(
                    v.getX(),
                    -325,
                    0,
                    0
            );
            animate.setDuration(300);
            animate.setFillAfter(true);
            vQuestionView.startAnimation(animate);
            ibDelete.setVisibility(ibDelete.VISIBLE);
            ibReply.setVisibility(ibReply.VISIBLE);
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    archiveQuestion(getAdapterPosition());
                }
            });
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerQuestion(getAdapterPosition());
                    ibDelete.setVisibility(ibDelete.INVISIBLE);
                    ibReply.setVisibility(ibReply.INVISIBLE);
                }
            });
        }

        private void studentSlideMenu(View v, ParseUser currentUser) {
            TranslateAnimation animate = new TranslateAnimation(
                    v.getX(),
                    -150,
                    0,
                    0
            );
            animate.setDuration(300);
            animate.setFillAfter(true);
            vQuestionView.startAnimation(animate);
            ibDelete.setVisibility(ibDelete.VISIBLE);
            if(User.getFullName(currentUser)
                    .equals(tvStudentName.getText().toString())) {
                ibDelete.setVisibility(ibDelete.VISIBLE);
                ibDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Question q = mQuestions.get(position);
                        q.setIsArchived(true);
                        q.setAnsweredAt(Calendar.getInstance().getTime());
                        q.saveInBackground();
                        removeAt(position);
                        mQueueFragment.createSnackbar(position, q);
                        ibDelete.setVisibility(ibDelete.INVISIBLE);
                    }
                });
            } else {
                ibReply.setVisibility(View.VISIBLE);
                ibDelete.setVisibility(View.GONE);
                ibReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyToQuestion(getAdapterPosition());
                        ibReply.setVisibility(ibReply.INVISIBLE);
                    }
                });
            }
        }

        // Bind the view elements to the Question.
        public void bind(Question question) {
            tvStudentName.setText(question.getAsker().getString(Question.KEY_FULL_NAME));
            tvPriorityEmoji.setText(question.getPriority());
            questionText = question.getText();
            setInitialQuestionText();
            tvStartTime.setText(question.getCreatedTimeAgo());
            setHelpType(question.getHelpType());
        }

        private void setHelpType(String helpType) {
            if (helpType.equals(mContext.getResources().getString(R.string.in_person))) {
                tvHelpEmoji.setText(R.string.EMOJI_IN_PERSON);
            } else if (helpType.equals(mContext.getResources().getString(R.string.written))) {
                tvHelpEmoji.setText(R.string.EMOJI_WRITTEN);
            }
        }

        //determines whether or not see more should be visible
        private void setInitialQuestionText() {
            tvDescription.setText(questionText);
            // runnable is getting the line count before anything is rendering in order to determine
            // if see more should be displayed or not
            tvDescription.post(new Runnable() {
                @Override
                public void run() {
                    originalLines = tvDescription.getLineCount();
                    if(originalLines > 1) {
                        tvDescription.setMaxLines(1);
                        tvSeeMore.setVisibility(View.VISIBLE);
                    } else {
                        tvSeeMore.setVisibility(View.GONE);
                    }
                }
            });
        }

        //determines whether to expand or collapse cell when cell is clicked on
        private void setTextExpansion() {
            if (tvSeeMore.getText().equals(mContext.getResources().getString(R.string.see_more))) {
                tvSeeMore.setText(mContext.getResources().getString(R.string.see_less));
                tvDescription.setMaxLines(Integer.MAX_VALUE);
            } else {
                tvSeeMore.setText(mContext.getResources().getString(R.string.see_more));
                tvDescription.setMaxLines(1);
            }
        }

        @Override
        public void onClick(View v) {
            if(originalLines > 1) {
                setTextExpansion();
            }
            hideActions(v);
        }

        private void hideActions(View v) {
            mClickListener.onItemClick(getAdapterPosition(), v);
            if(ibDelete.getVisibility() == View.VISIBLE || ibReply.getVisibility() == View.VISIBLE){
                TranslateAnimation animate = new TranslateAnimation(
                        itemView.getX(),
                        0,
                        0,
                        0
                );
                animate.setDuration(400);
                animate.setFillAfter(true);
                vQuestionView.startAnimation(animate);
                ibDelete.setVisibility(ibDelete.GONE);
                ibReply.setVisibility(ibReply.GONE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mClickListener.onItemLongClick(getAdapterPosition(), v);
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (User.isAdmin(currentUser)) {
                adminSlideMenu(v);
            } else {
                studentSlideMenu(v, currentUser);
            }
            return true;
        }
    }
}
