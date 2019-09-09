package com.wooddeep.crane.element;

import org.locationtech.jts.geom.Geometry;

abstract public class CycleElem extends BaseElem {
    public boolean online = false;
    public int type = 0; // ∆Ω±€ Ω
    public float archPara = 0.0f;
    private float bigArmLen = 0.0f;
    public float orgHeight = 0.0f;

    abstract public void setCarRange(float range);
    abstract public void setHAngle(float angle);
    abstract public void setVAngle(float angle);
    abstract public void setHeight(float height);

    abstract public float getHAngle();
    abstract public float getVAngle();
    abstract public Geometry getCenterGeo() throws Exception;
    abstract public Geometry getArmGeo(float dAngle) throws Exception;
    abstract public Geometry getCarGeo(float dAngle, float dDist) throws Exception;
    abstract public void setColor(int color) ;
    abstract public int getColor();

    public void setOnline(boolean online) {
        this.online = online;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setArchPara(float ap) {
        this.archPara = ap;
    }

    public float getArchPara() {
        return this.archPara;
    }

    public void setBigArmLen(float bal) {
        this.bigArmLen = bal;
    }

    public float getBigArmLen() {
        return this.bigArmLen;
    }

    public float getOrgHeight() {
        return orgHeight;
    }

    public void setOrgHeight(float orgHeight) {
        this.orgHeight = orgHeight;
    }
}
