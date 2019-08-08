package com.example.helpq.view.admin_views;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.AdminIndividualQuestionsAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Question;
import com.example.helpq.model.Sound;
import com.example.helpq.model.User;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AdminIndividualQuestionsFragment extends DialogFragment
        implements DialogDismissListener {

    public static final String TAG = "AdminIndividualQuestion";
    public static String KEY_USERNAME = "username";
    private RecyclerView rvQuestions;
    private List<Question> mQuestions;
    private AdminIndividualQuestionsAdapter mAdapter;
    private SimpleDraweeView sdvProfilePic;
    private TextView tvName;
    private TextView tvNoQuestions;
    private ParseUser mStudent;
    private ImageButton ibCancel;
    private boolean changesMade;

    // Swipe to refresh and progress bar
    private SwipeRefreshLayout mSwipeContainer;
    private ProgressBar pbLoading;

    public static AdminIndividualQuestionsFragment newInstance(ParseUser student) {
        AdminIndividualQuestionsFragment frag = new AdminIndividualQuestionsFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USERNAME, student);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT ||
                newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentManager manager = getFragmentManager();
            AdminIndividualQuestionsFragment frag = AdminIndividualQuestionsFragment.newInstance(mStudent);
            frag.show(manager.beginTransaction().remove(this), TAG);
        }
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

        changesMade = false;

        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);

        rvQuestions = view.findViewById(R.id.rvIndividualQuestions);
        sdvProfilePic = view.findViewById(R.id.ivProfilePic);
        tvName = view.findViewById(R.id.tvFullName);
        tvNoQuestions = view.findViewById(R.id.tvNoQuestions);
        mQuestions = new ArrayList<>();
        mStudent = getArguments().getParcelable(KEY_USERNAME);
        mAdapter = new AdminIndividualQuestionsAdapter(getContext(), mQuestions, this);
        rvQuestions.setAdapter(mAdapter);
        rvQuestions.setLayoutManager(new LinearLayoutManager(getContext()));

        ibCancel = view.findViewById(R.id.ibCancel);
        setCancelButton();


        tvName.setText(User.getFullName(mStudent));
        sdvProfilePic.setImageURI(Uri.parse("http://graph.facebook.com/"+
                User.getProfilePicture(mStudent)+"/picture?type=large"));
        populateQuestions();
        setupSwipeRefreshing(view);
    }

    // Ensures that dialog fragment keeps same width and height while in use
    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        mSwipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Sound.refreshPage(getContext());
                fetchQuestionsAsync();
            }
        });
        // Configure the refreshing colors
        mSwipeContainer.setProgressBackgroundColorSchemeColor(
                getContext().getResources().getColor(R.color.colorAccent));
        mSwipeContainer.setColorSchemeResources(R.color.colorMint,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setCancelButton() {
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sound.closeDialogWindow(getContext());
                if(changesMade) {
                    DialogDismissListener listener = (DialogDismissListener) getTargetFragment();
                    listener.onDismiss();
                }
                dismiss();
            }
        });
    }

    // Refresh the queue, and load questions.
    public void fetchQuestionsAsync() {
        mAdapter.clear();
        tvNoQuestions.setVisibility(View.INVISIBLE);
        populateQuestions();
        mSwipeContainer.setRefreshing(false);
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
                    pbLoading.setVisibility(View.INVISIBLE);
                    if(mQuestions.isEmpty()) {
                        tvNoQuestions.setVisibility(View.VISIBLE);
                    }
                    runLayoutAnimation();
                } else {
                    Log.d(TAG, "error querying replies");
                }
            }
        });
    }

    // Animate RecyclerView items falling onto the screen.
    protected void runLayoutAnimation() {
        final Context context = rvQuestions.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_enter);

        rvQuestions.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        rvQuestions.scheduleLayoutAnimation();
    }

    @Override
    public void onDismiss() {
        fetchQuestionsAsync();
        changesMade = true;
    }
}
