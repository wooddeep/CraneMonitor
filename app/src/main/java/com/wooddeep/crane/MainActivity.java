package com.wooddeep.crane;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wooddeep.crane.views.SimpleProgressbar;
import com.wooddeep.crane.views.SuperCircleView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    SuperCircleView mSuperCircleView;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSuperCircleView = findViewById(R.id.superview);
        mSuperCircleView.setValue(100, textView);
        mSuperCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //随机设定圆环大小
                int i = new Random().nextInt(100) + 1;
                //int i = 2;
                Log.i(TAG, "onClick: i::" + i);
                mSuperCircleView.setValue(i, textView);
            }
        });


        final SimpleProgressbar spb = (SimpleProgressbar) findViewById(R.id.spb);

        final int max = spb.getMax();

        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = spb.getProgress();
                while ((progress + 1) <= max) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    spb.setProgress(progress + 1);
                    progress = progress + 1;
                }
            }
        }).start();

    }


}
