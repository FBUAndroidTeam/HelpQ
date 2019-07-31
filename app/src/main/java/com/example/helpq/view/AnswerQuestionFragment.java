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
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.User;
import com.example.helpq.model.WaitTime;
import com.example.helpq.model.WaitTimeHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

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
                    if (!tbPrivate.isChecked()) {
                        notifyAllStudents();
                        notifyLikers();
                    }
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
        updateWaitTime();
    }

    // Put a notification that this question has been answered in the student's inbox.
    private void notifyStudent() {
        Notification notification = new Notification();
        notification.setUser(mQuestion.getAsker());
        notification.setTab(3);
        notification.saveInBackground();
    }

    // Put a notification that this question has been answered in the inboxes of students who
    // have liked this question.
    private void notifyLikers() {
        JSONArray likes = mQuestion.getLikes();
        if (likes != null) {
            for (int i = 0; i < likes.length(); i++) {
                try {
                    String objectId = likes.getJSONObject(i).getString("objectId");
                    ParseQuery<ParseUser> query = QueryFactory.UserQuery.getUserByObjectId(objectId);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error querying for liker of this question");
                                return;
                            }
                            if (objects.size() > 1) {
                                Log.d(TAG, "All users should have unique objectId!");
                            }
                            Notification notification = new Notification();
                            notification.setUser(objects.get(0));
                            notification.setTab(3);
                            notification.saveInBackground();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void updateWaitTime() {
        ParseUser user = ParseUser.getCurrentUser();
        String adminName = "";
        if (User.isAdmin(user)) adminName = user.getUsername();
        else adminName = User.getAdminName(user);
        final ParseQuery<WaitTime> query = new ParseQuery<WaitTime>(WaitTime.class);
        query.whereEqualTo(WaitTime.KEY_ADMIN_NAME, adminName);
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
                updateWaitTimeByPriority(waitTime);
                waitTime.saveInBackground();
            }
        });
    }

    private void updateWaitTimeByPriority(WaitTime waitTime) {
        WaitTimeHelper helper = new WaitTimeHelper(getContext());
        long newWaitTime;
        switch (mQuestion.getPriority()) {

            case WaitTimeHelper.BLOCKING:
                newWaitTime = helper.updateWaitTime(waitTime.getBlockingTime(),
                        waitTime.getBlockingSize(), mQuestion.getTimeDifference());
                waitTime.setBlockingTime(newWaitTime);
                waitTime.setBlockingSize(waitTime.getBlockingSize() + 1);
                break;

            case WaitTimeHelper.STRETCH:
                newWaitTime = helper.updateWaitTime(waitTime.getStretchTime(),
                        waitTime.getStretchSize(), mQuestion.getTimeDifference());
                waitTime.setStretchTime(newWaitTime);
                waitTime.setStretchSize(waitTime.getStretchSize() + 1);
                break;

            case WaitTimeHelper.CURIOSITY:
                newWaitTime = helper.updateWaitTime(waitTime.getCuriosityTime(),
                        waitTime.getCuriositySize(), mQuestion.getTimeDifference());
                waitTime.setCuriosityTime(newWaitTime);
                waitTime.setCuriositySize(waitTime.getCuriositySize() + 1);
                break;
        }
    }
}