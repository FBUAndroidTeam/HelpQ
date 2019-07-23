package com.example.helpq.view;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;

public class StudentQueueFragment extends Fragment {

    private FloatingActionButton fabCreateQuestion;
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "StudentQueueFragment";
    private QueueFragment mQueueFragment;

    public static StudentQueueFragment newInstance() {
        return new StudentQueueFragment();
    }

    public static StudentQueueFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        StudentQueueFragment fragment = new StudentQueueFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_queue, parent, false);
        mQueueFragment = new QueueFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.flStudentQueueContainer, mQueueFragment, QueueFragment.TAG)
                .commit();

        fabCreateQuestion = view.findViewById(R.id.fabCreateQuestion);
        fabCreateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                CreateQuestionFragment createQuestionFragment =
                        CreateQuestionFragment.newInstance("Some Title");
                createQuestionFragment.setTargetFragment(mQueueFragment, 300);
                createQuestionFragment.show(fm, CreateQuestionFragment.TAG);
            }
        });
        return view;
    }
}