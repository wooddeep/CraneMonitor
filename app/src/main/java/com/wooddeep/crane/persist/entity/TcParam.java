package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "tcparam") // 指定数据表的名称
public class TcParam {

    @DatabaseField(generatedId = true, columnName = "No", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "Rate", useGetSet = true, defaultValue = "2") // 倍率
    private String power;

    @DatabaseField(columnName = "Length", useGetSet = true, defaultValue = "50") //臂长
    private String armLength;

    // 对应的数据
    @DatabaseField(columnName = "Distance", useGetSet = true, defaultValue = "50") // 小车坐标
    private String coordinate;

    @DatabaseField(columnName = "Weight", useGetSet = true, defaultValue = "2") // 对应吊重量
    private String weight;

    // craneType & armLength & power 三个为联合键值
    @DatabaseField(columnName = "Type", useGetSet = true, defaultValue = "JL186/12")
    private String craneType;


    public TcParam() {
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

    public String getArmLength() {
        return armLength;
    }

    public void setArmLength(String armLength) {
        this.armLength = armLength;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public TcParam(String craneType, String armLength, String power, String coordinate, String weight) {
        this.craneType = craneType;
        this.armLength = armLength;
        this.power = power;
        this.coordinate = coordinate;
        this.weight = weight;
    }

    public static TcParam getInitData() {
        return new TcParam(
            "DEMO_CRANE",
            "50",
            "2",
            "35",
            "5.6"
        );
    }



}
