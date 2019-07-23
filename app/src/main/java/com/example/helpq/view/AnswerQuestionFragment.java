package com.example.helpq.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.Date;

public class AnswerQuestionFragment extends DialogFragment {

    private static final String TAG = "AnswerQuestionFragment";
    private Question mQuestion;

    // Layout fields
    private TextView tvStudent;
    private TextView tvDescription;
    private EditText etAnswer;
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
        return inflater.inflate(R.layout.fragment_answer_question, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get Question from Bundle
        mQuestion = getArguments().getParcelable("Question");

        tvStudent = view.findViewById(R.id.tvStudent);
        tvDescription = view.findViewById(R.id.tvQuestion);
        etAnswer = view.findViewById(R.id.etAnswer);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Show soft keyboard automatically and request focus to text
        etAnswer.requestFocus();

        String studentName = User.getFullName(mQuestion.getAsker());
        tvStudent.setText(studentName + "'s question:");
        tvDescription.setText(mQuestion.getText());
        submitAnswer();
    }

    // Submit the answer Parse.
    private void submitAnswer() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((etAnswer.getText().toString().isEmpty())) {
                    Toast.makeText(getContext(),
                            R.string.edge_case_answer,
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    mQuestion.setAnswer(etAnswer.getText().toString());
                    mQuestion.setAnsweredAt(new Date(System.currentTimeMillis()));
                    mQuestion.setIsArchived(true);
                    mQuestion.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getContext(), R.string.success_question_answered,
                                        Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Answer submitted successfully");
                                dismiss();
                            } else {
                                Log.d(TAG, "Answer failed to submit");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
