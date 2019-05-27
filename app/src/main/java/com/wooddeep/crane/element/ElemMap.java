package com.wooddeep.crane.element;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.HashMap;
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

    public BaseElem getElem(String id) {
        return elemMap.get(id);
    }

    public void forEach(Function<String, Boolean> callback) {
        Set<String> idSet = elemMap.keySet();
        for (String id: idSet) {

        }
    }

    public void alarmJudge(String mid) throws Exception {
        CenterCycle cc = (CenterCycle)elemMap.get(mid);
        float cx = cc.x;
        float cy = cc.y;
        float cr  = cc.r;
        float cos = (float)Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float)Math.sin(Math.toRadians(cc.hAngle));

        // 主环中心点的坐标
        float endpointX = cx + cos * cr;
        float endpointY = cy + sin * cr;

        Geometry gcc = new WKTReader().read(String.format("POINTSTRING (%f %f)", endpointX, endpointY));
        Set<String> idSet = elemMap.keySet();
        for (String id: idSet) {
            if (id.equals(mid)) continue;
            BaseElem elem = elemMap.get(id);
            if (elem instanceof SideCycle) { // 次环
                SideCycle sc = (SideCycle)elemMap.get(id);
                float sx = sc.x;
                float sy = sc.y;
                float sr  = sc.r;
                float scos = (float)Math.cos(Math.toRadians(sc.hAngle));
                float ssin = (float)Math.sin(Math.toRadians(sc.hAngle));

                // 次环端点的坐标
                float sendpointX = sx + scos * sr;
                float sendpointY = sy + ssin * sr;

                Geometry gsc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", sx, sy, sendpointX, sendpointY));
                double d = gcc.distance(gsc);
                //System.out.println("$$$ distance: " + d);
                distanceMap.put(id, (float)d);
            }
        }

    }
}
