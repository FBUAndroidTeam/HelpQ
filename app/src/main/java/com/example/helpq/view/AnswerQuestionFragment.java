package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.helpq.R;
import com.example.helpq.model.Question;

public class AnswerQuestionFragment extends DialogFragment {

    private static final String TAG = "AnswerQuestionFragment";
    private Question mQuestion;
    private Button btnSubmit;

    public static AnswerQuestionFragment newInstance(Question question) {
        AnswerQuestionFragment frag = new AnswerQuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable("Question", question);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mQuestion = getArguments().getParcelable("Question");
        return inflater.inflate(R.layout.fragment_answer_question, container, false);

    }

}
