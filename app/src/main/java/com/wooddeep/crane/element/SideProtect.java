package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.Curve;
import com.wooddeep.crane.views.Polygon;
import com.wooddeep.crane.views.Vertex;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SideProtect extends BaseElem{
    public int color;
    public List<Vertex> overtexs;
    public List<Vertex> vertexs;
    public CenterCycle centerCycle;
    public Curve curve;

    public SideProtect(
        CenterCycle cc,
        int color,
        List<Vertex> vertexs
    ) {
        this.centerCycle = cc;
        this.color = color;
        this.vertexs = vertexs;

        this.overtexs = new ArrayList<>();
        for (Vertex vertex: vertexs) {
            Vertex node = new Vertex(vertex.x, vertex.y);
            overtexs.add(node);
        }
    }

    public void drawSideProtect(
        Activity activity,
        ViewGroup parent
    ) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        for (Vertex vertex : vertexs) {
            //if (vertex.x < 0 || vertex.y < 0) continue;
            vertex.x = vertex.x * centerCycle.scale + centerCycle.deltaX;
            vertex.y = height - vertex.y * centerCycle.scale - centerCycle.delatY;  // y 轴转换
        }

        Curve curve = new Curve(context);
        this.curve = curve;
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(width, height);
        curve.setLayoutParams(paras);
        parent.addView(curve);

        curve.setBackgroundColor(0x00000000); // 透明色
        curve.setColor(color);
        curve.setValue(vertexs);
    }

    @Override
    public Geometry getGeometry() {
        return null;
    }

    public void setAlarm(boolean alarm) {
        this.curve.setAlarm(alarm);
    }

    public boolean getAlarm() {
        return this.curve.getAlarm();
    }

    public boolean getFlink() {
        return this.curve.getFlink();
    }

    public void setFlink(boolean flink) {
        this.curve.setFlink(flink);
    }

    public void setBoderColer(int boderColer) {
        this.curve.setBoderColer(boderColer);
    }
}
