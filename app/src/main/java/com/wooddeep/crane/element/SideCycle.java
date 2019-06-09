package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.SuperCircleView;

import org.locationtech.jts.geom.Geometry;

public class SideCycle  extends BaseElem{
    public float scale;
    public float cx;
    public float cy;
    public float x;
    public float y;
    public float r;
    public float ir;
    public float hAngle;            // 水平方向夹角
    public float vAngle;            // 垂直方向夹角
    public SuperCircleView cycle;

    public SideCycle(
        float scale,
        float cx,
        float cy,
        float x,
        float y,
        float r,
        float ir,
        float hAngle,            // 水平方向夹角
        float vAngle            // 垂直方向夹角
    ) {
        this.scale = scale;
        this.cx = cx;
        this.cy = cy;
        this.x = x;
        this.y = y;
        this.r = r;
        this.ir = ir;
        this.hAngle = hAngle;
        this.vAngle = vAngle;
    }

    public void drawSideCycle(
        Activity activity,
        ViewGroup parent
    ) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        // 1号塔机坐标100.100圆环直径55。2号塔机坐标130.123圆环直径60
        int ringWidth = 2; // 固定圆环宽度
        float originRadius = (scale * r);
        float originBackWidth = originRadius * 2 + ringWidth * 2 + 4; // 默认圆环正方形背景高度
        float originBackHeight = originBackWidth + 4; // 默认圆环正方形背景宽度
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
        this.cycle = cycle;
        cycle.show();
    }

    public void hAngleAdd(float added) {
        this.hAngle = this.hAngle + added;
        this.cycle.sethAngle(this.hAngle);
    }

    public void hAngleSub(float subed) {
        this.hAngle = this.hAngle - subed;
        this.cycle.sethAngle(this.hAngle);
    }

    public void vAngleAdd(float added) {
        this.vAngle = this.vAngle + added;
        this.cycle.setvAngle(this.vAngle);
    }

    public void vAngleSub(float subed) {
        this.vAngle = this.vAngle - subed;
        this.cycle.setvAngle(this.vAngle);
    }

    public void setAlarm(boolean alarm) {
        this.cycle.setAlarm(alarm);
    }

    public boolean getAlarm() {
        return this.cycle.getAlarm();
    }

    public boolean getFlink() {
        return this.cycle.getFlink();
    }

    public void setFlink(boolean flink) {
        this.cycle.setFlink(flink);
    }

    @Override
    public Geometry getGeometry() {
        return null;
    }
}
