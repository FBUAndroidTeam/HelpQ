package com.example.helpq.view.admin_views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.helpq.R;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.Notification;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.example.helpq.model.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateWorkshopFragment extends DialogFragment {

    public static final String TAG = "CreateWorkshopFragment";

    private EditText etTitle;
    private TimePicker tpTime;
    private EditText etLocation;
    private CalendarView cvDate;
    private Button btnSubmit;
    private int mMonth;
    private int mYear;
    private int mDay;
    private boolean isCalendarClicked;
    private ImageButton ibCancel;

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
        etTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        tpTime = view.findViewById(R.id.tpTime);
        tpTime.setIs24HourView(true);
        etLocation = view.findViewById(R.id.etLocation);
        etLocation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        cvDate = view.findViewById(R.id.cvDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        ibCancel = view.findViewById(R.id.ibCancel);
        setCancelButton();
        isCalendarClicked = false;

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "New Workshop");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to text
        etTitle.requestFocus();
        getDialog()
                .getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        cvDate.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                mMonth = month;
                mDay = dayOfMonth;
                mYear = year;
                isCalendarClicked = true;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etTitle.getText().toString().isEmpty() &&
                        !etLocation.getText().toString().isEmpty()) {
                    createWorkshop();
                } else {
                    Toast.makeText(getContext(), R.string.edge_case_enter_location,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setCancelButton() {
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sound.closeDialogWindow(getContext());
                dismiss();
            }
        });
    }

    private void createWorkshop() {
        Workshop workshop = new Workshop();
        workshop.setAttendees(new JSONArray());
        workshop.setLocation(etLocation.getText().toString());
        workshop.setCreator(ParseUser.getCurrentUser());
        workshop.setTitle(etTitle.getText().toString());
        workshop.setStartTime(createStartTimeDate());

        Date currTime = new Date(System.currentTimeMillis());
        if (currTime.compareTo(workshop.getStartTime()) < 0) {
            workshop.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        DialogDismissListener listener =
                                (DialogDismissListener) getTargetFragment();
                        Toast.makeText(getContext(),
                                R.string.success_create_workshop,
                                Toast.LENGTH_LONG).show();
                        listener.onDismiss();
                        dismiss();
                    } else {
                        Log.d(TAG, "Create workshop failed");
                        e.printStackTrace();
                    }
                }
            });
            notifyAllStudents();
        } else {
            Toast.makeText(getContext(), R.string.edge_case_wrong_date_workshop,
                    Toast.LENGTH_LONG).show();
        }
    }


    private Date createStartTimeDate() {
        Calendar cal = Calendar.getInstance();
        if(isCalendarClicked) {
            cal.set(Calendar.YEAR, mYear);
            cal.set(Calendar.MONTH, mMonth);
            cal.set(Calendar.DAY_OF_MONTH, mDay);
        }
        cal.set(Calendar.HOUR_OF_DAY, tpTime.getCurrentHour());
        cal.set(Calendar.MINUTE, tpTime.getCurrentMinute());
        return cal.getTime();
    }

    // Send notifications to all students that this workshops has been created.
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
                    notification.setTab(1);
                    notification.saveInBackground();
                }
            }
        });
    }
}
