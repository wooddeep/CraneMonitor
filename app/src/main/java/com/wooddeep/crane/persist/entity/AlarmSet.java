package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "alarm") // 指定数据表的名称
public class AlarmSet {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "t2tDistGear1", useGetSet = true, defaultValue = "1.0f") // 塔基与塔基1挡告警距离
    public float t2tDistGear1;

    @DatabaseField(columnName = "t2tDistGear2", useGetSet = true, defaultValue = "2.0f") // 塔基与塔基1挡告警距离
    public float t2tDistGear2;

    @DatabaseField(columnName = "t2tDistGear3", useGetSet = true, defaultValue = "3.0f") // 塔基与塔基1挡告警距离
    public float t2tDistGear3;

    @DatabaseField(columnName = "t2tDistGear4", useGetSet = true, defaultValue = "4.0f") // 塔基与塔基1挡告警距离
    public float t2tDistGear4;

    @DatabaseField(columnName = "t2tDistGear5", useGetSet = true, defaultValue = "5.0f") // 塔基与塔基1挡告警距离
    public float t2tDistGear5;


    @DatabaseField(columnName = "t2cDistGear1", useGetSet = true, defaultValue = "1.0f") // 塔基与区域1挡告警距离
    public float t2cDistGear1;

    @DatabaseField(columnName = "t2cDistGear2", useGetSet = true, defaultValue = "2.0f") // 塔基与区域1挡告警距离
    public float t2cDistGear2;

    @DatabaseField(columnName = "t2cDistGear3", useGetSet = true, defaultValue = "3.0f") // 塔基与区域1挡告警距离
    public float t2cDistGear3;

    @DatabaseField(columnName = "t2cDistGear4", useGetSet = true, defaultValue = "4.0f") // 塔基与区域1挡告警距离
    public float t2cDistGear4;

    @DatabaseField(columnName = "t2cDistGear5", useGetSet = true, defaultValue = "5.0f") // 塔基与区域1挡告警距离
    public float t2cDistGear5;

    // 小车
    @DatabaseField(columnName = "carSpeedDownDist", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    public float carSpeedDownDist;

    @DatabaseField(columnName = "carStopDist", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float carStopDist;

    // 力矩
    @DatabaseField(columnName = "moment1", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    public float moment1;

    @DatabaseField(columnName = "moment2", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float moment2;

    @DatabaseField(columnName = "moment3", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float moment3;

    // 风速
    @DatabaseField(columnName = "windSpeed1", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    public float windSpeed1;

    @DatabaseField(columnName = "windSpeed2", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float windSpeed2;

    // 吊重
    @DatabaseField(columnName = "weight1", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    public float weight1;

    @DatabaseField(columnName = "weight2", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float weight2;

    @DatabaseField(columnName = "weight3", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float weight3;

    // 大臂长度
    @DatabaseField(columnName = "armLengthMin", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    public float armLengthMin;

    @DatabaseField(columnName = "armLengthMax", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float armLengthMax;

    // 吊钩高度
    @DatabaseField(columnName = "hookHeightMin", useGetSet = true, defaultValue = "4.0f") // 小车减速距离
    public float hookHeightMin;

    @DatabaseField(columnName = "hookHeightMax", useGetSet = true, defaultValue = "5.0f") // 小车停车距离
    public float hookHeightMax;

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

    public float getT2tDistGear1() {
        return t2tDistGear1;
    }

    public void setT2tDistGear1(float t2tDistGear1) {
        this.t2tDistGear1 = t2tDistGear1;
    }

    public float getT2tDistGear2() {
        return t2tDistGear2;
    }

    public void setT2tDistGear2(float t2tDistGear2) {
        this.t2tDistGear2 = t2tDistGear2;
    }

    public float getT2tDistGear3() {
        return t2tDistGear3;
    }

    public void setT2tDistGear3(float t2tDistGear3) {
        this.t2tDistGear3 = t2tDistGear3;
    }

    public float getT2tDistGear4() {
        return t2tDistGear4;
    }

    public void setT2tDistGear4(float t2tDistGear4) {
        this.t2tDistGear4 = t2tDistGear4;
    }

    public float getT2tDistGear5() {
        return t2tDistGear5;
    }

    public void setT2tDistGear5(float t2tDistGear5) {
        this.t2tDistGear5 = t2tDistGear5;
    }

    public float getT2cDistGear1() {
        return t2cDistGear1;
    }

    public void setT2cDistGear1(float t2cDistGear1) {
        this.t2cDistGear1 = t2cDistGear1;
    }

    public float getT2cDistGear2() {
        return t2cDistGear2;
    }

    public void setT2cDistGear2(float t2cDistGear2) {
        this.t2cDistGear2 = t2cDistGear2;
    }

    public float getT2cDistGear3() {
        return t2cDistGear3;
    }

    public void setT2cDistGear3(float t2cDistGear3) {
        this.t2cDistGear3 = t2cDistGear3;
    }

    public float getT2cDistGear4() {
        return t2cDistGear4;
    }

    public void setT2cDistGear4(float t2cDistGear4) {
        this.t2cDistGear4 = t2cDistGear4;
    }

    public float getT2cDistGear5() {
        return t2cDistGear5;
    }

    public void setT2cDistGear5(float t2cDistGear5) {
        this.t2cDistGear5 = t2cDistGear5;
    }

    public float getCarSpeedDownDist() {
        return carSpeedDownDist;
    }

    public void setCarSpeedDownDist(float carSpeedDownDist) {
        this.carSpeedDownDist = carSpeedDownDist;
    }

    public float getCarStopDist() {
        return carStopDist;
    }

    public void setCarStopDist(float carStopDist) {
        this.carStopDist = carStopDist;
    }

    public float getMoment1() {
        return moment1;
    }

    public void setMoment1(float moment1) {
        this.moment1 = moment1;
    }

    public float getMoment2() {
        return moment2;
    }

    public void setMoment2(float monent2) {
        this.moment2 = monent2;
    }

    public float getMoment3() {
        return moment3;
    }

    public void setMoment3(float monent3) {
        this.moment3 = monent3;
    }

    public float getWindSpeed1() {
        return windSpeed1;
    }

    public void setWindSpeed1(float windSpeed1) {
        this.windSpeed1 = windSpeed1;
    }

    public float getWindSpeed2() {
        return windSpeed2;
    }

    public void setWindSpeed2(float windSpeed2) {
        this.windSpeed2 = windSpeed2;
    }

    public float getWeight1() {
        return weight1;
    }

    public void setWeight1(float weight1) {
        this.weight1 = weight1;
    }

    public float getWeight2() {
        return weight2;
    }

    public void setWeight2(float weight2) {
        this.weight2 = weight2;
    }

    public float getWeight3() {
        return weight3;
    }

    public void setWeight3(float weight3) {
        this.weight3 = weight3;
    }

    public float getArmLengthMin() {
        return armLengthMin;
    }

    public void setArmLengthMin(float armLengthMin) {
        this.armLengthMin = armLengthMin;
    }

    public float getArmLengthMax() {
        return armLengthMax;
    }

    public void setArmLengthMax(float armLengthMax) {
        this.armLengthMax = armLengthMax;
    }

    public float getHookHeightMin() {
        return hookHeightMin;
    }

    public void setHookHeightMin(float hookHeightMin) {
        this.hookHeightMin = hookHeightMin;
    }

    public float getHookHeightMax() {
        return hookHeightMax;
    }

    public void setHookHeightMax(float hookHeightMax) {
        this.hookHeightMax = hookHeightMax;
    }
}
