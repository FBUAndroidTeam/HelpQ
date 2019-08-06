package com.example.helpq.controller;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Notification;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.example.helpq.model.WaitTime;
import com.example.helpq.model.WaitTimeHelper;
import com.example.helpq.view.QueueFragment;
import com.example.helpq.view.ReplyQuestionFragment;
import com.example.helpq.view.admin_views.AnswerQuestionFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private static final String TAG = "QueueAdapter";
    private Context mContext;
    private List<Question> mQuestions;
    private QueueFragment mQueueFragment;
    private static ClickListener mClickListener;

    // Array to store state of adapter items (open/closed menu)
    private SparseBooleanArray mOpenItemArray = new SparseBooleanArray();

    // Maps Question objectIds to the notifications that point to them
    private Hashtable<String, Notification> mNotifications;

    // Constructor
    public QueueAdapter(Context context, List<Question> questions, QueueFragment fragment) {
        mContext = context;
        mQuestions = questions;
        mQueueFragment = fragment;
        mNotifications = new Hashtable<>();
        if (User.isAdmin(ParseUser.getCurrentUser())) findHighlightedQuestions();
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

    // Answers this question
    private void answerQuestion(int adapterPosition) {
        Question question = mQuestions.get(adapterPosition);
        if (question.getHelpType().equals(mContext.getResources().getString(R.string.written))) {
            AnswerQuestionFragment fragment = AnswerQuestionFragment.newInstance(question);
            fragment.setTargetFragment(mQueueFragment, 300);
            FragmentManager manager = mQueueFragment.getFragmentManager();
            fragment.show(manager, AnswerQuestionFragment.TAG);
        } else {
            Toast.makeText(mContext, R.string.request_in_person,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void replyToQuestion(Question question) {
        ReplyQuestionFragment fragment = ReplyQuestionFragment.newInstance(question);
        fragment.setTargetFragment(mQueueFragment, 300);
        FragmentManager manager = mQueueFragment.getFragmentManager();
        fragment.show(manager, ReplyQuestionFragment.TAG);
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

    private void findHighlightedQuestions() {
        ParseQuery query = QueryFactory.Notifications.getNotifications();
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error with notification query");
                    return;
                }
                for (int i = 0; i < objects.size(); i++) {
                    String questionId = objects.get(i).getQuestionId();
                    if (questionId != null) {
                        mNotifications.put(objects.get(i).getQuestionId(), objects.get(i));
                    }
                }
            }
        });
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        // View that contains all the elements that slide
        private View vQuestionView;

        // Layout fields of item_question
        private TextView tvStudentName;
        private TextView tvPriorityEmoji;
        private TextView tvHelpEmoji;
        private TextView tvDescription;
        private TextView tvStartTime;
        private TextView tvLikes;
        private TextView tvSeeMore;
        private String questionText;
        private TextView tvWaitTime;
        private ImageView ivMarker;

        // Number of lines (needed to implement See More)
        private int originalLines;

        // ImageButtons contained within hidden slide menu
        private ImageButton ibDelete;
        private ImageButton ibReply;
        private ImageButton ibLike;
        private ImageButton ibView;

        // Boolean variables
        private boolean isAdmin;
        private boolean isWritten;
        private boolean isPeer;

        // How much the menu should slide
        private int iSlideDeltaX;


        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            // Find views
            vQuestionView = itemView.findViewById(R.id.clQuestion);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvHelpEmoji = itemView.findViewById(R.id.tvHelpEmoji);
            tvDescription = itemView.findViewById(R.id.tvQuestion);
            tvStartTime = itemView.findViewById(R.id.tvAnswerTime);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvSeeMore = itemView.findViewById(R.id.tvSeeMore);
            tvWaitTime = itemView.findViewById(R.id.tvWaitTime);
            ibDelete = itemView.findViewById(R.id.ibDelete);
            ibReply = itemView.findViewById(R.id.ibReply);
            ibLike = itemView.findViewById(R.id.ibLike);
            ibView = itemView.findViewById(R.id.ibView);
            onClickSeeMore();
        }

        // Bind the view elements to the Question.
        public void bind(final Question question) {
            tvStudentName.setText(question.getAsker().getString(Question.KEY_FULL_NAME));
            tvPriorityEmoji.setText(question.getPriority());
            questionText = question.getText();
            setInitialQuestionText();
            tvStartTime.setText(question.getCreatedTimeAgo());
            setHelpType(question.getHelpType());
            setLikeButtonImage(ibLike, question.isLiked());
            setLikeText(question, tvLikes);
            setWaitTimeText(question, tvWaitTime);
            ivMarker = itemView.findViewById(R.id.ivMarker);

            isAdmin = User.isAdmin(ParseUser.getCurrentUser());
            isWritten = question.getHelpType()
                    .equals(mContext.getResources().getString(R.string.written));
            isPeer = !User.getFullName(ParseUser.getCurrentUser())
                    .equals(tvStudentName.getText().toString());

            setupSlideDeltaX();
            setupDeleteButton(question);
            setupReplyButton(question);
            setupLikeButton();
            setupViewButton(question);
            openMenuIfApplicable();
            markNewQuestion(question);
        }

        private void onClickSeeMore() {
            tvSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (originalLines > 1) {
                        setTextExpansion();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Display correct slide-back menu
            if (mOpenItemArray.get(getAdapterPosition())) {
                Sound.closeSlideMenu(mContext);
                hideActions(v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            ivMarker.setVisibility(View.GONE);
            mClickListener.onItemLongClick(getAdapterPosition(), v);
            if(!mOpenItemArray.get(getAdapterPosition())) {
                Sound.openSlideMenu(mContext);
                if (isAdmin) {
                    adminSlideMenu(400);
                } else {
                    studentSlideMenu(400);
                }
            }
            return true;
        }

        private void setupSlideDeltaX() {
            if (isAdmin && isWritten) iSlideDeltaX = -425;
            if (isAdmin && !isWritten) iSlideDeltaX = -160;
            if (!isAdmin && isWritten) iSlideDeltaX = -300;
            if (!isAdmin && !isWritten) iSlideDeltaX = -160;
            if (!isAdmin && isPeer && isWritten) iSlideDeltaX = -300;
            if (!isAdmin && isPeer && !isWritten) iSlideDeltaX = -160;
        }

        private void setupViewButton(final Question question) {
            ibView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sound.openDialogWindow(mContext);
                    replyToQuestion(question);
                    resetRecyclerCell(400);
                }
            });
            ibView.setVisibility(View.GONE);
        }

        private void setupLikeButton() {
            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sound.likeClicked(mContext);
                    Question question = mQuestions.get(getAdapterPosition());
                    boolean isLiked = question.isLiked();
                    if (!isLiked) {
                        question.likeQuestion(ParseUser.getCurrentUser());
                    } else {
                        question.unlikeQuestion(ParseUser.getCurrentUser());
                    }
                    question.saveInBackground();
                    setLikeButtonImage(ibLike, !isLiked);
                    setLikeText(question, tvLikes);
                }
            });
            ibLike.setVisibility(View.GONE);
        }

        private void setupReplyButton(final Question question) {
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sound.openDialogWindow(mContext);
                    if (isAdmin) {
                        answerQuestion(getAdapterPosition());
                    } else {
                        replyToQuestion(question);
                    }
                    resetRecyclerCell(400);
                }
            });
            ibReply.setVisibility(View.GONE);
        }

        private void setupDeleteButton(final Question question) {
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sound.delete(mContext);
                    if (isAdmin) {
                        question.setIsArchived(true);
                        question.setAnsweredAt(new Date(System.currentTimeMillis()));
                        question.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null) {
                                    Toast.makeText(mContext, R.string.archive_question,
                                            Toast.LENGTH_LONG).show();
                                    resetRecyclerCell(400);
                                    removeAt(getAdapterPosition());
                                } else {
                                    Log.d(TAG, "Failed to archive question");
                                    e.printStackTrace();
                                }
                            }
                        });
                        updateWaitTime(mQuestions.get(getAdapterPosition()));
                    } else {
                        question.setIsArchived(true);
                        question.setAnsweredAt(Calendar.getInstance().getTime());
                        question.saveInBackground();
                        resetRecyclerCell(400);
                        removeAt(getAdapterPosition());
                        mQueueFragment.createSnackbar(getLayoutPosition(), question);
                        ibDelete.setVisibility(View.GONE);
                    }
                }
            });
            ibDelete.setVisibility(View.GONE);
        }

        private void openMenuIfApplicable() {
            if (mOpenItemArray.get(getAdapterPosition())) {
                resetRecyclerCell(0);
                getMenu(0);
                return;
            } else {
                resetRecyclerCell(0);
                ibView.setClickable(false);
                ibLike.setClickable(false);
                ibDelete.setClickable(false);
                ibReply.setClickable(false);
            }
        }

        private void getMenu(int duration) {
            if (User.isAdmin(ParseUser.getCurrentUser())) {
                adminSlideMenu(duration);
            } else {
                studentSlideMenu(duration);
            }
        }

        private void adminSlideMenu(int duration) {
            ibDelete.setVisibility(View.VISIBLE);
            ibDelete.setClickable(true);
            if (isWritten) {
                ibReply.setVisibility(View.VISIBLE);
                ibReply.setClickable(true);
                ibView.setVisibility(View.VISIBLE);
                ibView.setClickable(true);
            }
            if (!mOpenItemArray.get(getAdapterPosition())) slideRecyclerCell(duration);
        }

        private void studentSlideMenu(int duration) {
            ibDelete.setVisibility(View.VISIBLE);
            if (isPeer) {
                peerQuestionMenu(duration);
                return;
            }
            if (isWritten) {
                currentUserWrittenMenu(duration);
                return;
            }
            currentUserInpersonMenu(duration);
        }

        private void currentUserWrittenMenu(int duration) {
            if (!mOpenItemArray.get(getAdapterPosition())) slideRecyclerCell(duration);
            ibDelete.setVisibility(View.VISIBLE);
            ibDelete.setClickable(true);
            ibView.setVisibility(View.VISIBLE);
            ibView.setClickable(true);
        }

        private void currentUserInpersonMenu(int duration) {
            if (!mOpenItemArray.get(getAdapterPosition())) slideRecyclerCell(duration);
            ibDelete.setVisibility(View.VISIBLE);
            ibDelete.setClickable(true);
        }

        private void peerQuestionMenu(int duration) {
            if (!mOpenItemArray.get(getAdapterPosition())) slideRecyclerCell(duration);

            if (isWritten) {
                ibReply.setVisibility(View.VISIBLE);
                ibReply.setClickable(true);
            }
            ibLike.setVisibility(View.VISIBLE);
            ibLike.setClickable(true);
            ibDelete.setVisibility(View.GONE);
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
                    tvDescription.setMaxLines(Integer.MAX_VALUE);
                    tvDescription.setText(questionText);
                    tvDescription.setMaxLines(1);
                    originalLines = tvDescription.getLineCount();
                    if (originalLines > 1) {
                        tvSeeMore.setVisibility(View.VISIBLE);
                    } else {
                        tvSeeMore.setVisibility(View.GONE);
                    }
                }
            });
        }

        // Place a marker on this question if a notification points to it.
        // Delete the notification.
        private void markNewQuestion(Question question) {
            String questionId = question.getObjectId();
            if (mNotifications.containsKey(questionId)) {
                ivMarker.setVisibility(View.VISIBLE);
                Notification notification = mNotifications.get(questionId);
                try {
                    notification.delete();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                notification.saveInBackground();
                mNotifications.remove(questionId);
            }
            else {
                ivMarker.setVisibility(View.GONE);
            }
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

        private void hideActions(View v) {
            if (mOpenItemArray.get(getAdapterPosition())) {
                mClickListener.onItemClick(getAdapterPosition(), v);
                resetRecyclerCell(400);
            }
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
            ibLike.setClickable(false);
            ibView.setClickable(false);
            ibReply.setClickable(false);
            ibDelete.setClickable(false);
        }
    }

    // Set the like button image, depending on whether it is active.
    private void setLikeButtonImage(ImageButton ib, boolean isActive) {
        ib.setBackgroundResource(isActive ? R.drawable.heart_icon_active : R.drawable.heart_icon);
    }

    private void setLikeText(Question question, TextView view) {
        int likeCount = question.getLikeCount();
        if (likeCount == 1) view.setText(String.format("%d like", question.getLikeCount()));
        else view.setText(String.format("%d likes", question.getLikeCount()));
    }

    private void setWaitTimeText(final Question question, final TextView view) {
        final ParseQuery<WaitTime> query = QueryFactory.WaitTimes.getAdminWaitTimes();
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
                WaitTime averageWaitTime = objects.get(0);
                WaitTimeHelper helper = new WaitTimeHelper(mContext);
                view.setText(helper.getQuestionWaitTime(question, averageWaitTime) + " wait");
            }
        });
    }

    // Add the wait time of the newly archived question to the weighted average.
    private void updateWaitTime(final Question question) {
        ParseQuery<WaitTime> query = QueryFactory.WaitTimes.getAdminWaitTimes();
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

    // Set the wait time by priority, and increment the corresponding question count.
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
