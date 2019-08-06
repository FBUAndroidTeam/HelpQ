package com.example.helpq.controller;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.Reply;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.example.helpq.view.ReplyQuestionFragment;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private List<Reply> mReplies;
    private Context mContext;
    private Question mQuestion;
    private boolean newVerifications;

    public ReplyAdapter(Context context, List<Reply> replies, Question question) {
        mContext = context;
        mReplies = replies;
        mQuestion = question;
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_reply, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(mReplies.get(i));
        newVerifications = false;
    }

    public boolean anyNewVerifications() {
        return newVerifications;
    }

    @Override
    public int getItemCount() {
        return mReplies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView ppvPicture;
        private TextView tvFullName;
        private TextView tvReply;
        private ImageView ivVerified;
        private TextView tvVerification;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ppvPicture = itemView.findViewById(R.id.ppvPicture);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvReply = itemView.findViewById(R.id.tvReply);
            ivVerified = itemView.findViewById(R.id.ivVerified);
            tvVerification = itemView.findViewById(R.id.tvVerification);
        }

        public void bind(Reply reply) {
            ppvPicture.setImageURI(Uri.parse("http://graph.facebook.com/"+
                    User.getProfilePicture(reply.getUser())+"/picture?type=large"));
            tvReply.setText(reply.getText());
            tvFullName.setText(User.getFullName(reply.getUser()));
            if(reply.getVerification() || User.isAdmin(ParseUser.getCurrentUser())) {
                ivVerified.setVisibility(View.VISIBLE);
                if(User.isAdmin(ParseUser.getCurrentUser()) && !mQuestion.getIsArchived()) {
                    verification(reply);
                }
                if(reply.getVerification()) {
                    ivVerified.setColorFilter(ContextCompat
                            .getColor(mContext, R.color.colorAccent));
                    tvVerification.setVisibility(View.VISIBLE);
                } else {
                    ivVerified.clearColorFilter();
                    tvVerification.setVisibility(View.GONE);
                }
            } else {
                ivVerified.setVisibility(View.GONE);
                tvVerification.setVisibility(View.GONE);
                ivVerified.clearColorFilter();
            }
        }

        private void verification(final Reply reply) {
            ivVerified.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!reply.getVerification()) {
                        Sound.actionDone(mContext);
                        ivVerified.setColorFilter(ContextCompat
                                .getColor(mContext, R.color.colorAccent));
                        tvVerification.setVisibility(View.VISIBLE);
                        reply.setVerified(true);
                        saveQuestionChanges();
                        newVerifications = true;
                    } else { //unverifying
                        if(anyRepliesVerified(reply)) {
                            ivVerified.clearColorFilter();
                            reply.setVerified(false);
                            tvVerification.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(mContext,
                                    mContext.getResources().getString(R.string.at_least_one_verify),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    reply.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Log.d(ReplyQuestionFragment.TAG, "Verification saved");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    private void saveQuestionChanges() {
        mQuestion.setAnswer(mContext.getResources().getString(R.string.see_student_reply));
        mQuestion.setIsArchived(true);
        mQuestion.setIsPrivate(false);
        mQuestion.setAnsweredAt(new Date(System.currentTimeMillis()));
        mQuestion.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.d(ReplyQuestionFragment.TAG, "question verification saved");
                } else {
                    Log.d(ReplyQuestionFragment.TAG, "question verification fail");
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean anyRepliesVerified(Reply targetReply) {
        for(int i = 0; i < mReplies.size(); i++) {
            Reply reply = mReplies.get(i);
            if(reply.getVerification() && reply != targetReply) {
                return true;
            }
        }
        return false;
    }
}
