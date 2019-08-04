package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
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
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.model.WaitTime;
import com.example.helpq.model.WaitTimeHelper;
import com.example.helpq.view.admin_views.AnswerQuestionFragment;
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
            FragmentManager manager = mQueueFragment.getFragmentManager();
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
        FragmentManager manager = mQueueFragment.getFragmentManager();
        fragment.show(manager, ReplyQuestionFragment.TAG);
    }

    // Archives this question
    private void archiveQuestion(Question question, final int adapterPosition) {
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

        // Number of lines (needed to implement See More)
        private int originalLines;

        // ImageButtons contained within hidden slide menu
        private ImageButton ibDelete;
        private ImageButton ibReply;
        private ImageButton ibLike;
        private ImageButton ibView;

        // Boolean variables
        private boolean isSlideMenuOpen;
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
            if (isAdmin) {
                if (isWritten) hideActions(v, iSlideDeltaX);
                else hideActions(v, iSlideDeltaX);
            } else {
                hideActions(v, iSlideDeltaX);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            mClickListener.onItemLongClick(getAdapterPosition(), v);
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (isAdmin) {
                adminSlideMenu(v);
            } else {
                studentSlideMenu(v, currentUser);
            }
            return true;
        }

        private void setupSlideDeltaX() {
            if (isAdmin && isWritten) iSlideDeltaX = -425;
            if (isAdmin && !isWritten) iSlideDeltaX = -300;
            if (!isAdmin && isWritten) iSlideDeltaX = -300;
            if (!isAdmin && !isWritten) iSlideDeltaX = -160;
            if (isPeer && isWritten) iSlideDeltaX = -300;
            if (isPeer && !isWritten) iSlideDeltaX = -160;
            isSlideMenuOpen = false;
        }

        private void setupViewButton(final Question question) {
            ibView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyToQuestion(question);
                    resetRecyclerCell(iSlideDeltaX);
                }
            });
            ibView.setClickable(false);
            ibView.setVisibility(View.GONE);
        }

        private void setupLikeButton() {
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
                    setLikeButtonImage(ibLike, !isLiked);
                    setLikeText(question, tvLikes);
                }
            });
            ibLike.setClickable(false);
            ibLike.setVisibility(View.GONE);
        }

        private void setupReplyButton(final Question question) {
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdmin) {
                        answerQuestion(getAdapterPosition());
                    } else {
                        replyToQuestion(question);
                    }
                    resetRecyclerCell(iSlideDeltaX);
                }
            });
            ibReply.setClickable(false);
            ibReply.setVisibility(View.GONE);
        }

        private void setupDeleteButton(final Question question) {
            ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAdmin) {
                        archiveQuestion(question, getAdapterPosition());
                    } else {
                        question.setIsArchived(true);
                        question.setAnsweredAt(Calendar.getInstance().getTime());
                        question.saveInBackground();
                        removeAt(getAdapterPosition());
                        mQueueFragment.createSnackbar(getLayoutPosition(), question);
                        ibDelete.setVisibility(View.GONE);
                        resetRecyclerCell(iSlideDeltaX);
                    }
                }
            });
            ibDelete.setClickable(false);
            ibDelete.setVisibility(View.GONE);
        }

        private void adminSlideMenu(View v) {
            ibDelete.setVisibility(View.VISIBLE);
            ibView.setVisibility(View.VISIBLE);
            ibDelete.setClickable(true);
            ibView.setClickable(true);
            if (isWritten) {
                ibReply.setVisibility(View.VISIBLE);
                ibReply.setClickable(true);
            }
            if (!isSlideMenuOpen) {
                vQuestionView.startAnimation(slideRecyclerCell(v, iSlideDeltaX));
            }
        }

        private void studentSlideMenu(View v, ParseUser currentUser) {
            ibDelete.setVisibility(View.VISIBLE);
            final Question q = mQuestions.get(getAdapterPosition());

            if (isPeer) {
                peerQuestionMenu(v, q);
                return;
            }
            if (isWritten) {
                currentUserWrittenMenu(v, q);
                return;
            }
            currentUserInpersonMenu(v, q);
        }

        private void currentUserWrittenMenu(View v, final Question q) {
            if (!isSlideMenuOpen) {
                vQuestionView.startAnimation(slideRecyclerCell(v, iSlideDeltaX));
            }
            ibDelete.setVisibility(View.VISIBLE);
            ibDelete.setClickable(true);
            ibView.setVisibility(View.VISIBLE);
            ibView.setClickable(true);
        }

        private void currentUserInpersonMenu(View v, final Question q) {
            if (!isSlideMenuOpen) {
                vQuestionView.startAnimation(slideRecyclerCell(v, iSlideDeltaX));
            }
            ibDelete.setVisibility(View.VISIBLE);
            ibDelete.setClickable(true);
        }

        private void peerQuestionMenu(View v, final Question q) {
            if (!isSlideMenuOpen) {
                vQuestionView.startAnimation(slideRecyclerCell(v, iSlideDeltaX));
            }
            if (isWritten) {
                ibReply.setVisibility(View.VISIBLE);
                ibReply.setClickable(true);
            }
            ibLike.setVisibility(View.VISIBLE);
            ibLike.setClickable(true);
            ibDelete.setVisibility(View.GONE);
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
            isSlideMenuOpen = true;
            return animate;
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
                    if (originalLines > 1) {
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

        private void hideActions(View v, int deltaX) {
            if (isSlideMenuOpen) {
                mClickListener.onItemClick(getAdapterPosition(), v);
                resetRecyclerCell(deltaX);
            }
        }

        private void resetRecyclerCell(int deltaX) {
            TranslateAnimation animate = new TranslateAnimation(
                    itemView.getX() + deltaX,
                    0,
                    0,
                    0
            );
            animate.setDuration(300);
            animate.setFillAfter(true);
            ibLike.setClickable(false);
            ibView.setClickable(false);
            ibReply.setClickable(false);
            ibDelete.setClickable(false);
            vQuestionView.startAnimation(animate);
            isSlideMenuOpen = false;
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
