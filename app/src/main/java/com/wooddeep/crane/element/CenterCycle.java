package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.SuperCircleView;

public class CenterCycle extends BaseElem{
    public float scale;
    public float oscale;            // 原始尺寸比例
    public float r;                   // 原始大臂半径
    public float ir;                  // 原始小臂半径
    public float hAngle;            // 水平方向夹角
    public float vAngle;             // 垂直方向夹角
    public SuperCircleView cycle;

    public CenterCycle(
        float oscale,
        float r,
        float ir,
        float hAngle,
        float vAngle
    ) {
        this.oscale = oscale;
        this.r = r;
        this.ir = ir;
        this.hAngle = hAngle;
        this.vAngle = vAngle;
    }

    public void drawCenterCycle(
        Activity activity,
        ViewGroup parent
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
        this.scale = scale;
        this.cycle = cycle;
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
}
