package com.wooddeep.crane.element;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.Polygon;
import com.wooddeep.crane.views.Vertex;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SideArea extends BaseElem{
    public int color;
    public List<Vertex> overtexs;
    public List<Vertex> vertexs;
    public CenterCycle centerCycle;
    public Polygon area;
    public int type;
    public float height; // 高度

    public SideArea(
        CenterCycle cc,
        int color,
        List<Vertex> vertexs,
        int t,
        float h
    ) {
        this.centerCycle = cc;
        this.color = color;
        this.type = t;
        this.height = h;
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

        for (Vertex vertex : vertexs) {
            //if ((int)(vertex.x) < 0 || (int)(vertex.y) < 0) break;
            vertex.x = vertex.x * centerCycle.scale + centerCycle.deltaX;
            vertex.y = height - vertex.y * centerCycle.scale - centerCycle.delatY;  // y 轴转换
        }

        Polygon area = new Polygon(context);
        this.area = area;
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(width, height);
        area.setLayoutParams(paras);
        parent.addView(area);

        area.setBackgroundColor(0x00000000); // 透明色
        area.setColor(color);
        area.setType(type);
        area.setValue(vertexs);
    }

    /*
    public void drawSideArea(
        Activity activity,
        ViewGroup parent,
        int type
    ) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        for (Vertex vertex : vertexs) {
            //if (vertex.x < 0 || vertex.y < 0) continue;
            vertex.x = vertex.x * centerCycle.scale + centerCycle.deltaX;
            vertex.y = height - vertex.y * centerCycle.scale - centerCycle.delatY;  // y 轴转换
        }

        Polygon area = new Polygon(context);
        this.area = area;
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(width, height);
        area.setLayoutParams(paras);
        parent.addView(area);

        area.setBackgroundColor(0x00000000); // 透明色
        area.setColor(color);
        area.setType(type);
        area.setValue(vertexs);
    }
    */

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

    @Override
    public Geometry getGeometry() {
        Geometry geometry = null;
        if (this.type == 0) {
            List<Vertex> vertexs = this.overtexs;
            Coordinate[] coordPolygon = new Coordinate[vertexs.size() + 1];
            for (int i = 0; i < vertexs.size(); i++) {
                coordPolygon[i] = new Coordinate(vertexs.get(i).x, vertexs.get(i).y);
            }
            coordPolygon[vertexs.size()] = new Coordinate(vertexs.get(0).x, vertexs.get(0).y);
            geometry = new GeometryFactory().createPolygon(coordPolygon);
        }

        if (this.type == 1) {
            List<Vertex> vertexs = this.overtexs;
            Coordinate[] coordCurve = new Coordinate[vertexs.size()];
            for (int i = 0; i < vertexs.size(); i++) {
                coordCurve[i] = new Coordinate(vertexs.get(i).x, vertexs.get(i).y);
            }
            geometry = new GeometryFactory().createLineString(coordCurve);
        }

        return geometry;
    }

}
