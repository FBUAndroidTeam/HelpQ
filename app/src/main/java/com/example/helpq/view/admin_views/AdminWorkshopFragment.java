package com.example.helpq.view.admin_views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.helpq.R;
import com.example.helpq.controller.AdminWorkshopAdapter;
import com.example.helpq.model.DialogDismissListener;
import com.example.helpq.model.QueryFactory;
import com.example.helpq.model.Sound;
import com.example.helpq.model.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class AdminWorkshopFragment extends Fragment implements DialogDismissListener {
    public static final String TAG = "AdminWorkshopFragment";
    private TextView tvNotice;
    private RecyclerView rvAdminWorkshops;
    private List<Workshop> mWorkshops;
    private AdminWorkshopAdapter mAdapter;
    private FloatingActionButton fabAddWorkshop;
    private SwipeRefreshLayout swipeContainer;
    private FragmentManager fm;
    private ProgressBar pbLoading;
    private Snackbar mSnackbar;
    private Workshop workshopToDelete;
    private boolean undoClicked;

    public static AdminWorkshopFragment newInstance() {
        return new AdminWorkshopFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_workshop, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvNotice = view.findViewById(R.id.tvNotice);
        tvNotice.setVisibility(View.GONE);
        rvAdminWorkshops = view.findViewById(R.id.rvAdminWorkshops);
        mWorkshops = new ArrayList<>();
        mAdapter = new AdminWorkshopAdapter(getContext(), mWorkshops, this);
        rvAdminWorkshops.setAdapter(mAdapter);
        rvAdminWorkshops.setLayoutManager(new LinearLayoutManager(getContext()));
        fabAddWorkshop = view.findViewById(R.id.fabAddWorkshop);
        pbLoading = view.findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.VISIBLE);
        fm = getFragmentManager();
        fabAddWorkshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sound.openDialogWindow(getContext());
                CreateWorkshopFragment createWorkshopFragment =
                        CreateWorkshopFragment.newInstance("Some Title");
                createWorkshopFragment.setTargetFragment(AdminWorkshopFragment.this,
                        300);
                createWorkshopFragment.show(fm, createWorkshopFragment.TAG);

            }
        });

        queryWorkshops();
        setupSwipeRefreshing(view);
    }

    // Handle logic for Swipe to Refresh.
    private void setupSwipeRefreshing(@NonNull View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Sound.refreshPage(getContext());
                fetchWorkshopsAsync();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setProgressBackgroundColorSchemeColor(
                getContext().getResources().getColor(R.color.colorAccent));
        swipeContainer.setColorSchemeResources(R.color.colorMint,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Reload workshops.
    protected void fetchWorkshopsAsync() {
        if(mSnackbar != null && !undoClicked) {
            mSnackbar.dismiss();
            mAdapter.deleteWorkshop(workshopToDelete);
            mSnackbar = null;
        }
        mAdapter.clear();
        queryWorkshops();
        swipeContainer.setRefreshing(false);
    }

    private void queryWorkshops() {
        ParseQuery<Workshop> query = QueryFactory.Workshops.getWorkshopsForAdmin();
        query.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if(e == null) {
                    mWorkshops.addAll(objects);
                    mAdapter.notifyDataSetChanged();
                    pbLoading.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "mAdapter notified");
                } else {
                    e.printStackTrace();
                }
                isPageEmpty();
                runLayoutAnimation();
            }
        });
    }

    private void isPageEmpty() {
        if(mWorkshops.size() == 0) {
            tvNotice.setVisibility(View.VISIBLE);
        } else {
            tvNotice.setVisibility(View.GONE);
        }
    }

    public void createSnackbar(final int adapterpos, final Workshop workshop){
        undoClicked = false;
        View.OnClickListener myOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!undoClicked) {
                    mWorkshops.add(adapterpos, workshop);
                    mAdapter.notifyItemInserted(adapterpos);
                    rvAdminWorkshops.scrollToPosition(adapterpos);
                    undoClicked = true;
                }
            }
        };
        workshopToDelete = workshop;
         mSnackbar = Snackbar.make(getView(), R.string.workshop_deleted, Snackbar.LENGTH_LONG);
         mSnackbar.setAction(R.string.snackbar_action, myOnClickListener).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if(event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT
                                || event == DISMISS_EVENT_CONSECUTIVE) {
                            mAdapter.deleteWorkshop(workshop);
                        }
                    }
                })
                .show();
    }

    // Animate RecyclerView items falling onto the screen.
    protected void runLayoutAnimation() {
        final Context context = rvAdminWorkshops.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_enter);

        rvAdminWorkshops.setLayoutAnimation(controller);
        mAdapter.notifyDataSetChanged();
        rvAdminWorkshops.scheduleLayoutAnimation();
    }

    @Override
    public void onDismiss() {
        fetchWorkshopsAsync();
    }
}