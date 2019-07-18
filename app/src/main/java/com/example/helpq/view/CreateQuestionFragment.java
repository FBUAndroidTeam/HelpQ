package com.example.helpq.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.helpq.R;
import com.example.helpq.model.Question;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CreateQuestionFragment extends DialogFragment {

    private static final String TAG = "CreateQuestionFragment";
    private EditText etQuestion;
    private ToggleButton tbBlocker;
    private ToggleButton tbStretch;
    private ToggleButton tbExplanation;
    private ToggleButton toggleSelected;
    private Button btnSubmit;
    private TextView tvStudent;

    public static CreateQuestionFragment newInstance(String title) {
        CreateQuestionFragment frag = new CreateQuestionFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_question, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tbBlocker = view.findViewById(R.id.tbBlocker);
        tbExplanation = view.findViewById(R.id.tbExplanation);
        tbStretch = view.findViewById(R.id.tbStretch);
        etQuestion = (EditText) view.findViewById(R.id.etQuestion);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvStudent = view.findViewById(R.id.tvStudent);

        tvStudent.setText(ParseUser.getCurrentUser().getString("fullName"));

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "New Question");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to text
        etQuestion.requestFocus();
        getDialog()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        toggleButtons();
        submitQuestion();
    }

    // ensures only one toggle button is selected
    private void toggleButtons() {
        tbBlocker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbExplanation.setChecked(false);
                    tbStretch.setChecked(false);
                    toggleSelected = tbBlocker;
                }
            }
        });
        tbStretch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbBlocker.setChecked(false);
                    tbExplanation.setChecked(false);
                    toggleSelected = tbStretch;
                }
            }
        });
        tbExplanation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tbBlocker.setChecked(false);
                    tbStretch.setChecked(false);
                    toggleSelected = tbExplanation;
                }
            }
        });
    }

    //method that deals with submitting question to parse
    private void submitQuestion() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etQuestion.getText().equals("") && toggleSelected != null) {
                    Question newQuestion = new Question();
                    newQuestion.setText(etQuestion.getText().toString());
                    newQuestion.setAsker(ParseUser.getCurrentUser());
                    newQuestion.setIsArchived(false);
                    newQuestion.setPriority(toggleSelected.getText().toString());
                    newQuestion.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Log.d(TAG, "Question created successfully");
                                dismiss();
                            } else {
                                Log.d(TAG, "Question failed to create");
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(),
                            "Please enter a question and/or priority level",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}
