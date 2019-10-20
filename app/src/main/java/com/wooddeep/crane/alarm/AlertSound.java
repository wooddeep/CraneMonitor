package com.wooddeep.crane.alarm;

import android.content.Context;
import android.media.MediaPlayer;

import com.wooddeep.crane.R;

public class AlertSound {
    public static AlarmFile[] soundFileList = new AlarmFile[]{
        new AlarmFile(R.raw.level1_alarm, false),
        new AlarmFile(R.raw.level2_alarm, false),
        new AlarmFile(R.raw.level3_alarm, false),
        new AlarmFile(R.raw.level4_alarm, false),
        new AlarmFile(R.raw.level5_alarm, false),
    };

    public static MediaPlayer[] playsers = new MediaPlayer[soundFileList.length];

    public static void init(Context context) {
        for (int i = 0; i < soundFileList.length; i++) {
            playsers[i] = MediaPlayer.create(context, soundFileList[i].fileId);
            playsers[i].setLooping(true);
            //playsers[i].pause();
        }
    }

    public static void open(int index) {
        for (int i = 0; i < playsers.length; i++) {
            if (i + 1 == index) {
                if (!playsers[i].isPlaying()) {
                    playsers[i].start();
                }
            } else {
                if (playsers[i].isPlaying()) {
                    playsers[i].pause();
                }
            }
        }
    }

}
