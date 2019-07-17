package com.example.helpq.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.helpq.R;

public class StudentQueueFragment extends Fragment {

    private FloatingActionButton fabCreateQuestion;
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String TAG = "StudentQueueFragment";
    FragmentManager fragmentManager;

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

        fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.flStudentQueueContainer, new QueueFragment(), QueueFragment.TAG).commit();

        fabCreateQuestion = view.findViewById(R.id.fabCreateQuestion);
        fabCreateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                CreateQuestionFragment CreateQuestionFragment = com.example.helpq.view.CreateQuestionFragment.newInstance("Some Title");
                CreateQuestionFragment.show(fm, "fragment_create_question");
            }
        });
        return view;
    }


}