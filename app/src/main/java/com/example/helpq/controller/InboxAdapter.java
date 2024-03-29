package com.example.helpq.controller;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.example.helpq.view.ReplyQuestionFragment;
import com.example.helpq.view.student_views.InboxFragment;
import com.parse.ParseUser;

import java.util.List;
import java.util.Set;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private static final String TAG = "InboxAdapter";
    private Context mContext;
    private List<Question> mMessages;
    private static ClickListener mClickListener;
    private InboxFragment mInboxFragment;

    // Array to store state of adapter items (open/closed menu)
    private SparseBooleanArray mOpenItemArray = new SparseBooleanArray();

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

        private static final int DURATION = 400;

        // Layout fields of item_message
        private TextView tvQuestion;
        private TextView tvPriorityEmoji;
        private TextView tvAnswerTime;
        private TextView tvAnswer;
        private TextView tvLikes;
        private ImageButton ibLike;
        private ImageButton ibView;
        private ImageView ivMarker;
        private View vQuestionView;
        private int iSlideDeltaX;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvAnswerTime = itemView.findViewById(R.id.tvMonth);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibView = itemView.findViewById(R.id.ibView);
            ivMarker = itemView.findViewById(R.id.ivMarker);
            vQuestionView = itemView.findViewById(R.id.clQuestion);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        // Bind the view elements to the message.
        public void bind(Question message) {
            setupMessageText(message);
            setupMenuClickListeners();
            openMenuIfApplicable();
            markNewMessage(message.getObjectId());
        }

        @Override
        public void onClick(View v) {
            if (mOpenItemArray.get(getAdapterPosition())) {
                Sound.closeSlideMenu(mContext);
                resetRecyclerCell(DURATION);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mClickListener.onItemLongClick(getAdapterPosition(), v);
            ivMarker.setVisibility(View.GONE);
            if (!mOpenItemArray.get(getAdapterPosition())) {
                Sound.openSlideMenu(mContext);
                getMenu(DURATION);
            }
            return true;
        }

        private void setupMessageText(Question message) {
            tvPriorityEmoji.setText(message.getPriority());
            tvQuestion.setText(message.getText());
            tvAnswerTime.setText(message.getAnsweredTimeAgo());
            if (User.isAdmin(ParseUser.getCurrentUser())) iSlideDeltaX = -170;
            else iSlideDeltaX = -310;
            tvAnswer.setText(message.getAnswer());
//            setTextClickableIfNeeded(message);
            setLikeText(message, tvLikes);
            setLikeButton(ibLike, message.isLiked());
        }

        private void setTextClickableIfNeeded(Question message) {
            if((message.getAnswer()).
                    equals(mContext.getResources().getString(R.string.see_student_reply))) {
                tvAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyToQuestion(mMessages.get(getAdapterPosition()));
                    }
                });
            } else {
                tvAnswer.setClickable(false);
            }
        }

        private void setupMenuClickListeners() {
            ibView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sound.openDialogWindow(mContext);
                    replyToQuestion(mMessages.get(getAdapterPosition()));
                    resetRecyclerCell(DURATION);
                }
            });
            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sound.likeClicked(mContext);
                    Question question = mMessages.get(getAdapterPosition());
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

        // If the menu should be open, open it.
        private void openMenuIfApplicable() {
            if (mOpenItemArray.get(getAdapterPosition())) {
                resetRecyclerCell(0);
                getMenu(0);
                return;
            } else {
                resetRecyclerCell(0);
                ibView.setClickable(false);
                ibLike.setClickable(false);
            }
        }

        // Place a marker on this message if a notification points to it.
        // Delete the notification.
        private void markNewMessage(String messageId) {
            Set<String> notifications = mInboxFragment.mNotifications;
            if (notifications.contains(messageId)) {
                ivMarker.setVisibility(View.VISIBLE);
            }
            else {
                ivMarker.setVisibility(View.GONE);
            }
        }

        private void getMenu(int duration) {
            if (User.isAdmin(ParseUser.getCurrentUser())) {
                adminMenu(duration);
            } else {
                studentMenu(duration);
            }
        }

        private void adminMenu(int duration) {
            if (!mOpenItemArray.get(getAdapterPosition())) slideRecyclerCell(duration);
            ibLike.setVisibility(View.GONE);
            ibView.setVisibility(View.VISIBLE);
            ibView.setClickable(true);
        }

        private void studentMenu(int duration) {
            if (!mOpenItemArray.get(getAdapterPosition())) slideRecyclerCell(duration);
            ibView.setVisibility(View.VISIBLE);
            ibLike.setVisibility(View.VISIBLE);
            ibView.setClickable(true);
            ibLike.setClickable(true);
        }

        private void slideRecyclerCell(int duration) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(vQuestionView,
                    "translationX", iSlideDeltaX);
            animation.setDuration(duration);
            animation.start();
            mOpenItemArray.put(getAdapterPosition(), true);
        }

        private void resetRecyclerCell(int duration) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(vQuestionView,
                    "x", 0);
            animation.setDuration(duration);
            animation.start();
            mOpenItemArray.put(getAdapterPosition(), false);
            ibView.setClickable(false);
            ibLike.setClickable(false);
        }
    }
}
