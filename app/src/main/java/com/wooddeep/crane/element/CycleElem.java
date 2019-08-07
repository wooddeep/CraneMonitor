package com.wooddeep.crane.element;

import org.locationtech.jts.geom.Geometry;

abstract public class CycleElem extends BaseElem {
    abstract public void setCarRange(float range);
    abstract public void setHAngle(float angle);
    abstract public float getHAngle();
    abstract public Geometry getCenterGeo() throws Exception;
    abstract public Geometry getArmGeo(float dAngle) throws Exception;
    abstract public Geometry getCarGeo(float dAngle, float dDist) throws Exception;
}
