package com.wooddeep.crane.element;

import android.view.View;
import android.view.ViewGroup;

import com.wooddeep.crane.views.Vertex;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ElemMap {
    private HashMap<String, BaseElem> elemMap;

    public HashMap<String, Float> distanceMap;

    public ElemMap() {
        elemMap = new HashMap<>();
        distanceMap = new HashMap<>();
    }

    public void addElem(String id, BaseElem elem) {
        elemMap.put(id, elem);
    }

    public void delElems(ViewGroup parent) {
        Set<String> idSet = elemMap.keySet();
        for (String id : idSet) {
            BaseElem elem = elemMap.get(id);

            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) elemMap.get(id);
                parent.removeView(sc.cycle);
            }

            if (elem instanceof SideArea) { // cycle
                SideArea sa = (SideArea) elemMap.get(id);
                parent.removeView(sa.area);
            }

            if (elem instanceof CenterCycle) {
                CenterCycle cc = (CenterCycle) elemMap.get(id);
                parent.removeView(cc.cycle);
            }
        }
    }

    public BaseElem getElem(String id) {
        return elemMap.get(id);
    }

    public void forEach(Function<String, Boolean> callback) {
        Set<String> idSet = elemMap.keySet();
        for (String id : idSet) {

        }
    }

    public void alarmJudge(String mid, float limit) throws Exception {
        CenterCycle cc = (CenterCycle) elemMap.get(mid);
        float cx = cc.x;
        float cy = cc.y;
        float cr = cc.r;
        float cos = (float) Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle));

        float endpointX = cx + cos * cr; // 本塔基 大臂端点 X 坐标
        float endpointY = cy + sin * cr; // 本塔基 大臂端点 Y 坐标

        double isin = Math.sin(Math.toRadians(cc.hAngle + 180));
        double icos = Math.cos(Math.toRadians(cc.hAngle + 180));
        float iendpointX = cx + (float) (cc.ir * icos); // todo 添加垂直方向的斜率计算
        float iendpointY = cy + (float) (cc.ir * isin);

        float myHeight = cc.height; // 本塔高度

        Geometry gcc = new WKTReader().read(String.format("POINTSTRING (%f %f)", endpointX, endpointY)); // 本环端点
        Set<String> idSet = elemMap.keySet();
        for (String id : idSet) {
            if (id.equals(mid)) continue;
            BaseElem elem = elemMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) elemMap.get(id);
                float sideHeight = sc.height;

                float sx = sc.x;
                float sy = sc.y;
                float sr = sc.r;
                float scos = (float) Math.cos(Math.toRadians(sc.hAngle));
                float ssin = (float) Math.sin(Math.toRadians(sc.hAngle));

                System.out.printf("#### angle = %f\n", sc.hAngle);

                /*
                float sendpointX = sx + scos * sr;
                float sendpointY = sy + ssin * sr;

                if (Math.abs(myHeight - sideHeight) <= 1) { // 高度差相差1m, 当成等高, 查看当前圆心和对端 大臂端点的距离
                    Coordinate coord1 = new Coordinate(cx, cy);
                    Coordinate coord2 = new Coordinate(sendpointX, sendpointY);
                } else if ((myHeight - sideHeight) > 1) {

                } else {

                }

                Geometry gsc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", sx, sy, sendpointX, sendpointY)); // 侧环大臂
                double d = gcc.distance(gsc);
                distanceMap.put(id, (float) d);
                if (limit >= (float)d) {
                    sc.setAlarm(true);
                } else {
                    sc.setAlarm(false);
                }
                */
            }

            /*
            if (elem instanceof SideArea) { // polygon
                SideArea sa = (SideArea) elemMap.get(id);
                List<Vertex> vertexs = sa.overtexs;
                Coordinate[] coordPolygon = new Coordinate[vertexs.size() + 1];
                for (int i = 0; i < vertexs.size(); i++) {
                    coordPolygon[i] = new Coordinate(vertexs.get(i).x, vertexs.get(i).y);
                }
                coordPolygon[vertexs.size()] = new Coordinate(vertexs.get(0).x, vertexs.get(0).y);
                Geometry gPolygon = new GeometryFactory().createPolygon(coordPolygon);

                Coordinate[] arm = new Coordinate[]{
                        new Coordinate(endpointX, endpointY),
                        new Coordinate(iendpointX, iendpointY),
                        new Coordinate(iendpointX + 0.01, iendpointX - 0.01), // 一厘米的偏移, 构造一个矩形
                        new Coordinate(endpointX + 0.01, endpointY - 0.01),
                        new Coordinate(endpointX, endpointY)
                };

                Geometry gArm = new GeometryFactory().createPolygon(arm);
                //boolean intersected = gPolygon.intersects(gArm);
                double distance = gArm.distance(gPolygon);
                if (limit >= (float)distance) {
                    sa.setAlarm(true);
                } else {
                    sa.setAlarm(false);
                }
                //System.out.println(String.format("### distance: %f, intersected status: %b", distance, intersected));
            }
            */
        }
    }

    public void alramFlink() {
        Set<String> idSet = elemMap.keySet();
        CenterCycle cc = null;
        boolean hasAlarm = false;
        for (String id : idSet) {
            BaseElem elem = elemMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) elemMap.get(id);
                if (sc.getAlarm()) {
                    hasAlarm = true;
                    sc.setFlink(!sc.getFlink());
                }
            }

            if (elem instanceof SideArea) { // cycle
                SideArea sa = (SideArea) elemMap.get(id);
                if (sa.getAlarm()) {
                    hasAlarm = true;
                    sa.setFlink(!sa.getFlink());
                }
            }

            if (elem instanceof CenterCycle) {
                cc = (CenterCycle) elemMap.get(id);
            }
        }

        cc.setAlarm(hasAlarm);
        if (hasAlarm) {
            cc.setFlink(!cc.getFlink());
        }
    }
}
