package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.view.AnswerQuestionFragment;
import com.example.helpq.view.CreateQuestionFragment;
import com.example.helpq.view.MainActivity;
import com.example.helpq.view.QueueFragment;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private static final String TAG = "QueueAdapter";
    private Context mContext;
    private List<Question> mQuestions;
    private QueueFragment mQueueFragment;

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

    // Add a list of items -- change to type used
    public void addAll(List<Question> list) {
        mQuestions.addAll(list);
        Collections.sort(mQuestions);
        notifyDataSetChanged();
    }

    // Answers this question
    private void answerQuestion(int adapterPosition) {
        Question question = mQuestions.get(adapterPosition);
        if (question.getHelpType()
                .equals(mContext.getResources().getString(R.string.written))) {
            AnswerQuestionFragment fragment = AnswerQuestionFragment.newInstance(question);
            fragment.setTargetFragment(mQueueFragment, 300);
            FragmentManager manager = ((MainActivity) mContext).getSupportFragmentManager();
            fragment.show(manager, CreateQuestionFragment.TAG);
        } else {
            Toast.makeText(mContext, R.string.request_in_person,
                    Toast.LENGTH_LONG).show();
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

    // Removes question at this position
    public void removeAt(int position) {
        mQuestions.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mQuestions.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // Layout fields of item_question
        private TextView tvStudentName;
        private TextView tvPriorityEmoji;
        private TextView tvHelpEmoji;
        private TextView tvDescription;
        private TextView tvStartTime;
        private View vQuestionView;
        private ImageButton ibDelete;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvPriorityEmoji = itemView.findViewById(R.id.tvPriorityEmoji);
            tvHelpEmoji = itemView.findViewById(R.id.tvHelpEmoji);
            tvDescription = itemView.findViewById(R.id.tvQuestion);
            vQuestionView = itemView.findViewById(R.id.clQuestion);
            tvStartTime = itemView.findViewById(R.id.tvAnswerTime);
            ibDelete = itemView.findViewById(R.id.ibDelete);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (User.isAdmin(currentUser)) {
                        //                        showAdminPopup(v);
                        TranslateAnimation animate = new TranslateAnimation(
                                v.getX(),
                                -200,
                                0,
                                0
                        );
                        animate.setDuration(300);
                        animate.setFillAfter(true);
                        ibDelete.setVisibility(ibDelete.VISIBLE);
                        vQuestionView.startAnimation(animate);
                        ibDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                answerQuestion(getAdapterPosition());
                            }
                        });

                    } else if (User.getFullName(currentUser)
                            .equals(tvStudentName.getText().toString())) {
                        showStudentPopup(v);
                    }
                    return false;
                }
            });
        }

        // Displays anchored popup menu based on view selected (for admin)
        private void showAdminPopup(View v) {
            PopupMenu popup = new PopupMenu(mContext, v);
            popup.inflate(R.menu.menu_popup_admin);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_answer:
                            answerQuestion(getAdapterPosition());
                            return true;
                        case R.id.menu_delete:
                            archiveQuestion(getAdapterPosition());
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }

        // Displays anchored popup menu based on view selected (for student)
        private void showStudentPopup(View v) {
            PopupMenu popup = new PopupMenu(mContext, v);
            popup.inflate(R.menu.menu_popup_student);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu_delete:
                            int adapterPosition = getAdapterPosition();
                            Question question = mQuestions.get(adapterPosition);
                            try {
                                question.delete();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            question.saveInBackground();
                            Toast.makeText(mContext, R.string.delete_question,
                                    Toast.LENGTH_LONG).show();
                            removeAt(adapterPosition);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        }

        // Bind the view elements to the Question.
        public void bind(Question question) {
            tvStudentName.setText(question.getAsker().getString(Question.KEY_FULL_NAME));
            tvPriorityEmoji.setText(question.getPriority());
            tvDescription.setText(question.getText());
            tvStartTime.setText(question.getCreatedTimeAgo());

            String helpType = question.getHelpType();
            if (helpType.equals(mContext.getResources().getString(R.string.in_person))) {
                tvHelpEmoji.setText(R.string.EMOJI_IN_PERSON);
            } else if (helpType.equals(mContext.getResources().getString(R.string.written))) {
                tvHelpEmoji.setText(R.string.EMOJI_WRITTEN);
            }
        }
    }

}
