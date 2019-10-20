package com.wooddeep.crane.alarm;

import android.content.Context;
import android.media.MediaPlayer;

import com.wooddeep.crane.R;

class AlarmFile {
    public int fileId;
    public boolean isAlarm;

    public AlarmFile(int fi, boolean ia) {
        this.fileId = fi;
        this.isAlarm = ia;
    }
}

public class AlarmSound {

    //private static MediaPlayer player;

    public static AlarmFile[] soundFileList = new AlarmFile[]{
        /*
        new AlarmFile(R.raw.car_back_alarm, false),
        new AlarmFile(R.raw.car_back_warning, false),
        new AlarmFile(R.raw.car_out_alarm, false),
        new AlarmFile(R.raw.car_out_warning, false),
        new AlarmFile(R.raw.hook_down_warning, false),
        new AlarmFile(R.raw.hook_up_warning, false),
        new AlarmFile(R.raw.left_rotate_alarm, false),
        new AlarmFile(R.raw.left_rotate_warning, false),
        new AlarmFile(R.raw.moment_alarm, false),
        new AlarmFile(R.raw.moment_warning, false),
        new AlarmFile(R.raw.right_rotate_alarm, false),
        new AlarmFile(R.raw.right_rotate_warning, false),
        new AlarmFile(R.raw.rigt_left_alarm, false),
        new AlarmFile(R.raw.weight_alarm, false)
        */
    };

    public static MediaPlayer[] playsers = new MediaPlayer[soundFileList.length];

    private static int currentIndex = 0;

    public static void init(Context context) {
        for (int i = 0; i < soundFileList.length; i++) {
            playsers[i] = MediaPlayer.create(context, soundFileList[i].fileId);
            //playsers[i].pause();
            //playsers[i].start();
        }
    }

    public static void setStatus(int id, boolean status) {
        for (int i = 0; i < soundFileList.length; i++) {
            if (id == soundFileList[i].fileId) {
                soundFileList[i].isAlarm = status;
            }
        }
    }


    public static void pause() {
        //player.pause();
        for (int i = 0; i < playsers.length; i++) {
            if (playsers[i].isPlaying()) {
                playsers[i].pause();
            }
        }
    }

    public static void start(int index) {
        //playsers[index].start();

        if (!playsers[index].isPlaying() && soundFileList[index].isAlarm) { // 未运行, 有告警
            playsers[index].start();
            playsers[index].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playsers[index].pause();
                    start((index + 1) % playsers.length);
                }
            });
        }

        if (playsers[index].isPlaying() && !soundFileList[index].isAlarm) { // 在运行，无告警
            playsers[index].pause();
            start((index + 1) % playsers.length);
        }

        if (playsers[index].isPlaying() && soundFileList[index].isAlarm) { // 在运行，有告警
            return;
        }

        if (!playsers[index].isPlaying() && !soundFileList[index].isAlarm) { // 未运行，无告警
            start((index + 1) % playsers.length);
            return;
        }

    }

}
