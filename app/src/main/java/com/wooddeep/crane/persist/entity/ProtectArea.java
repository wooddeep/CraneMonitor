package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "calibration") // 指定数据表的名称
public class ProtectArea {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    // 保护区编号
    @DatabaseField(columnName = "areaNo", useGetSet = true, defaultValue = "1")
    private String areaNo;

    // 坐标点编号
    @DatabaseField(columnName = "pointNo", useGetSet = true, defaultValue = "1") //臂长
    private float pointNo;

    // 对应点的X坐标
    @DatabaseField(columnName = "coordinateX", useGetSet = true, defaultValue = "50") // 小车坐标
    private float coordinateX;

    // 对应点的Y坐标
    @DatabaseField(columnName = "coordinateY", useGetSet = true, defaultValue = "50") // 对应吊重量
    private int coordinateY;

    public ProtectArea() {
        super();
    }

    @Override
    public String toString() {
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }

    public float getPointNo() {
        return pointNo;
    }

    public void setPointNo(float pointNo) {
        this.pointNo = pointNo;
    }

    public float getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(float coordinateX) {
        this.coordinateX = coordinateX;
    }

    public int getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }
}
