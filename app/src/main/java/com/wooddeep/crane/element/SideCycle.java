package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.SuperCircleView;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;

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
    public String name;
    public CenterCycle centerCycle;
    public SuperCircleView cycle;
    private WKTReader wKTReader = new WKTReader();
    private String centerGeoStr = "";
    public int currColor;

    public SideCycle(
        CenterCycle cc,
        float x,
        float y,
        float r,
        float ir,
        float hAngle,            // 水平方向夹角
        float vAngle,            // 垂直方向夹角
        float carRange,
        float h,
        String name
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
        this.name = name;
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
        //cycle.setmRingNormalColor(Color.rgb(46, 139, 87));
        cycle.setmRingNormalColor(Color.LTGRAY);
        cycle.sethAngle(hAngle);
        cycle.setvAngle(vAngle);
        cycle.setName(name);
        cycle.setCarRange(carRange);
        cycle.setScale(centerCycle.scale);
        cycle.setmViewCenterX(x * centerCycle.scale + centerCycle.deltaX);
        cycle.setmViewCenterY(height - y * centerCycle.scale - centerCycle.delatY);
        this.cycle = cycle;
        cycle.show();
        centerGeoStr = String.format("POINTSTRING (%f %f)", this.x, this.y);
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

    @Override
    public void setColor(int color) {
        //Color.rgb(46, 139, 87)
        if (this.currColor != color) {
            this.cycle.setmRingNormalColor(color);
            this.currColor = color;
        }
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

    @Override
    public Geometry getCenterGeo() throws Exception {
        Geometry geo = wKTReader.read(centerGeoStr);
        return geo;
    }

    @Override
    public Geometry getArmGeo(float dAngle) throws Exception {
        float cos = (float) Math.cos(Math.toRadians(this.hAngle + dAngle));
        float sin = (float) Math.sin(Math.toRadians(this.hAngle + dAngle));
        float endpointX = this.x + cos * this.r; // 本塔基 大臂端点 X 坐标
        float endpointY = this.y + sin * this.r; // 本塔基 大臂端点 Y 坐标
        float icos = (float) Math.cos(Math.toRadians(this.hAngle + 180 + dAngle));
        float isin = (float) Math.sin(Math.toRadians(this.hAngle + 180 + dAngle));
        float startpointX = this.x + (float) (this.ir * icos); // todo 添加垂直方向的斜率计算
        float startpointY = this.y + (float) (this.ir * isin);
        String armGeoStr = String.format("LINESTRING (%f %f, %f %f)", startpointX, startpointY, endpointX, endpointY);
        Geometry gcc = wKTReader.read(armGeoStr);
        armGeoStr = null;
        return gcc;
    }

    @Override
    public Geometry getCarGeo(float dAngle, float dDist) throws Exception {
        float cos = (float) Math.cos(Math.toRadians(this.hAngle + dAngle));
        float sin = (float) Math.sin(Math.toRadians(this.hAngle + dAngle));
        String carGeoStr = String.format("POINTSTRING (%f %f)",
            this.x + cos * (this.carRange + dDist), this.y + sin * (this.carRange + dDist));
        Geometry carPos = wKTReader.read(carGeoStr);
        carGeoStr = null;
        return carPos;
    }
}
