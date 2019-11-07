package com.wooddeep.crane.alarm;

import android.content.Context;
import android.media.MediaPlayer;

import com.wooddeep.crane.R;

public class AlertSound {

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
            playsers[i].setLooping(false);
        }
    }

    public static void open(int index) {
        if (index > 5) return;
        if (index == 1) {
            playsers[0].start();
        } else {
            playsers[1].start();
        }
    }

}
