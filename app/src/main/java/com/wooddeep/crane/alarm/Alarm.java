package com.wooddeep.crane.alarm;

import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.SideCycle;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.HashMap;
import java.util.Set;

public class Alarm {

    public static void craneToCraneAlarm(HashMap<String, CycleElem> craneMap, String no, float limit) throws Exception {

        CenterCycle cc = (CenterCycle) craneMap.get(no);

        float cos = (float) Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle));

        float endpointX = cc.x + cos * cc.r; // 本塔基 大臂端点 X 坐标
        float endpointY = cc.y + sin * cc.r; // 本塔基 大臂端点 Y 坐标

        float icos = (float)Math.cos(Math.toRadians(cc.hAngle + 180));
        float isin = (float)Math.sin(Math.toRadians(cc.hAngle + 180));

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
                float distance = (float)gcc.distance(gsc);

                System.out.printf("### intersect: %b, distance = %f\n", intersect, distance);

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
}
