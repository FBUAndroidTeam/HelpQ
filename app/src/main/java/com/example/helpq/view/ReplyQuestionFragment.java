package com.example.helpq.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.controller.ReplyAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.Question;
import com.example.helpq.model.Reply;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.facebook.drawee.view.SimpleDraweeView;
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
    private TextView tvNoComments;
    private SimpleDraweeView ppvAskerPic;
    private RecyclerView rvReplies;
    private SimpleDraweeView ppvProfilePic;
    private EditText etReply;
    private Question mQuestion;
    private Button btnReply;
    private ReplyAdapter adapter;
    private List<Reply> mReplies;
    private ImageButton ibCancel;
    private ProgressBar pbLoading;

    public static ReplyQuestionFragment newInstance(Question question) {
        ReplyQuestionFragment frag = new ReplyQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("Question", question);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply_question, container, false);
    }

    //ensures that dialog fragment keeps same width and height while in use, also allows the
    //question to stay in view when a student is writing a comment
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    //deals with configuration change by switching to new horizontal layout; recreates itself
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            FragmentManager m = getFragmentManager();
            FragmentTransaction transaction = m.beginTransaction();
            ReplyQuestionFragment frag =
                    ReplyQuestionFragment.newInstance((Question) getArguments().get("Question"));
            transaction.remove(this);
            frag.show(transaction, TAG);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvQuestion = view.findViewById(R.id.tvQuestion);
        ppvAskerPic = view.findViewById(R.id.ivProfilePic);
        ppvProfilePic = view.findViewById(R.id.ivMyProfilePic);
        btnReply = view.findViewById(R.id.btnReply);
        tvNoComments = view.findViewById(R.id.tvNoComments);
        etReply = view.findViewById(R.id.etReply);
        ibCancel = view.findViewById(R.id.ibCancel);
        setCancelButton();

        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);

        mReplies = new ArrayList<>();
        rvReplies = view.findViewById(R.id.rvReplies);
        mQuestion = getArguments().getParcelable("Question");
        adapter = new ReplyAdapter(getContext(), mReplies, mQuestion);
        rvReplies.setAdapter(adapter);
        populateReplyList();
        rvReplies.setLayoutManager(new LinearLayoutManager(getContext()));

        tvFullName.setText(User.getFullName(mQuestion.getAsker()));
        tvQuestion.setText(mQuestion.getText());

        ppvAskerPic.setImageURI(Uri.parse("http://graph.facebook.com/"+
                User.getProfilePicture(mQuestion.getAsker())+"/picture?type=large"));
        ppvProfilePic.setImageURI(Uri.parse("http://graph.facebook.com/"+
                User.getProfilePicture(ParseUser.getCurrentUser())+"/picture?type=large"));
        submitReply();
        if(User.isAdmin(ParseUser.getCurrentUser())) {
            disableReply();
        }
    }

    private void setCancelButton() {
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sound.closeDialogWindow(getContext());
                if(adapter.anyNewVerifications()) {
                    DialogDismissListener listener = (DialogDismissListener) getTargetFragment();
                    listener.onDismiss();
                }
                dismiss();
            }
        });
    }

    private void disableReply() {
        ppvProfilePic.setVisibility(View.GONE);
        etReply.setVisibility(View.GONE);
        btnReply.setVisibility(View.GONE);
    }

    private void submitReply() {
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                btnReply.setEnabled(false);
                if(!etReply.getText().toString().equals("")) {
                    Sound.actionDone(getContext());
                    final Reply newReply = new Reply();
                    newReply.setText(etReply.getText().toString());
                    newReply.setQuestion(mQuestion);
                    newReply.setUser(ParseUser.getCurrentUser());
                    newReply.setVerified(false);
                    newReply.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                etReply.setText("");
                                if(getContext() != null) {
                                    ((InputMethodManager) getContext()
                                            .getSystemService(Activity.INPUT_METHOD_SERVICE))
                                            .hideSoftInputFromWindow(v.getWindowToken(), 0);
                                }
                                mReplies.add(newReply);
                                adapter.notifyItemInserted(mReplies.size() - 1);
                                rvReplies.getLayoutManager().scrollToPosition(mReplies.size() - 1);
                                tvNoComments.setVisibility(tvNoComments.INVISIBLE);
                                btnReply.setEnabled(true);
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
                    btnReply.setEnabled(true);
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
                    pbLoading.setVisibility(View.INVISIBLE);
                } else {
                    Log.d(TAG, "error querying replies");
                }
                if(mReplies.isEmpty()) {
                    tvNoComments.setVisibility(tvNoComments.VISIBLE);
                }
            }
        });
    }
}
