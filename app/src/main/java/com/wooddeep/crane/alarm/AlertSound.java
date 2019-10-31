package com.wooddeep.crane.alarm;

import android.content.Context;
import android.media.MediaPlayer;

import com.wooddeep.crane.R;

public class AlertSound {

    private static boolean [] playingflags = new boolean[] {false, false, false, false, false};

    public static AlarmFile[] soundFileList = new AlarmFile[]{
        new AlarmFile(R.raw.alarm, false),
        new AlarmFile(R.raw.warning, false),
        new AlarmFile(R.raw.warning, false),
        new AlarmFile(R.raw.warning, false),
        new AlarmFile(R.raw.warning, false),
    };

    public static MediaPlayer[] playsers = new MediaPlayer[soundFileList.length];

    public static void init(Context context) {
        for (int i = 0; i < soundFileList.length; i++) {
            playsers[i] = MediaPlayer.create(context, soundFileList[i].fileId);
            playsers[i].setLooping(true);
            playsers[i].start();
            playsers[i].pause();
        }
    }

    public static void reinit(Context context) {
        int count = 0;
        for (int i = 0; i < soundFileList.length; i++) {
            if (playsers[i].isPlaying()) {
                count++;
            }
        }
        if (count < 2) return;

        System.out.println("## alarm sound reinit!");

        for (int i = 0; i < soundFileList.length; i++) {
            if (playsers[i].isPlaying()) {
                playsers[i].pause();
            }
            playsers[i].release();
        }

        for (int i = 0; i < soundFileList.length; i++) {
            playsers[i] = MediaPlayer.create(context, soundFileList[i].fileId);
            playsers[i].setLooping(true);
            playsers[i].start();
            playsers[i].pause();
        }
    }

    public static void open(int index) {
        for (int i = 0; i < playsers.length; i++) {
            if (i + 1 == index) {
                if (!playsers[i].isPlaying()) {
                //if (!playingflags[i]) {
                    System.out.println("## open: alert sound index: " + i);
                    playsers[i].start();
                    //playingflags[i] = true;
                }
            } else {
                if (playsers[i].isPlaying()) {
                //if (playingflags[i]) {
                    System.out.println("## close: alert sound index: " + i);
                    playsers[i].pause();
                    //playingflags[i] = false;
                }
            }
        }
    }

}
