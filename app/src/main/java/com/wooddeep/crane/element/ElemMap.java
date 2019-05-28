package com.wooddeep.crane.element;

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

    public BaseElem getElem(String id) {
        return elemMap.get(id);
    }

    public void forEach(Function<String, Boolean> callback) {
        Set<String> idSet = elemMap.keySet();
        for (String id : idSet) {

        }
    }

    public void alarmJudge(String mid) throws Exception {
        CenterCycle cc = (CenterCycle) elemMap.get(mid);
        float cx = cc.x;
        float cy = cc.y;
        float cr = cc.r;
        float cos = (float) Math.cos(Math.toRadians(cc.hAngle));
        float sin = (float) Math.sin(Math.toRadians(cc.hAngle));

        float endpointX = cx + cos * cr;
        float endpointY = cy + sin * cr;

        double isin = Math.sin(Math.toRadians(cc.hAngle + 180));
        double icos = Math.cos(Math.toRadians(cc.hAngle + 180));
        float iendpointX = cx + (float) (cc.ir * icos); // todo 添加垂直方向的斜率计算
        float iendpointY = cy + (float) (cc.ir * isin);

        Geometry gcc = new WKTReader().read(String.format("POINTSTRING (%f %f)", endpointX, endpointY));
        Set<String> idSet = elemMap.keySet();
        for (String id : idSet) {
            if (id.equals(mid)) continue;
            BaseElem elem = elemMap.get(id);
            if (elem instanceof SideCycle) { // cycle
                SideCycle sc = (SideCycle) elemMap.get(id);
                float sx = sc.x;
                float sy = sc.y;
                float sr = sc.r;
                float scos = (float) Math.cos(Math.toRadians(sc.hAngle));
                float ssin = (float) Math.sin(Math.toRadians(sc.hAngle));

                float sendpointX = sx + scos * sr;
                float sendpointY = sy + ssin * sr;

                Geometry gsc = new WKTReader().read(String.format("LINESTRING (%f %f, %f %f)", sx, sy, sendpointX, sendpointY));
                double d = gcc.distance(gsc);
                distanceMap.put(id, (float) d);
            }

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
                        new Coordinate(endpointX, endpointY), new Coordinate(iendpointX, iendpointY),
                        new Coordinate(iendpointX + 1, iendpointX - 1), new Coordinate(endpointX + 1, endpointY - 1),
                        new Coordinate(endpointX, endpointY)
                };

                System.out.println(String.format("%f,%f;%f,%f;%f,%f;%f,%f",
                        vertexs.get(0).x, vertexs.get(0).y, vertexs.get(1).x, vertexs.get(1).y,
                        vertexs.get(2).x, vertexs.get(2).y, vertexs.get(3).x, vertexs.get(3).y));

                System.out.println(String.format("%f,%f;%f,%f;%f,%f;%f,%f",
                        endpointX, endpointY, iendpointX, iendpointY,
                        iendpointX + 1, iendpointY - 1, endpointX + 1, endpointY - 1));

                Geometry gArm = new GeometryFactory().createPolygon(arm);
                boolean intersected = gPolygon.intersects(gArm);

                System.out.println("### endpoint to polygon's distance = " + intersected);
            }
        }

    }
}
