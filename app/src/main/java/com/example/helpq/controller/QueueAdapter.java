package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.model.WaitTime;
import com.example.helpq.model.WaitTimeHelper;
import com.example.helpq.view.AnswerQuestionFragment;
import com.example.helpq.view.QueueFragment;
import com.example.helpq.view.ReplyQuestionFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
            FragmentManager manager = mQueueFragment.getParentFragment().getChildFragmentManager();
            //((MainActivity) mContext).getSupportFragmentManager();
            //List<Fragment> fragmentList = manager.getFragments();
            //FragmentManager queueFragManager = fragmentList.get(1).getChildFragmentManager();
            fragment.show(manager, AnswerQuestionFragment.TAG);
        } else {
            Toast.makeText(mContext, R.string.request_in_person,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void replyToQuestion(Question question) {
        ReplyQuestionFragment fragment = ReplyQuestionFragment.newInstance(question);
        fragment.setTargetFragment(mQueueFragment, 300);
        FragmentManager manager = mQueueFragment.getParentFragment().getChildFragmentManager();
        fragment.show(manager, ReplyQuestionFragment.TAG);
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
        updateWaitTime(mQuestions.get(adapterPosition));
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
        private ImageButton ibLike;
        private TextView tvLikes;
        private ImageButton ibView;
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
            ibLike = itemView.findViewById(R.id.ibLike);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
            ibView = itemView.findViewById(R.id.ibView);
        }

        private void adminSlideMenu(View v) {
            vQuestionView.startAnimation(slideRecyclerCell(v, -425));
            ibDelete.setVisibility(ibDelete.VISIBLE);
            ibReply.setVisibility(ibReply.VISIBLE);
            ibView.setVisibility(ibView.VISIBLE);
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
                    ibDelete.setVisibility(View.INVISIBLE);
                    ibReply.setVisibility(View.INVISIBLE);
                    resetRecyclerCell();
                }
            });
            ibView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Question question = mQuestions.get(getAdapterPosition());
                    replyToQuestion(question);
                    ibView.setVisibility(View.GONE);
                    resetRecyclerCell();
                }
            });
        }

        private void studentSlideMenu(View v, ParseUser currentUser) {
            ibDelete.setVisibility(ibDelete.VISIBLE);
            final Question q = mQuestions.get(getAdapterPosition());
            vQuestionView.startAnimation(slideRecyclerCell(v, -300));
            if(User.getFullName(currentUser)
                    .equals(tvStudentName.getText().toString())) {
                ibDelete.setVisibility(ibDelete.VISIBLE);
                ibDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        q.setIsArchived(true);
                        q.setAnsweredAt(Calendar.getInstance().getTime());
                        q.saveInBackground();
                        removeAt(getAdapterPosition());
                        mQueueFragment.createSnackbar(getAdapterPosition(), q);
                        ibDelete.setVisibility(ibDelete.INVISIBLE);
                    }
                });
                if(q.getHelpType().equals(mContext.getResources().getString(R.string.written))) {
                    ibView.setVisibility(View.VISIBLE);
                    ibView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            replyToQuestion(mQuestions.get(getAdapterPosition()));
                            ibView.setVisibility(View.GONE);
                        }
                    });
                }
            } else {
                ibReply.setVisibility(View.VISIBLE);
                ibDelete.setVisibility(View.GONE);
                ibLike.setVisibility(View.VISIBLE);

                ibLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Question question = mQuestions.get(getAdapterPosition());
                        boolean isLiked = question.isLiked();
                        if (!isLiked) {
                            question.likeQuestion(ParseUser.getCurrentUser());
                        } else {
                            question.unlikeQuestion(ParseUser.getCurrentUser());
                        }
                        question.saveInBackground();
                        setButton(ibLike, !isLiked,
                                R.drawable.ic_like, R.drawable.ic_like_active, R.color.colorRed);
                        setLikeText(question, tvLikes);
                    }
                });
                ibReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(q.getHelpType().equals(mContext.getResources().getString(R.string.written))) {
                            replyToQuestion(q);
                            ibReply.setVisibility(ibReply.GONE);
                        } else {
                            Toast.makeText(mContext,
                                mContext.getResources().getString(R.string.reply_in_person_help),
                                Toast.LENGTH_LONG).show();
                        }
                        resetRecyclerCell();
                    }
                });
            }
        }

        private TranslateAnimation slideRecyclerCell(View v, int deltaX) {
            TranslateAnimation animate = new TranslateAnimation(
                    v.getX(),
                    deltaX,
                    0,
                    0
            );
            animate.setDuration(300);
            animate.setFillAfter(true);
            return animate;
        }

        // Bind the view elements to the Question.
        public void bind(Question question) {
            tvStudentName.setText(question.getAsker().getString(Question.KEY_FULL_NAME));
            tvPriorityEmoji.setText(question.getPriority());
            questionText = question.getText();
            setInitialQuestionText();
            tvStartTime.setText(question.getCreatedTimeAgo());
            setHelpType(question.getHelpType());
            setButton(ibLike, question.isLiked(),
                    R.drawable.ic_like, R.drawable.ic_like_active, R.color.colorRed);
            setLikeText(question, tvLikes);
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
            // runnable is getting the line count before anything is rendering in order to determine
            // if see more should be displayed or not
            tvDescription.post(new Runnable() {
                @Override
                public void run() {
                    tvDescription.setText(questionText);
                    tvDescription.setMaxLines(1);
                    originalLines = tvDescription.getLineCount();
                    if(originalLines > 1) {
                        tvSeeMore.setVisibility(View.VISIBLE);
                    } else {
                        tvSeeMore.setVisibility(View.GONE);
                        tvDescription.setMaxLines(Integer.MAX_VALUE);
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
            if(ibDelete.getVisibility() == View.VISIBLE || ibReply.getVisibility() == View.VISIBLE) {
                resetRecyclerCell();
            }
        }

        private void resetRecyclerCell() {
            TranslateAnimation animate = new TranslateAnimation(
                    itemView.getX(),
                    0,
                    0,
                    0
            );
            animate.setDuration(400);
            animate.setFillAfter(true);
            vQuestionView.startAnimation(animate);
            ibDelete.setVisibility(View.GONE);
            ibReply.setVisibility(View.GONE);
            ibLike.setVisibility(View.GONE);
            ibView.setVisibility(View.GONE);
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

    // sets the color of a button, depending on whether it is active
    private void setButton(ImageView iv, boolean isActive, int strokeResId, int fillResId, int activeColor) {
        iv.setImageResource(isActive ? fillResId : strokeResId);
        iv.setColorFilter(ContextCompat.getColor(mContext, isActive ? activeColor : R.color.colorFBBlue));
    }

    private void setLikeText(Question question, TextView view) {
        int likeCount = question.getLikeCount();
        if (likeCount == 1) view.setText(String.format("%d like", question.getLikeCount()));
        else view.setText(String.format("%d likes", question.getLikeCount()));
    }

    private void updateWaitTime(final Question question) {
        ParseUser user = ParseUser.getCurrentUser();
        String adminName = "";
        if (User.isAdmin(user)) adminName = user.getUsername();
        else adminName = User.getAdminName(user);
        final ParseQuery<WaitTime> query = new ParseQuery<WaitTime>(WaitTime.class);
        query.whereEqualTo(WaitTime.KEY_ADMIN_NAME, adminName);
        query.findInBackground(new FindCallback<WaitTime>() {
            @Override
            public void done(List<WaitTime> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error querying for wait times");
                    return;
                }
                if (objects.size() > 1) {
                    Log.e(TAG, "Every admin should only have one WaitTime dataset!");
                    return;
                }
                WaitTime waitTime = objects.get(0);
                updateWaitTimeByPriority(waitTime, question);
                waitTime.saveInBackground();
            }
        });
    }

    private void updateWaitTimeByPriority(WaitTime waitTime, Question question) {
        WaitTimeHelper helper = new WaitTimeHelper(mContext);
        long newWaitTime;
        switch (question.getPriority()) {

            case WaitTimeHelper.BLOCKING:
                newWaitTime = helper.updateWaitTime(waitTime.getBlockingTime(),
                        waitTime.getBlockingSize(), question.getTimeDifference());
                waitTime.setBlockingTime(newWaitTime);
                waitTime.setBlockingSize(waitTime.getBlockingSize() + 1);
                break;

            case WaitTimeHelper.STRETCH:
                newWaitTime = helper.updateWaitTime(waitTime.getStretchTime(),
                        waitTime.getStretchSize(), question.getTimeDifference());
                waitTime.setStretchTime(newWaitTime);
                waitTime.setStretchSize(waitTime.getStretchSize() + 1);
                break;

            case WaitTimeHelper.CURIOSITY:
                newWaitTime = helper.updateWaitTime(waitTime.getCuriosityTime(),
                        waitTime.getCuriositySize(), question.getTimeDifference());
                waitTime.setCuriosityTime(newWaitTime);
                waitTime.setCuriositySize(waitTime.getCuriositySize() + 1);
                break;
        }
    }
}
