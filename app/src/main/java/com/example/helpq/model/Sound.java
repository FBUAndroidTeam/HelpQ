package com.example.helpq.model;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.helpq.R;

public class Sound {

    public static void openSlideMenu(Context context) {
        MediaPlayer.create(context, R.raw.zapsplat_cartoon_pop_pull_finger_out_of_tube_004_35236)
                .start();
    }

    public static void changeTabs(Context context) {
        MediaPlayer.create(context, R.raw.zapsplat_cartoon_whoosh_whip_away_001_26700).start();
    }

    public static void closeSlideMenu(Context context) {
        MediaPlayer.create(context, R.raw.zapsplat_leisure_playing_card_single_slide_pick_up_20472)
                .start();
    }

    public static void openDialogWindow(Context context) {
        MediaPlayer.create(context, R.raw.zapsplat_cartoon_ping_twang_002_31424).start();
    }

    public static void closeDialogWindow(Context context) {
        MediaPlayer.create(context,
                R.raw.zapsplat_foley_rope_thin_swish_swoosh_single_whip_001_20383).start();
    }

    public static void likeClicked(Context context) {
        MediaPlayer.create(context, R.raw.zapsplat_cartoon_pop_mouth_006_28806).start();
    }

    public static void delete(Context context) {
        MediaPlayer.create(context, R.raw.technology_laptop_notebook_delete_key_press).start();
    }

    public static void refreshPage(Context context) {
        MediaPlayer.create(context, R.raw.zapsplat_cartoon_slip_trip_up_001_18133).start();
    }

}
