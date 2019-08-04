package com.example.helpq.model;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.helpq.R;

public class Sound {

    private static MediaPlayer player;

    public static void slideMenuSound(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_cartoon_pop_pull_finger_out_of_tube_004_35236);
        player.start();
    }

    public static void swipePages(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_cartoon_whoosh_whip_away_001_26700);
        player.start();
    }

    public static void slideBack(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_leisure_playing_card_single_slide_pick_up_20472);
        player.start();
    }

    public static void fabPopUp(Context context) {
        player = MediaPlayer.create(context, R.raw.zapsplat_cartoon_ping_twang_002_31424);
        player.start();
    }

    public static void closeWindow(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_foley_rope_thin_swish_swoosh_single_whip_001_20383);
        player.start();
    }

    public static void likeClicked(Context context) {
        player = MediaPlayer.create(context, R.raw.zapsplat_cartoon_pop_mouth_006_28806);
        player.start();
    }

    public static void delete(Context context) {
        player = MediaPlayer.create(context, R.raw.technology_laptop_notebook_delete_key_press);
        player.start();
    }

    public static void refreshPage(Context context) {
        player = MediaPlayer.create(context, R.raw.zapsplat_cartoon_slip_trip_up_001_18133);
        player.start();
    }


}
