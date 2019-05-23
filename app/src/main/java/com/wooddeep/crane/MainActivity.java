package com.wooddeep.crane;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.Polygon;
import com.wooddeep.crane.views.SuperCircleView;
import com.wooddeep.crane.views.Vertex;

import java.util.ArrayList;
import java.util.List;

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

    // (100, 100, 55/2)
    // (130, 123, 60/2)
    // detaX = 30
    // detaY = 23

    /**
     * 画中心圆环
     **/
    public static float DrawCenterCycle(Activity activity, ViewGroup parent, float oscale, int r, int ir) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        // 1号塔机坐标100.100圆环直径55。2号塔机坐标130.123圆环直径60
        int ringWidth = 2; // 固定圆环宽度
        float originRadius = Math.min(width, height) / 4; // 默认圆的直径为屏幕的一半
        originRadius = oscale * originRadius; // 比例变化

        float originBackWidth = originRadius * 2 + ringWidth * 2; // 默认圆环正方形背景高度
        float originBackHeight = originBackWidth; // 默认圆环正方形背景宽度
        float scale = originRadius / r;
        int centerX = width / 2;   // 中心点x坐标(到左边距的长度)，相对于FrameLayout的左下角
        int centerY = height/ 2;   // 中心点y坐标(到下边距的长度)，相对于FrameLayout的左下角

        float leftMargin = centerX - originRadius;  // 左偏
        float topMagin = height - (centerY + originRadius);   // 下偏

        SuperCircleView cycle = new SuperCircleView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams((int)originBackWidth, (int)originBackHeight);
        paras.leftMargin = (int)leftMargin;
        paras.topMargin = (int)topMagin;
        cycle.setLayoutParams(paras);
        parent.addView(cycle);
        cycle.setDefMinRadio((int)originRadius);
        cycle.setBackgroundColor(0x00000000); // 透明色
        cycle.setDefRingWidth(ringWidth);
        cycle.setmRingNormalColor(Color.GREEN);
        float orgInnerRadius = originRadius / r * ir;
        cycle.setmInnerRadio((int)orgInnerRadius);
        cycle.setValue(100);
        return scale;
    }

    /**
     * 画旁边圆环
     **/
    public static float DrawSideCycle(Activity activity, ViewGroup parent, float scale, int cx, int cy, int x, int y, int r) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        // 1号塔机坐标100.100圆环直径55。2号塔机坐标130.123圆环直径60
        int ringWidth = 2; // 固定圆环宽度
        int originRadius = (int)scale * r;
        int originBackWidth = originRadius * 2 + ringWidth * 2; // 默认圆环正方形背景高度
        int originBackHeight = originBackWidth; // 默认圆环正方形背景宽度
        int centerX = width / 2;   // 中心点x坐标(到左边距的长度)，相对于FrameLayout的左下角
        int centerY = height/ 2;   // 中心点y坐标(到下边距的长度)，相对于FrameLayout的左下角

        int deltaX = (int)scale * (x - cx);
        int delatY = (int)scale * (y - cy);

        int leftMargin = centerX - originRadius + deltaX;  // 左偏
        int topMagin = height - (centerY + originRadius) - delatY;   // 下偏

        SuperCircleView cycle = new SuperCircleView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(originBackWidth, originBackHeight);
        paras.leftMargin = leftMargin;
        paras.topMargin = topMagin;
        cycle.setLayoutParams(paras);
        parent.addView(cycle);
        cycle.setDefMinRadio(originRadius);
        cycle.setBackgroundColor(0x00000000); // 透明色
        cycle.setDefRingWidth(ringWidth);
        cycle.setmRingNormalColor(Color.GREEN);
        cycle.setValue(100);
        return scale;
    }

    /**
     * 画旁边圆环
     * @param: cx central ring's x axis
     **/
    public static void DrawSideArea(Activity activity, ViewGroup parent, float scale, int cx, int cy) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        // 1号塔机坐标100.100圆环直径55。2号塔机坐标130.123圆环直径60

        /*
        int ringWidth = 2; // 固定圆环宽度
        int originRadius = (int)scale * r;
        int originBackWidth = originRadius * 2 + ringWidth * 2; // 默认圆环正方形背景高度
        int originBackHeight = originBackWidth; // 默认圆环正方形背景宽度
        int centerX = width / 2;   // 中心点x坐标(到左边距的长度)，相对于FrameLayout的左下角
        int centerY = height/ 2;   // 中心点y坐标(到下边距的长度)，相对于FrameLayout的左下角

        int deltaX = (int)scale * (x - cx);
        int delatY = (int)scale * (y - cy);

        int leftMargin = centerX - originRadius + deltaX;  // 左偏
        int topMagin = height - (centerY + originRadius) - delatY;   // 下偏
        */

        Polygon area = new Polygon(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(300, 300); // TODO 替换实际的长宽
        //paras.leftMargin = leftMargin;
        //paras.topMargin = topMagin;
        area.setLayoutParams(paras);
        parent.addView(area);
        //cycle.setDefMinRadio(originRadius);
        area.setBackgroundColor(0x00000000); // 透明色

        List<Vertex> vertex = new ArrayList<Vertex>() {{
            add(new Vertex(100, 100));
            add(new Vertex(150, 150));
            add(new Vertex(50, 150));
            add(new Vertex(0, 50));
        }};

        area.setValue(vertex);
    }


    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        FrameLayout mainFrame = findViewById(R.id.main_frame);
        float scale  = DrawCenterCycle(this, mainFrame, 1f, 55/2, 20);
        DrawSideCycle(this, mainFrame, scale, 100, 100, 130, 123, 60/2);
        DrawSideArea(this, mainFrame, scale, 0, 0);
    }

}
