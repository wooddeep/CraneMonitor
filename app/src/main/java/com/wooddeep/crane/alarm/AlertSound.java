package com.wooddeep.crane.alarm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.wooddeep.crane.MainActivity;
import com.wooddeep.crane.R;

import static android.content.Context.AUDIO_SERVICE;

public class AlertSound {

    public static AlarmFile[] soundFileList = new AlarmFile[]{
        new AlarmFile(R.raw.alarm, false),
        new AlarmFile(R.raw.warning, false),
        //new AlarmFile(R.raw.warning, false),
        //new AlarmFile(R.raw.warning, false),
        //new AlarmFile(R.raw.warning, false),
    };

    public static MediaPlayer[] playsers = new MediaPlayer[soundFileList.length];

    /*
    public static void init(Context context) {
        for (int i = 0; i < soundFileList.length; i++) {
            playsers[i] = MediaPlayer.create(context, soundFileList[i].fileId);
            playsers[i].setLooping(false);
        }

        playsers[0].setVolume(0, 0);
        playsers[1].setVolume(0, 0);

        playsers[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (MainActivity.alarmLevel.get() == 1) {
                    playsers[0].setVolume(1, 1);
                } else {
                    playsers[0].setVolume(0, 0);
                }

                playsers[0].start();
            }
        });
        playsers[0].start();

        playsers[1].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (MainActivity.alarmLevel.get() > 1 && MainActivity.alarmLevel.get() <=5) {
                    playsers[1].setVolume(1, 1);
                } else {
                    playsers[1].setVolume(0, 0);
                }

                playsers[1].start();
            }
        });
        playsers[1].start();
    }
    */

    public static void init(Context context) {
        for (int i = 0; i < soundFileList.length; i++) {
            playsers[i] = MediaPlayer.create(context, soundFileList[i].fileId);
            playsers[i].setLooping(true);
        }

        playsers[0].setVolume(0, 0);
        playsers[1].setVolume(0, 0);

        playsers[0].start();

        playsers[1].start();
    }

    /*
    public static void open(int index) {
        System.out.println("## alarm index = " + index);
        if (index > 5) return;
        try {
            if (index == 1) {
                if (!playsers[0].isPlaying()) {
                    playsers[0].start();
                }
            } else {
                if (!playsers[1].isPlaying()) {
                    playsers[1].start();
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        try {
            if (index == 1) {
                if (playsers[1].isPlaying()) {
                    playsers[1].pause();
                }
            } else {
                if (playsers[0].isPlaying()) {
                    playsers[0].pause();
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    */


    public static void open(int index) {

        if (index > 5) return;

        if (index == 1) {
            playsers[0].setVolume(1, 1);
            playsers[1].setVolume(0, 0);
            return;
        }

        if (index > 1 && index < 6) {
            playsers[0].setVolume(0, 0);
            playsers[1].setVolume(1, 1);
            return;
        }
    }

}
