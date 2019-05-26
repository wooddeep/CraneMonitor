package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.Polygon;
import com.wooddeep.crane.views.SuperCircleView;
import com.wooddeep.crane.views.Vertex;

import java.util.Arrays;
import java.util.List;

public class DrawTool {

    /**
     * 画中心圆环
     **/
    public static float DrawCenterCycle(
        Activity activity,
        ViewGroup parent,
        float oscale,            // 原始尺寸比例
        float r,                   // 原始大臂半径
        float ir,                  // 原始小臂半径
        float hAngle,            // 水平方向夹角
        float vAngle             // 垂直方向夹角
    ) {
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
        int centerY = height / 2;   // 中心点y坐标(到下边距的长度)，相对于FrameLayout的左下角

        float leftMargin = centerX - originRadius;  // 左偏
        float topMagin = height - (centerY + originRadius);   // 下偏

        SuperCircleView cycle = new SuperCircleView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams((int) originBackWidth, (int) originBackHeight);
        paras.leftMargin = (int) leftMargin;
        paras.topMargin = (int) topMagin;
        cycle.setLayoutParams(paras);
        parent.addView(cycle);
        cycle.setDefMinRadio((int) originRadius);
        cycle.setBackgroundColor(0x00000000); // 透明色
        cycle.setDefRingWidth(ringWidth);
        cycle.setmRingNormalColor(Color.GREEN);
        float orgInnerRadius = originRadius / r * ir;
        cycle.setmInnerRadio((int) orgInnerRadius);
        cycle.sethAngle(hAngle);
        cycle.setvAngle(vAngle);
        cycle.show();
        return scale;
    }

    /**
     * 画旁边圆环
     **/
    public static float DrawSideCycle(
        Activity activity,
        ViewGroup parent,
        float scale,
        float cx,
        float cy,
        float x,
        float y,
        float r,
        float ir,
        float hAngle,            // 水平方向夹角
        float vAngle             // 垂直方向夹角
    ) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        // 1号塔机坐标100.100圆环直径55。2号塔机坐标130.123圆环直径60
        int ringWidth = 2; // 固定圆环宽度
        float originRadius = (scale * r);
        float originBackWidth = originRadius * 2 + ringWidth * 2; // 默认圆环正方形背景高度
        float originBackHeight = originBackWidth; // 默认圆环正方形背景宽度
        float centerX = width / 2;   // 中心点x坐标(到左边距的长度)，相对于FrameLayout的左下角
        float centerY = height / 2;   // 中心点y坐标(到下边距的长度)，相对于FrameLayout的左下角

        float deltaX = (scale * (x - cx));
        float deltaY = (scale * (y - cy));

        float leftMargin = centerX - originRadius + deltaX;  // 左偏
        float topMagin = height - (centerY + originRadius) - deltaY;   // 下偏

        SuperCircleView cycle = new SuperCircleView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams((int)originBackWidth, (int)originBackHeight);
        paras.leftMargin = (int)leftMargin;
        paras.topMargin = (int)topMagin;
        cycle.setLayoutParams(paras);
        parent.addView(cycle);
        cycle.setDefMinRadio((int)originRadius);
        float orgInnerRadius = originRadius / r * ir;
        cycle.setmInnerRadio((int) orgInnerRadius);
        cycle.setBackgroundColor(0x00000000); // 透明色
        cycle.setDefRingWidth(ringWidth);
        cycle.setmRingNormalColor(Color.GREEN);
        cycle.sethAngle(hAngle);
        cycle.setvAngle(vAngle);
        cycle.show();
        return scale;
    }

    /**
     * 画旁边区域
     *
     * @param: cx central ring's x axis
     **/
    public static void DrawSideArea(
        Activity activity,
        ViewGroup parent,
        int color,
        float scale,
        float cx,
        float cy,
        List<Vertex> vertexs
    ) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        int centerX = width / 2;   // 中心点x坐标(到左边距的长度)，相对于FrameLayout的左下角
        int centerY = height / 2;   // 中心点y坐标(到下边距的长度)，相对于FrameLayout的左下角

        float[] xAxies = new float[vertexs.size()];
        float[] yAxies = new float[vertexs.size()];
        for (int i = 0; i < vertexs.size(); i++) {
            xAxies[i] = vertexs.get(i).x;
            yAxies[i] = vertexs.get(i).y;
        }
        Arrays.sort(xAxies);
        Arrays.sort(yAxies);

        for (Vertex vertex : vertexs) {
            vertex.x = (scale * vertex.x) + centerX - (cx * scale);
            vertex.y = height - (scale * vertex.y) + (cy * scale) - centerY;  // y 轴转换
        }

        Polygon area = new Polygon(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(width, height);
        area.setLayoutParams(paras);
        parent.addView(area);

        area.setBackgroundColor(0x00000000); // 透明色
        area.setColor(color);
        area.setValue(vertexs);
    }


}
