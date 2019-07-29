package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.helpq.R;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.Notification;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.List;

public class AnswerQuestionFragment extends DialogFragment {

    public static final String TAG = "AnswerQuestionFragment";
    private Question mQuestion;

    // Layout fields
    private TextView tvStudent;
    private TextView tvDescription;
    private EditText etAnswer;
    private ToggleButton tbPrivate;
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
        tbPrivate = view.findViewById(R.id.tbPrivate);

        // Show soft keyboard automatically and request focus to text
        etAnswer.requestFocus();

        String studentName = User.getFullName(mQuestion.getAsker());
        tvStudent.setText(studentName + "'s question:");
        tvDescription.setText(mQuestion.getText());
        setupSubmitButton();
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((etAnswer.getText().toString().isEmpty())) {
                    Toast.makeText(getContext(),
                            R.string.edge_case_answer,
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    submitAnswer();

                    // Create and send notifications to students
                    if (!tbPrivate.isChecked()) notifyAllStudents();
                    notifyStudent();
                }
            }
        });
    }

    // Submit the answer Parse.
    private void submitAnswer() {
        mQuestion.setAnswer(etAnswer.getText().toString());
        mQuestion.setAnsweredAt(new Date(System.currentTimeMillis()));
        mQuestion.setIsArchived(true);
        mQuestion.setIsPrivate(tbPrivate.isChecked());
        mQuestion.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), R.string.success_question_answered,
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Answer submitted successfully");
                    DialogDismissListener listener = (DialogDismissListener) getTargetFragment();
                    listener.onDismiss();
                    dismiss();
                } else {
                    Log.d(TAG, "Answer failed to submit");
                    e.printStackTrace();
                }
            }
        });
    }

    // Put a notification that this question has been answered in the student's inbox.
    private void notifyStudent() {
        Notification notification = new Notification();
        notification.setUser(mQuestion.getAsker());
        notification.setTab(3);
        notification.saveInBackground();
    }

    // Put a notification that this question has been answered on all students' boards.
    private void notifyAllStudents() {
        ParseQuery<ParseUser> studentQuery = ParseUser.getQuery();
        studentQuery.whereEqualTo(User.KEY_ADMIN_NAME, ParseUser.getCurrentUser().getUsername());
        studentQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error with query");
                    e.printStackTrace();
                    return;
                }

                // Add Notification to student's board tab.
                for (ParseUser student : objects) {
                    Notification notification = new Notification();
                    notification.setUser(student);
                    notification.setTab(4);
                    notification.saveInBackground();
                }
            }
        });
    }
}