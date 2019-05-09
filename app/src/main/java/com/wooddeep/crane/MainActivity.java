package com.wooddeep.crane;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wooddeep.crane.views.SimpleProgressbar;
import com.wooddeep.crane.views.SuperCircleView;

import java.util.Random;

// 获取控件的尺寸
// mainFrame.measure(0,0);

// 获取组件宽度
// int width = mainFrame.getMeasuredWidth();

// 获取组件高度
// int height = mainFrame.getMeasuredHeight();

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    SuperCircleView mSuperCircleView;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout mainFrame = findViewById(R.id.main_frame);

        mSuperCircleView = findViewById(R.id.superview);
        mSuperCircleView.setValue(100);
        mSuperCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //随机设定圆环大小
                int i = new Random().nextInt(100) + 1;
                //int i = 2;
                Log.i(TAG, "onClick: i::" + i);
                mSuperCircleView.setValue(i);
            }
        });

        context = getApplicationContext();

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

        // 画圆
        SuperCircleView cycle = new SuperCircleView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(320, 320);
        paras.leftMargin = 300;
        paras.topMargin = 100;
        cycle.setLayoutParams(paras);
        mainFrame.addView(cycle);
        cycle.setDefMinRadio(150);
        cycle.setValue(100);
        cycle.setBackgroundColor(0x00000000);

    }

    /**
     * 获取控件坐标
     **/
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        int[] location = new int[2];
        FrameLayout mainFrame = findViewById(R.id.main_frame);
        mainFrame.getLocationInWindow(location);

    }
}
