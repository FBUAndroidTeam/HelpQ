package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.view.InboxFragment;
import com.example.helpq.view.ReplyQuestionFragment;
import com.parse.ParseUser;

import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private static final String TAG = "InboxAdapter";
    private final int iAdminDeltaX = -150;
    private final int iStudentDeltaX = -300;
    private Context mContext;
    private List<Question> mMessages;
    private static ClickListener mClickListener;
    private InboxFragment mInboxFragment;

    // Constructor
    public InboxAdapter(Context context, List<Question> mMessages, InboxFragment fragment) {
        this.mContext = context;
        this.mMessages = mMessages;
        mInboxFragment = fragment;
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

    private void setLikeText(Question question, TextView view) {
        int likeCount = question.getLikeCount();
        if (likeCount == 1) view.setText(String.format("%d like", question.getLikeCount()));
        else view.setText(String.format("%d likes", question.getLikeCount()));
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        InboxAdapter.mClickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    private void replyToQuestion(Question question) {
        ReplyQuestionFragment fragment = ReplyQuestionFragment.newInstance(question);
        fragment.setTargetFragment(mInboxFragment, 300);
        FragmentManager manager = mInboxFragment.getFragmentManager();
        fragment.show(manager, ReplyQuestionFragment.TAG);
    }

    // Set the like button, depending on whether it is active.
    private void setLikeButton(ImageButton ib, boolean isActive) {
        ib.setBackgroundResource(isActive ? R.drawable.heart_icon_active : R.drawable.heart_icon);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        // Layout fields of item_message
        private TextView tvQuestion;
        private TextView tvPriorityEmoji;
        private TextView tvAnswerTime;
        private TextView tvAdminName;
        private TextView tvAnswer;
        private TextView tvLikes;
        private ImageButton ibLike;
        private ImageButton ibView;
        private View vQuestionView;
        private boolean isSlideMenuOpen;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvAnswerTime = itemView.findViewById(R.id.tvAnswerTime);
            tvAdminName = itemView.findViewById(R.id.tvAdminName);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibView = itemView.findViewById(R.id.ibView);
            vQuestionView = itemView.findViewById(R.id.clQuestion);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
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
            setLikeText(message, tvLikes);
            setLikeButton(ibLike, message.isLiked());
            isSlideMenuOpen = false;
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

        private void resetRecyclerCell(int deltaX) {
            TranslateAnimation animate = new TranslateAnimation(
                    itemView.getX() + deltaX,
                    0,
                    0,
                    0
            );
            animate.setDuration(400);
            animate.setFillAfter(true);
            vQuestionView.startAnimation(animate);
        }

        @Override
        public void onClick(View v) {
            if (isSlideMenuOpen) {
                if (User.isAdmin(ParseUser.getCurrentUser())) {
                    resetRecyclerCell(iAdminDeltaX);
                } else {
                    resetRecyclerCell(iStudentDeltaX);
                }
                isSlideMenuOpen = false;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mClickListener.onItemLongClick(getAdapterPosition(), v);
            Question question = mMessages.get(getAdapterPosition());
            if(User.isAdmin(ParseUser.getCurrentUser())) {
                adminMenu(v);
            } else {
                studentMenu(v, question);
            }

            return true;
        }

        private void studentMenu(View v, final Question question) {
            if (!isSlideMenuOpen) {
                vQuestionView.startAnimation(slideRecyclerCell(v, iStudentDeltaX));
                isSlideMenuOpen = true;
            }
            ibView.setVisibility(View.VISIBLE);
            ibLike.setVisibility(View.VISIBLE);
            ibView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyToQuestion(question);
                    resetRecyclerCell(iStudentDeltaX);
                }
            });

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isLiked = question.isLiked();
                    if (!isLiked) {
                        question.likeQuestion(ParseUser.getCurrentUser());
                    } else {
                        question.unlikeQuestion(ParseUser.getCurrentUser());
                    }
                    question.saveInBackground();
                    setLikeButton(ibLike, !isLiked);
                    setLikeText(question, tvLikes);
                }
            });
        }

        private void adminMenu(View v) {
            if (!isSlideMenuOpen) {
                vQuestionView.startAnimation(slideRecyclerCell(v, iAdminDeltaX));
                isSlideMenuOpen = true;
            }
            ibLike.setVisibility(View.GONE);
            ibView.setVisibility(View.VISIBLE);
            ibView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Question message = mMessages.get(getAdapterPosition());
                    replyToQuestion(message);
                    resetRecyclerCell(iAdminDeltaX);
                }
            });
        }
    }
}
