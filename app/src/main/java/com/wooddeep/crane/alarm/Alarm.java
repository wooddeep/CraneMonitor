package com.wooddeep.crane.alarm;

import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.views.Vertex;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKTReader;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Alarm {

    public static Geometry getCcArmGeo(CycleElem cycleElem, float dAngle) throws Exception {
        CenterCycle cc = (CenterCycle) cycleElem;
        float cos = (float) Math.cos(Math.toRadians(cc.hAngle + dAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle + dAngle));
        float endpointX = cc.x + cos * cc.r; // 本塔基 大臂端点 X 坐标
        float endpointY = cc.y + sin * cc.r; // 本塔基 大臂端点 Y 坐标
        float icos = (float) Math.cos(Math.toRadians(cc.hAngle + 180 + dAngle));
        float isin = (float) Math.sin(Math.toRadians(cc.hAngle + 180 + dAngle));
        float startpointX = cc.x + (float) (cc.ir * icos); // todo 添加垂直方向的斜率计算
        float startpointY = cc.y + (float) (cc.ir * isin);
        Geometry gcc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", startpointX, startpointY, endpointX, endpointY));
        return gcc;
    }

    public static Geometry getCcCarGeo(CycleElem cycleElem) throws Exception {
        CenterCycle cc = (CenterCycle) cycleElem;
        float cos = (float) Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle));
        Geometry carPos = new WKTReader().read(String.format("POINTSTRING (%f %f)", cc.x + cos * cc.carRange, cc.y + sin * cc.carRange));
        return carPos;
    }

    public static Geometry getScArmGeo(CycleElem cycleElem, float dAngle) throws Exception {
        SideCycle cc = (SideCycle) cycleElem;
        float cos = (float) Math.cos(Math.toRadians(cc.hAngle + dAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle + dAngle));
        float endpointX = cc.x + cos * cc.r; // 本塔基 大臂端点 X 坐标
        float endpointY = cc.y + sin * cc.r; // 本塔基 大臂端点 Y 坐标
        float icos = (float) Math.cos(Math.toRadians(cc.hAngle + 180 + dAngle));
        float isin = (float) Math.sin(Math.toRadians(cc.hAngle + 180 + dAngle));
        float startpointX = cc.x + (float) (cc.ir * icos); // todo 添加垂直方向的斜率计算
        float startpointY = cc.y + (float) (cc.ir * isin);
        Geometry gcc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", startpointX, startpointY, endpointX, endpointY));
        return gcc;
    }

    public static Geometry getScCarGeo(CycleElem cycleElem) throws Exception {
        SideCycle cc = (SideCycle) cycleElem;
        float cos = (float) Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle));
        Geometry carPos = new WKTReader().read(String.format("POINTSTRING (%f %f)", cc.x + cos * cc.carRange, cc.y + sin * cc.carRange));
        return carPos;
    }

    public static CenterCycle craneToCraneAlarm(HashMap<String, CycleElem> craneMap, String no, AlarmSet alarmSet) throws Exception {

        CenterCycle cc = (CenterCycle) craneMap.get(no);

        Geometry gcc = getCcArmGeo(cc, 0f);
        float myHeight = cc.height; // 本塔高度
        Coordinate myCenter = new Coordinate(cc.x, cc.y);

        Set<String> idSet = craneMap.keySet();
        for (String id : idSet) {
            if (id.equals(no)) continue;
            BaseElem elem = craneMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) craneMap.get(id);

                Coordinate sideCenter = new Coordinate(sc.x, sc.y);
                double cTocDist = myCenter.distance(sideCenter);
                if (cTocDist > (cc.r + sc.r)) { // 两塔基距离大于两塔基大臂之和, 则不进行判断
                    System.out.println("## center to center distance big than arms sum!");
                    continue;
                }

                Geometry gsc = getScArmGeo(sc, 0f);
                float sideHeight = sc.height;

                if (Math.abs(myHeight - sideHeight) <= 1) { // 高度差相差1m, 当成等高, 查看当前圆心和对端 大臂端点的距离, 无前后告警, 只有左右告警
                    //boolean intersect = gcc.intersects(gsc);
                    float distance = (float) gcc.distance(gsc);
                    if (distance < alarmSet.getT2cDistGear1()) { // TODO: 此处告警距离采取的是1挡告警距离，需要根据实际的档位来设定告警距离
                        //System.out.printf("### center arm to side[%s] arm distance alarm!\n", id);
                        // 判断左告警 还是 右告警
                        Geometry gPredect = getCcArmGeo(cc, 0.1f); // 逆时针旋转
                        float distPred = (float)gPredect.distance(gsc);
                        if (distPred < distance) { // 逆时针旋转 距离告警，则是 左转告警
                            System.out.printf("### center turn left to [%s] alarm!!!\n", id);
                        } else {
                            System.out.printf("### center turn right to [%s] alarm!!!\n", id);
                        }
                    }
                } else if ((myHeight - sideHeight) > 1) { // 中心塔基比边缘塔基高, 计算中心塔基小车位置和 边缘塔基距离
                    Geometry carPos =  getCcCarGeo(cc);
                    float carToArmDis = (float) carPos.distance(gsc);
                    System.out.printf("### center car to side[%s] arm distance: %f \n", id, carToArmDis);
                } else {
                    Geometry carPos = getScCarGeo(sc);
                    float carToArmDis = (float) carPos.distance(gcc);
                    System.out.printf("### side[%s] car to center arm distance: %f \n", id, carToArmDis);
                }
            }
        }
        return cc;
    }

    public static void craneToAreaAlarm(List<BaseElem> elems, CenterCycle cc, AlarmSet alarmSet) throws Exception {

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
