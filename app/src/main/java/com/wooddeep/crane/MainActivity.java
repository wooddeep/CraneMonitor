package com.wooddeep.crane;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.wooddeep.crane.tookit.CenterCycle;
import com.wooddeep.crane.tookit.SideArea;
import com.wooddeep.crane.tookit.SideCycle;
import com.wooddeep.crane.views.Vertex;

import java.util.ArrayList;
import java.util.List;

// 启动mumu之后, 输入：
// adb connect 127.0.0.1:7555
// 然后再调试, 就ok了

/**
 * 获取主界面FrameLayout的坐标及长宽
 **/
/*
@Override
public void onWindowFocusChanged (boolean hasFocus) {
    FrameLayout mainFrame = findViewById(R.id.main_frame);
    int[] location = new int[2];
    mainFrame.getLocationInWindow(location);
    Context context = getApplicationContext();
    int width = mainFrame.getMeasuredWidth(); // 获取组件宽度
    int height = mainFrame.getMeasuredHeight(); // 获取组件高度
    Log.i(TAG, String.format("## MainFrame: x0:%d, y0:%d, width:%d, height:%d\n", location[0], location[1], width, height));
}
*/

// 圆环设置
/*
SuperCircleView mSuperCircleView;
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
*/

// 进度条
/*
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
*/

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        FrameLayout mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        final CenterCycle centerCycle = new CenterCycle(1.0f, 80 / 2, 10, 45, 30);
        centerCycle.drawCenterCycle(this, mainFrame);

        float scale = centerCycle.scale; //DrawTool.DrawCenterCycle(this, mainFrame, 1.0f, 80 / 2, 10, 45, 30);
        SideCycle sideCycle = new SideCycle(scale, 100, 100, 130, 130, 60 / 2, 10, -45, 0);
        sideCycle.drawSideCycle(this, mainFrame);


        List<Vertex> vertex1 = new ArrayList<Vertex>() {{
            add(new Vertex(35, 75));
            add(new Vertex(35, 125));
            add(new Vertex(60, 100));
        }};

        SideArea sideArea = new SideArea(Color.GREEN, scale, 100, 100, vertex1);
        sideArea.drawSideArea(this, mainFrame);

        List<Vertex> vertex2 = new ArrayList<Vertex>() {{
            add(new Vertex(160, 130));
            add(new Vertex(175, 160));
            add(new Vertex(190, 130));
        }};

        SideArea sideArea1 = new SideArea(Color.RED, scale, 100, 100, vertex2);
        sideArea1.drawSideArea(this, mainFrame);

        findViewById(R.id.adjust_hangle_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCycle.hAngleAdd(10);
            }
        });

        findViewById(R.id.adjust_hangle_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCycle.hAngleSub(10);
            }
        });

        findViewById(R.id.adjust_vangle_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCycle.vAngleAdd(5);
            }
        });

        findViewById(R.id.adjust_vangle_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCycle.vAngleSub(5);
            }
        });
    }

}
