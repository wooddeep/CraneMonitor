package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "calibration") // 指定数据表的名称
public class Load {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    // craneType & armLength & power 三个为联合键值
    @DatabaseField(columnName = "craneType", useGetSet = true, defaultValue = "JL186/12")
    private String craneType;

    @DatabaseField(columnName = "armLength", useGetSet = true, defaultValue = "50") //臂长
    private float armLength;

    @DatabaseField(columnName = "power", useGetSet = true, defaultValue = "1") // 倍率
    private int power;

    // 对应的数据
    @DatabaseField(columnName = "coordinate", useGetSet = true, defaultValue = "50") // 小车坐标
    private float coordinate;

    @DatabaseField(columnName = "weight", useGetSet = true, defaultValue = "1") // 对应吊重量
    private int weight;

    public Load() {
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

    public String getCraneType() {
        return craneType;
    }

    public void setCraneType(String craneType) {
        this.craneType = craneType;
    }

    public float getArmLength() {
        return armLength;
    }

    public void setArmLength(float armLength) {
        this.armLength = armLength;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public float getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(float coordinate) {
        this.coordinate = coordinate;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
