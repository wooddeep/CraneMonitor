package com.wooddeep.crane.alarm;

import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.views.Vertex;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Alarm {

    public static CenterCycle craneToCraneAlarm(HashMap<String, CycleElem> craneMap, String no, float limit) throws Exception {

        CenterCycle cc = (CenterCycle) craneMap.get(no);

        float cos = (float) Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle));

        float endpointX = cc.x + cos * cc.r; // 本塔基 大臂端点 X 坐标
        float endpointY = cc.y + sin * cc.r; // 本塔基 大臂端点 Y 坐标

        float icos = (float) Math.cos(Math.toRadians(cc.hAngle + 180));
        float isin = (float) Math.sin(Math.toRadians(cc.hAngle + 180));

        float startpointX = cc.x + (float) (cc.ir * icos); // todo 添加垂直方向的斜率计算
        float startpointY = cc.y + (float) (cc.ir * isin);

        float myHeight = cc.height; // 本塔高度

        Geometry gcc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", startpointX, startpointY, endpointX, endpointY));

        Set<String> idSet = craneMap.keySet();
        for (String id : idSet) {
            if (id.equals(no)) continue;
            BaseElem elem = craneMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) craneMap.get(id);
                float sideHeight = sc.height;
                float cos2 = (float) Math.cos(Math.toRadians(sc.hAngle));
                float sin2 = (float) Math.sin(Math.toRadians(sc.hAngle));

                float xendpointX = sc.x + cos2 * sc.r;
                float xendpointY = sc.y + sin2 * sc.r;

                float icos2 = (float) Math.cos(Math.toRadians(sc.hAngle + 180));
                float isin2 = (float) Math.sin(Math.toRadians(sc.hAngle + 180));

                float xstartpointX = sc.x + sc.ir * icos2;
                float xstartpointY = sc.y + sc.ir * isin2;

                Geometry gsc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", xstartpointX, xstartpointY, xendpointX, xendpointY));

                boolean intersect = gcc.intersects(gsc);
                float distance = (float) gcc.distance(gsc);

                //System.out.printf("### intersect: %b, distance = %f\n", intersect, distance);

                /*
                if (Math.abs(myHeight - sideHeight) <= 1) { // 高度差相差1m, 当成等高, 查看当前圆心和对端 大臂端点的距离
                    Coordinate coord1 = new Coordinate(cx, cy);
                    Coordinate coord2 = new Coordinate(sendpointX, sendpointY);
                    float dis = (float)coord1.distance(coord2);
                    if (dis <= cc.bigArm) { // 圆心到 端点的距离

                    }

                    System.out.printf("### distance = %f\n", dis);
                    if (dis <= limit) {
                        System.out.println("##### distance alarm .....");
                    }

                } else if ((myHeight - sideHeight) > 1) {

                } else {

                }
                */
            }
        }
        return cc;
    }

    public static void craneToAreaAlarm(List<BaseElem> elems, CenterCycle cc, float limit) throws Exception {

        float cos = (float) Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle));

        float endpointX = cc.x + cos * cc.r; // 本塔基 大臂端点 X 坐标
        float endpointY = cc.y + sin * cc.r; // 本塔基 大臂端点 Y 坐标

        float icos = (float) Math.cos(Math.toRadians(cc.hAngle + 180));
        float isin = (float) Math.sin(Math.toRadians(cc.hAngle + 180));

        float startpointX = cc.x + (float) (cc.ir * icos); // todo 添加垂直方向的斜率计算
        float startpointY = cc.y + (float) (cc.ir * isin);

        float myHeight = cc.height; // 本塔高度

        Geometry gcc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", startpointX, startpointY, endpointX, endpointY));

        for (BaseElem elem : elems) {
            SideArea sa = (SideArea) elem;

            //float sideHeight = sa.height;
            if (sa.type == 0) continue;

            if (sa.type == 0) {
                List<Vertex> vertexs = sa.overtexs;
                Coordinate[] coordPolygon = new Coordinate[vertexs.size() + 1];
                for (int i = 0; i < vertexs.size(); i++) {
                    coordPolygon[i] = new Coordinate(vertexs.get(i).x, vertexs.get(i).y);
                }
                coordPolygon[vertexs.size()] = new Coordinate(vertexs.get(0).x, vertexs.get(0).y);
                Geometry gPolygon = new GeometryFactory().createPolygon(coordPolygon);

                boolean intersect = gcc.intersects(gPolygon);
                float distance = (float) gcc.distance(gPolygon);

                System.out.printf("@@@ intersect: %b, distance = %f\n", intersect, distance);
            }

            if (sa.type == 1) {
                List<Vertex> vertexs = sa.overtexs;
                Coordinate[] coordCurve = new Coordinate[vertexs.size()];
                for (int i = 0; i < vertexs.size(); i++) {
                    coordCurve[i] = new Coordinate(vertexs.get(i).x, vertexs.get(i).y);
                }
                Geometry gCurve = new GeometryFactory().createLineString(coordCurve);

                boolean intersect = gcc.intersects(gCurve);
                float distance = (float) gcc.distance(gCurve);

                System.out.printf("@@@ intersect: %b, distance = %f\n", intersect, distance);
            }

            /*
            if (Math.abs(myHeight - sideHeight) <= 1) { // 高度差相差1m, 当成等高, 查看当前圆心和对端 大臂端点的距离
                Coordinate coord1 = new Coordinate(cx, cy);
                Coordinate coord2 = new Coordinate(sendpointX, sendpointY);
                float dis = (float)coord1.distance(coord2);
                if (dis <= cc.bigArm) { // 圆心到 端点的距离

                }

                System.out.printf("### distance = %f\n", dis);
                if (dis <= limit) {
                    System.out.println("##### distance alarm .....");
                }

            } else if ((myHeight - sideHeight) > 1) {

            } else {

            }
            */

        }
    }
}
