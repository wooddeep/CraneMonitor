package com.wooddeep.crane.persist.entity;

@SuppressWarnings("unused")
public class CranePara {
    private int id;
    private String name;
    private int type; // 塔基类型
    private float coordX1;
    private float coordY1;
    private float coordX2;
    private float coordY2;
    private float CraneHeight;
    private float BigArmLength;
    private float BalancArmLength;
    private float CraneBodyRadius;
    private float BigArmWidth;
    private float BalancArmWidth;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Float getCoordX1() {
        return coordX1;
    }

    public void setCoordX1(Float coordX1) {
        this.coordX1 = coordX1;
    }

    public Float getCoordY1() {
        return coordY1;
    }

    public void setCoordY1(Float coordY1) {
        this.coordY1 = coordY1;
    }

    public Float getCoordX2() {
        return coordX2;
    }

    public void setCoordX2(Float coordX2) {
        this.coordX2 = coordX2;
    }

    public Float getCoordY2() {
        return coordY2;
    }

    public void setCoordY2(Float coordY2) {
        this.coordY2 = coordY2;
    }

    public Float getCraneHeight() {
        return CraneHeight;
    }

    public void setCraneHeight(Float craneHeight) {
        CraneHeight = craneHeight;
    }

    public Float getBigArmLength() {
        return BigArmLength;
    }

    public void setBigArmLength(Float bigArmLength) {
        BigArmLength = bigArmLength;
    }

    public Float getBalancArmLength() {
        return BalancArmLength;
    }

    public void setBalancArmLength(Float balancArmLength) {
        BalancArmLength = balancArmLength;
    }

    public Float getCraneBodyRadius() {
        return CraneBodyRadius;
    }

    public void setCraneBodyRadius(Float craneBodyRadius) {
        CraneBodyRadius = craneBodyRadius;
    }

    public Float getBigArmWidth() {
        return BigArmWidth;
    }

    public void setBigArmWidth(Float bigArmWidth) {
        BigArmWidth = bigArmWidth;
    }

    public Float getBalancArmWidth() {
        return BalancArmWidth;
    }

    public void setBalancArmWidth(Float balancArmWidth) {
        BalancArmWidth = balancArmWidth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CranePara() {
        super();
    }

    public CranePara(int id, String name, int type, float coordX1, float coordY1,
                     float coordX2, float coordY2, float CraneHeight, float BigArmLength,
                     float BalancArmLength, float CraneBodyRadius, float BigArmWidth, float BalancArmWidth) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.coordX1 = coordX1;
        this.coordY1 = coordY1;
        this.coordX2 = coordX2;
        this.coordY2 = coordY2;
        this.CraneHeight = CraneHeight;
        this.BigArmLength = BigArmLength;
        this.BalancArmLength = BalancArmLength;
        this.CraneBodyRadius = CraneBodyRadius;
        this.BigArmWidth = BigArmWidth;
        this.BalancArmWidth = BalancArmWidth;
    }

    @Override
    public String toString() {
        return String.format("## id: %d-name: %s", this.id, this.name);
    }
}
