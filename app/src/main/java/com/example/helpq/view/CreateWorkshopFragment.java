package com.example.helpq.view;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CreateWorkshopFragment extends DialogFragment {

    public static CreateWorkshopFragment newInstance(String title) {
        CreateWorkshopFragment frag = new CreateWorkshopFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
}
