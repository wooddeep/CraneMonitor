package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "calibration") // 指定数据表的名称
public class AlarmSet {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "t2tDistGear1", useGetSet = true, defaultValue = "1.0f") // 塔基与塔基1挡告警距离
    private float t2tDistGear1;

    @DatabaseField(columnName = "t2tDistGear2", useGetSet = true, defaultValue = "2.0f") // 塔基与塔基1挡告警距离
    private float t2tDistGear2;

    @DatabaseField(columnName = "t2tDistGear3", useGetSet = true, defaultValue = "3.0f") // 塔基与塔基1挡告警距离
    private float t2tDistGear3;

    @DatabaseField(columnName = "t2tDistGear4", useGetSet = true, defaultValue = "4.0f") // 塔基与塔基1挡告警距离
    private float t2tDistGear4;

    @DatabaseField(columnName = "t2tDistGear5", useGetSet = true, defaultValue = "5.0f") // 塔基与塔基1挡告警距离
    private float t2tDistGear5;


    @DatabaseField(columnName = "t2cDistGear1", useGetSet = true, defaultValue = "1.0f") // 塔基与区域1挡告警距离
    private float t2cDistGear1;

    @DatabaseField(columnName = "t2cDistGear2", useGetSet = true, defaultValue = "2.0f") // 塔基与区域1挡告警距离
    private float t2cDistGear2;

    @DatabaseField(columnName = "t2cDistGear3", useGetSet = true, defaultValue = "3.0f") // 塔基与区域1挡告警距离
    private float t2cDistGear3;

    @DatabaseField(columnName = "t2cDistGear4", useGetSet = true, defaultValue = "4.0f") // 塔基与区域1挡告警距离
    private float t2cDistGear4;

    @DatabaseField(columnName = "t2cDistGear5", useGetSet = true, defaultValue = "5.0f") // 塔基与区域1挡告警距离
    private float t2cDistGear5;

    // 小车
    @DatabaseField(columnName = "carSpeedDownDist", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    private float carSpeedDownDist;

    @DatabaseField(columnName = "carStopDist", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float carStopDist;

    // 力矩
    @DatabaseField(columnName = "moment1", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    private float moment1;

    @DatabaseField(columnName = "monent2", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float monent2;

    @DatabaseField(columnName = "monent3", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float monent3;

    // 风速
    @DatabaseField(columnName = "windSpeed1", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    private float windSpeed1;

    @DatabaseField(columnName = "windSpeed2", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float windSpeed2;

    // 吊重
    @DatabaseField(columnName = "weight1", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    private float weight1;

    @DatabaseField(columnName = "weight2", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float weight2;

    @DatabaseField(columnName = "weight3", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float weight3;

    // 大臂长度
    @DatabaseField(columnName = "armLengthMin", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    private float armLengthMin;

    @DatabaseField(columnName = "armLengthMax", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float armLengthMax;

    // 吊钩高度
    @DatabaseField(columnName = "hookHeightMin", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    private float hookHeightMin;

    @DatabaseField(columnName = "hookHeightMax", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    private float hookHeightMax;

    public AlarmSet() {
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

}
