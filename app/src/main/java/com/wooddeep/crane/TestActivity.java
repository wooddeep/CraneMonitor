package com.wooddeep.crane;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    //private SurfaceView  srfc;
    private Button start;
    private Button stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vlc_test);

        //在Activity中可以为按钮增加事件
        SurfaceView srfc = findViewById(R.id.srfc);
        start = findViewById(R.id.btn_play_start);
        stop = findViewById(R.id.btn_play_stop);

        LibVLC libVLC = null;
        ArrayList<String> options = new ArrayList<>();
        libVLC = new LibVLC(getApplication(), options);
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer(libVLC);
            //String url = getString(R.string.http_video11_qtv_com_cn_qtv1_sd_manifest_m3u8);

            String url = "rtsp://192.168.141.98:8554/camera";
            //String url = "file:///sdcard/lihan.mp4";

            mediaPlayer.getVLCVout().setVideoSurface(srfc.getHolder().getSurface(), srfc.getHolder());
            //播放前还要调用这个方法
            mediaPlayer.getVLCVout().attachViews();

            Media media = new Media(libVLC, Uri.parse(url));

            mediaPlayer.setMedia(media);
            //mediaPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
        }

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mediaPlayer.play();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }
}
