package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.SuperCircleView;

import org.locationtech.jts.geom.Geometry;

public class SideCycle  extends CycleElem {
    public float x;
    public float y;
    public float r;
    public float ir;
    public float hAngle;            // 水平方向夹角
    public float vAngle;            // 垂直方向夹角
    public float prvCarRange = -1f;
    public float prvHangle = -1f;
    public float carRange;
    public float height;
    public CenterCycle centerCycle;
    public SuperCircleView cycle;

    public SideCycle(
        CenterCycle cc,
        float x,
        float y,
        float r,
        float ir,
        float hAngle,            // 水平方向夹角
        float vAngle,            // 垂直方向夹角
        float carRange,
        float h
    ) {
        this.centerCycle = cc;
        this.x = x;
        this.y = y;
        this.r = r;
        this.ir = ir;
        this.hAngle = hAngle;
        this.vAngle = vAngle;
        this.carRange = carRange * cc.scale;
        this.height = h;
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
        float originRadius = (centerCycle.scale * r);
        SuperCircleView cycle = new SuperCircleView(context);

        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(width, height);
        cycle.setLayoutParams(paras);
        parent.addView(cycle);
        cycle.setDefMinRadio(originRadius);
        float orgInnerRadius = originRadius / r * ir;
        cycle.setmInnerRadio(orgInnerRadius);
        cycle.setBackgroundColor(0x00000000); // 透明色
        cycle.setDefRingWidth(ringWidth);
        cycle.setmRingNormalColor(Color.rgb(46, 139, 87));
        cycle.sethAngle(hAngle);
        cycle.setvAngle(vAngle);
        cycle.setCarRange(carRange);
        cycle.setScale(centerCycle.scale);
        cycle.setmViewCenterX(x * centerCycle.scale + centerCycle.deltaX);
        cycle.setmViewCenterY(height - y * centerCycle.scale - centerCycle.delatY);
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

    @Override
    public void setHAngle(float angle) {
        if (this.hAngle == angle) return;
        this.hAngle = angle;
        this.cycle.sethAngle(angle);
    }

    @Override
    public float getHAngle() {
        return this.hAngle;
    }

    @Override
    public void setCarRange(float range) {
        this.carRange = range;
        cycle.setCarRange(range * this.centerCycle.scale);
    }
}
