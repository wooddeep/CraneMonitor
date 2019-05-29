package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.Polygon;
import com.wooddeep.crane.views.SuperCircleView;
import com.wooddeep.crane.views.Vertex;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SideArea extends BaseElem{

    public int color;
    public float scale;
    public float cx;
    public float cy;
    public List<Vertex> overtexs;
    public List<Vertex> vertexs;

    public Polygon area;

    public SideArea(
        int color,
        float scale,
        float cx,
        float cy,
        List<Vertex> vertexs
    ) {
        this.color = color;
        this.scale = scale;
        this.cx = cx;
        this.cy = cy;
        this.vertexs = vertexs;

        this.overtexs = new ArrayList<>();
        for (Vertex vertex: vertexs) {
            Vertex node = new Vertex(vertex.x, vertex.y);
            overtexs.add(node);
        }
    }

    public void drawSideArea(
        Activity activity,
        ViewGroup parent
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
        this.area = area;
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(width, height);
        area.setLayoutParams(paras);
        parent.addView(area);

        area.setBackgroundColor(0x00000000); // 透明色
        area.setColor(color);
        area.setValue(vertexs);
    }

    @Override
    public Geometry getGeometry() {
        return null;
    }

    public void setAlarm(boolean alarm) {
        this.area.setAlarm(alarm);
    }

    public boolean getAlarm() {
        return this.area.getAlarm();
    }

    public boolean getFlink() {
        return this.area.getFlink();
    }

    public void setFlink(boolean flink) {
        this.area.setFlink(flink);
    }

    public void setBoderColer(int boderColer) {
        this.area.setBoderColer(boderColer);
    }
}
