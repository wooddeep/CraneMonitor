package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.tookit.StringTool;
import com.wooddeep.crane.views.SuperCircleView;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class CenterCycle extends CycleElem {
    public float scale;
    public float oscale;            // 原始尺寸比例
    public float x;
    public float y;
    public float r;                   // 原始大臂半径
    public float ir;                  // 原始小臂半径
    public float hAngle;            // 水平方向夹角
    public float vAngle;             // 垂直方向夹角
    public float carRange;
    public float deltaX;
    public float delatY;
    public float height;
    public String name;
    public SuperCircleView cycle;

    private String centerGeoStr = "";

    private WKTReader wKTReader = new WKTReader();
    private Geometry geometry; // 几何坐标

    public CenterCycle(
        float oscale,
        float x,
        float y,
        float r,
        float ir,
        float hAngle,
        float vAngle,
        float carRange,
        float h,
        String name
    ) {
        this.oscale = oscale;
        this.r = r;
        this.ir = ir;
        this.x = x;
        this.y = y;
        this.hAngle = hAngle;
        this.vAngle = vAngle;
        this.carRange = carRange;
        this.height = h;
        this.name = name;
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

        float originBackWidth = originRadius * 2 + ringWidth * 2 + 10; // 默认圆环正方形背景高度
        float originBackHeight = originBackWidth; // 默认圆环正方形背景宽度
        float scale = originRadius / r;
        float centerX = width / 2;   // 中心点x坐标(到左边距的长度)，相对于FrameLayout的左下角
        float centerY = height / 2;   // 中心点y坐标(到下边距的长度)，相对于FrameLayout的左下角

        SuperCircleView cycle = new SuperCircleView(context);
        cycle.setElevation(10);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(width, height);

        cycle.setLayoutParams(paras);
        parent.addView(cycle);
        cycle.setDefMinRadio(originRadius);
        cycle.setmViewCenterX(centerX);
        cycle.setmViewCenterY(centerY);
        cycle.setBackgroundColor(0x00000000); // 透明色
        cycle.setDefRingWidth(ringWidth);
        cycle.setmRingNormalColor(Color.rgb(18,150,219));
        float orgInnerRadius = originRadius / r * ir;
        cycle.setmInnerRadio(orgInnerRadius);
        cycle.sethAngle(hAngle);
        cycle.setvAngle(vAngle);
        cycle.setCarRange(carRange * scale);
        cycle.setScale(scale);
        cycle.setName(name);
        cycle.show();
        this.deltaX = centerX - x * scale;
        this.delatY = height - centerY - y * scale;
        this.cycle = cycle;
        this.scale = scale;

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
        this.cycle.setmRingNormalColor(color);
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
    public void setCarRange(float range) {
        this.carRange = range;
        //this.cycle.setCarRange(range);
        cycle.setCarRange(range * scale);
    }

    @Override
    public Geometry getGeometry() {
        //Geometry g1 = new WKTReader().read("LINESTRING (0 10, 10 0)");
        //Geometry g2 = new WKTReader().read("POINTSTRING (0 0)"); // 大臂端点
        return null;
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

        //BufferedReader bufferedReader = StringTool.stringReader(armGeoStr.getBytes());

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
