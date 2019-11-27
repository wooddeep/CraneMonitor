package com.wooddeep.crane.alarm;

import android.content.Context;
import android.media.MediaPlayer;

import com.wooddeep.crane.R;

public class AlertSound {

    public static AlarmFile[] soundFileList = new AlarmFile[]{
        new AlarmFile(R.raw.alarm, false),
        new AlarmFile(R.raw.warning, false),
    };

    public static MediaPlayer[] playsers = new MediaPlayer[soundFileList.length];

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

    public static void open(int index) {

        if (index > 5) {
            playsers[0].setVolume(0, 0);
            playsers[1].setVolume(0, 0);
            return;
        }

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
