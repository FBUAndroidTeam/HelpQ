package com.example.helpq.model;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.helpq.R;

public class Sound {

    private static MediaPlayer player;

    public static void openSlideMenu(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_cartoon_pop_pull_finger_out_of_tube_004_35236);
        playSound(context);
    }

    public static void changeTabs(Context context) {
        player = MediaPlayer.create(context, R.raw.zapsplat_cartoon_whoosh_whip_away_001_26700);
        playSound(context);
    }

    public static void closeSlideMenu(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_leisure_playing_card_single_slide_pick_up_20472);
        playSound(context);
    }

    public static void openDialogWindow(Context context) {
        player = MediaPlayer.create(context, R.raw.zapsplat_cartoon_ping_twang_002_31424);
        playSound(context);
    }

    public static void closeDialogWindow(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_foley_rope_thin_swish_swoosh_single_whip_001_20383);
        playSound(context);
    }

    public static void likeClicked(Context context) {
        player = MediaPlayer.create(context, R.raw.zapsplat_cartoon_pop_mouth_006_28806);
        playSound(context);
    }

    public static void delete(Context context) {
        player = MediaPlayer.create(context, R.raw.technology_laptop_notebook_delete_key_press);
        playSound(context);
    }

    public static void refreshPage(Context context) {
        player = MediaPlayer.create(context, R.raw.zapsplat_cartoon_slip_trip_up_001_18133);
        playSound(context);
    }

    public static void actionDone(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_multimedia_notification_chime_bell_002_26402);
        playSound(context);
    }

    public static void actionUndo(Context context) {
        player = MediaPlayer.create(context,
                R.raw.zapsplat_multimedia_notification_chime_bell_001_26401);
        playSound(context);
    }

    private static void playSound(Context context) {
        if(context != null) {
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
    }

}
