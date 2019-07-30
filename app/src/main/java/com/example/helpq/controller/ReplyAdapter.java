package com.example.helpq.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.model.Reply;
import com.example.helpq.model.User;
import com.facebook.login.widget.ProfilePictureView;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private List<Reply> mReplies;
    private Context mContext;

    public ReplyAdapter(Context context, List<Reply> replies) {
        mContext = context;
        mReplies = replies;
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
    }

    @Override
    public int getItemCount() {
        return mReplies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ProfilePictureView ppvPicture;
        private TextView tvFullName;
        private TextView tvReply;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ppvPicture = itemView.findViewById(R.id.ppvPicture);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvReply = itemView.findViewById(R.id.tvReply);
        }

        public void bind(Reply reply) {
            ppvPicture.setProfileId(User.getProfilePicture(reply.getUser()));
            tvReply.setText(reply.getText());
            tvFullName.setText(User.getFullName(reply.getUser()));
        }
    }
}
