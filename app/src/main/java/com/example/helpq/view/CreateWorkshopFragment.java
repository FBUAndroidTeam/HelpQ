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
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.Workshop;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.Date;

public class CreateWorkshopFragment extends DialogFragment {

    public static final String TAG = "CreateWorkshopFragment";

    private EditText etTitle;
    private TimePicker tpTime;
    private EditText etLocation;
    private CalendarView cvDate;
    private Button btnSubmit;

    public static CreateWorkshopFragment newInstance(String title) {
        CreateWorkshopFragment frag = new CreateWorkshopFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_workshop, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etTitle = view.findViewById(R.id.etTitle);
        tpTime = view.findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);
        etLocation = view.findViewById(R.id.etLocation);
        cvDate = view.findViewById(R.id.cvDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "New Workshop");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to text
        etTitle.requestFocus();
        getDialog()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etTitle.getText().equals("") && !etLocation.getText().equals("")) {
                    createWorkshop();
                } else {
                    Toast.makeText(getContext(), "Please enter a title and/or location",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createWorkshop() {
        Workshop workshop = new Workshop();
        workshop.setAttendees(new JSONArray());
        workshop.setLocation(etLocation.getText().toString());
        workshop.setCreator(ParseUser.getCurrentUser());
        workshop.setTitle(etTitle.getText().toString());
        workshop.setStartTime(extractDate());
        workshop.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(getContext(), "Workshop created", Toast.LENGTH_LONG).show();
                    dismiss();
                } else {
                    Log.d(TAG, "Create workshop failed");
                    e.printStackTrace();
                }
            }
        });
    }

    private Date extractDate() {
        Date calenderDate = new Date(cvDate.getDate());
        int year = calenderDate.getYear();
        int month = calenderDate.getMonth();
        int day = calenderDate.getDate();
        int hour = tpTime.getCurrentHour();
        int min = tpTime.getCurrentMinute();
        return new Date(year, month, day, hour, min, 0);
    }
}