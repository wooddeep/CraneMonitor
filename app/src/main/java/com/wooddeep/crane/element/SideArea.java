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
    public String name;
    private GeometryFactory geometryFactory = new GeometryFactory();
    private Coordinate[] coordPolygon ;//= new Coordinate[vertexs.size() + 1];
    private Coordinate[] coordCurve ;//= new Coordinate[vertexs.size()];
    private Geometry geometry = null;

    public SideArea(
        CenterCycle cc,
        int color,
        List<Vertex> vertexs,
        int t,
        float h,
        String name
    ) {
        this.centerCycle = cc;
        this.color = color;
        this.type = t;
        this.height = h;
        this.name = name;
        this.vertexs = vertexs;

        this.overtexs = new ArrayList<>();
        for (Vertex vertex: vertexs) {
            Vertex node = new Vertex(vertex.x, vertex.y);
            overtexs.add(node);
        }
        coordPolygon = new Coordinate[vertexs.size() + 1];
        coordCurve = new Coordinate[vertexs.size()];
    }

    public void drawSideArea(
        Activity activity,
        ViewGroup parent
    ) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度

        if (vertexs.size() == 0) return;

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
        area.setName(name);

        if (this.type == 0) {
            List<Vertex> vertexs = this.overtexs;
            for (int i = 0; i < vertexs.size(); i++) {
                coordPolygon[i] = new Coordinate(vertexs.get(i).x, vertexs.get(i).y);
            }
            coordPolygon[vertexs.size()] = new Coordinate(vertexs.get(0).x, vertexs.get(0).y);
        }

        if (this.type == 1) {
            List<Vertex> vertexs = this.overtexs;
            for (int i = 0; i < vertexs.size(); i++) {
                coordCurve[i] = new Coordinate(vertexs.get(i).x, vertexs.get(i).y);
            }
        }
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

    // TODO
    @Override
    public Geometry getGeometry() {

        if (vertexs.size() == 0) return null;

        if (this.type == 0) {
            geometry = geometryFactory.createPolygon(coordPolygon);
        }

        if (this.type == 1) {
            geometry = geometryFactory.createLineString(coordCurve);
        }

        return geometry;
    }

}
