package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.controller.ReplyAdapter;
import com.example.helpq.model.Question;
import com.example.helpq.model.Reply;
import com.example.helpq.model.User;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ReplyQuestionFragment extends DialogFragment {

    public static final String TAG = "ReplyQuestionFragment";
    private TextView tvFullName;
    private TextView tvQuestion;
    private ProfilePictureView ppvAskerPic;
    private RecyclerView rvReplies;
    private ProfilePictureView ppvProfilePic;
    private EditText etReply;
    private Question mQuestion;
    private Button btnReply;
    private ReplyAdapter adapter;
    private List<Reply> mReplies;

    public static ReplyQuestionFragment newInstance(Question question) {
        ReplyQuestionFragment frag = new ReplyQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("Question", question);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        ppvAskerPic = view.findViewById(R.id.ivProfilePic);
        ppvProfilePic = view.findViewById(R.id.ivMyProfilePic);
        btnReply = view.findViewById(R.id.btnReply);
        etReply = view.findViewById(R.id.etReply);

        mReplies = new ArrayList<>();
        rvReplies = view.findViewById(R.id.rvReplies);
        mQuestion = getArguments().getParcelable("Question");
        adapter = new ReplyAdapter(getContext(), mReplies);
        rvReplies.setAdapter(adapter);
        populateReplyList();
        rvReplies.setLayoutManager(new LinearLayoutManager(getContext()));

        tvFullName.setText(User.getFullName(mQuestion.getAsker()));
        tvQuestion.setText(mQuestion.getText());
        ppvAskerPic.setProfileId(User.getProfilePicture(mQuestion.getAsker()));
        ppvProfilePic.setProfileId(User.getProfilePicture(ParseUser.getCurrentUser()));
        submitReply();
    }

    private void submitReply() {
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etReply.getText().toString().equals("")) {
                    final Reply newReply = new Reply();
                    newReply.setText(etReply.getText().toString());
                    newReply.setQuestion(mQuestion);
                    newReply.setUser(ParseUser.getCurrentUser());
                    newReply.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                etReply.setText("");
                                //hide keyboard
                                mReplies.add(newReply);
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "error creating reply");
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(),
                            getContext().getResources().getString(R.string.empty_reply),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void populateReplyList() {
        ParseQuery<Reply> query = new ParseQuery<Reply>(Reply.class);
        query.whereEqualTo("question", mQuestion);
        query.include("user");
        query.findInBackground(new FindCallback<Reply>() {
            @Override
            public void done(List<Reply> objects, ParseException e) {
                if(e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        mReplies.add(objects.get(i));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "error querying replies");
                }
            }
        });
    }
}
