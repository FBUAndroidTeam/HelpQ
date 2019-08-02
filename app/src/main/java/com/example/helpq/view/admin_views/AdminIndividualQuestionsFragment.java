package com.example.helpq.view.admin_views;

import android.net.Uri;
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
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.AdminIndividualQuestionsAdapter;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminIndividualQuestionsFragment extends DialogFragment {

    public static final String TAG = "AdminIndividualQuestion";
    public static String KEY_USERNAME = "username";
    private RecyclerView rvQuestions;
    private List<Question> mQuestions;
    private AdminIndividualQuestionsAdapter mAdapter;
    private SimpleDraweeView sdvProfilePic;
    private TextView tvName;
    private ParseUser mStudent;

    public static AdminIndividualQuestionsFragment newInstance(ParseUser student) {
        AdminIndividualQuestionsFragment frag = new AdminIndividualQuestionsFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USERNAME, student);
        frag.setArguments(args);
        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_individual_questions,
                container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvQuestions = view.findViewById(R.id.rvIndividualQuestions);
        sdvProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvFullName);
        mQuestions = new ArrayList<>();
        mStudent = getArguments().getParcelable(KEY_USERNAME);
        mAdapter = new AdminIndividualQuestionsAdapter(getContext(), mQuestions, this);
        rvQuestions.setAdapter(mAdapter);
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

        tvName.setText(User.getFullName(mStudent));
        sdvProfilePic.setImageURI(Uri.parse("http://graph.facebook.com/"+
                User.getProfilePicture(mStudent)+"/picture?type=large"));
        populateQuestions();
    }

    private void populateQuestions(){
        ParseQuery<Question> query = QueryFactory.Questions.getQuestionsForQueue();
        query.whereEqualTo("student", mStudent);
        query.findInBackground(new FindCallback<Question>() {
            @Override
            public void done(List<Question> objects, ParseException e) {
                if(e == null) {
                    mQuestions.addAll(objects);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "error querying replies");
                }
            }
        });
    }
}
