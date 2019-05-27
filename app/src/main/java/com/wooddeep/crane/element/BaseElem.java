package com.wooddeep.crane.element;

import org.locationtech.jts.geom.Geometry;

import java.util.UUID;

abstract public class BaseElem {

    private String uuid;

    public BaseElem() {
        this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    abstract Geometry getGeometry();

}
