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

    public static void craneToCraneAlarm(HashMap<String, CycleElem> craneMap, String no, float limit) {

        CenterCycle cc = (CenterCycle) craneMap.get(no);

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

        //Geometry gcc = new WKTReader().read(String.format("POINTSTRING (%f %f)", endpointX, endpointY)); // 本环端点
        Set<String> idSet = craneMap.keySet();
        for (String id : idSet) {
            if (id.equals(no)) continue;

            BaseElem elem = craneMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) craneMap.get(id);
                float sideHeight = sc.height;
                float sx = sc.x;
                float sy = sc.y;
                float sr = sc.r;
                float scos = (float) Math.cos(Math.toRadians(sc.hAngle));
                float ssin = (float) Math.sin(Math.toRadians(sc.hAngle));

                float sendpointX = sx + scos * sr;
                float sendpointY = sy + ssin * sr;

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
            }
        }
    }
}
