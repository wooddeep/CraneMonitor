package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "calibration") // 指定数据表的名称
public class Calibration {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "rotateStartX1", useGetSet = true, defaultValue = "-1.0f")
    public float rotateStartX1;

    @DatabaseField(columnName = "rotateStartY1", useGetSet = true, defaultValue = "-1.0f")
    public float rotateStartY1;

    @DatabaseField(columnName = "rotateStartData", useGetSet = true, defaultValue = "-1.0f") // data代表从串口中解析到的数据
    public float rotateStartData;

    @DatabaseField(columnName = "rotateEndX2", useGetSet = true, defaultValue = "-1.0f")
    public float rotateEndX2;

    @DatabaseField(columnName = "rotateEndY2", useGetSet = true, defaultValue = "-1.0f")
    public float rotateEndY2;

    @DatabaseField(columnName = "rotateEndData", useGetSet = true, defaultValue = "-1.0f")
    public float rotateEndData;

    @DatabaseField(columnName = "rotateRate", useGetSet = true, defaultValue = "-1.0f")
    public float rotateRate;

    @DatabaseField(columnName = "GearRate1", useGetSet = true, defaultValue = "-1.0f")
    public float GearRate1;

    @DatabaseField(columnName = "GearRate2", useGetSet = true, defaultValue = "-1.0f")
    public float GearRate2;

    @DatabaseField(columnName = "GearRate3", useGetSet = true, defaultValue = "-1.0f")
    public float GearRate3;

    @DatabaseField(columnName = "GearRate4", useGetSet = true, defaultValue = "-1.0f")
    public float GearRate4;

    @DatabaseField(columnName = "GearRate5", useGetSet = true, defaultValue = "-1.0f")
    public float GearRate5;

    // 倾角
    @DatabaseField(columnName = "dipAngleStart", useGetSet = true, defaultValue = "-1.0f")
    public float dipAngleStart;

    @DatabaseField(columnName = "dipAngleStartData", useGetSet = true, defaultValue = "-1.0f") // data代表从串口中解析到的数据
    public float dipAngleStartData;

    @DatabaseField(columnName = "dipAngleEnd", useGetSet = true, defaultValue = "-1.0f")
    public float dipAngleEnd;

    @DatabaseField(columnName = "dipAngleEndData", useGetSet = true, defaultValue = "-1.0f")
    public float dipAngleEndData;

    @DatabaseField(columnName = "dipAngleRate", useGetSet = true, defaultValue = "-1.0f")
    public float dipAngleRate;

    // 重量
    @DatabaseField(columnName = "weightStart", useGetSet = true, defaultValue = "-1.0f")
    public float weightStart;

    @DatabaseField(columnName = "weightStartData", useGetSet = true, defaultValue = "-1.0f") // data代表从串口中解析到的数据
    public float weightStartData;

    @DatabaseField(columnName = "weightEnd", useGetSet = true, defaultValue = "-1.0f")
    public float weightEnd;

    @DatabaseField(columnName = "weightEndData", useGetSet = true, defaultValue = "-1.0f")
    public float weightEndData;

    @DatabaseField(columnName = "weightRate", useGetSet = true, defaultValue = "-1.0f")
    public float weightRate;

    // 小车长度
    @DatabaseField(columnName = "lengthStart", useGetSet = true, defaultValue = "-1.0f")
    public float lengthStart;

    @DatabaseField(columnName = "lengthStartData", useGetSet = true, defaultValue = "-1.0f") // data代表从串口中解析到的数据
    public float lengthStartData;

    @DatabaseField(columnName = "lengthEnd", useGetSet = true, defaultValue = "-1.0f")
    public float lengthEnd;

    @DatabaseField(columnName = "lengthEndData", useGetSet = true, defaultValue = "-1.0f")
    public float lengthEndData;

    @DatabaseField(columnName = "lengthRate", useGetSet = true, defaultValue = "-1.0f")
    public float lengthRate;

    // 小车高度
    @DatabaseField(columnName = "heightStart", useGetSet = true, defaultValue = "-1.0f")
    public float heightStart;

    @DatabaseField(columnName = "heightStartData", useGetSet = true, defaultValue = "-1.0f") // data代表从串口中解析到的数据
    public float heightStartData;

    @DatabaseField(columnName = "heightEnd", useGetSet = true, defaultValue = "-1.0f")
    public float heightEnd;

    @DatabaseField(columnName = "heightEndData", useGetSet = true, defaultValue = "-1.0f")
    public float heightEndData;

    @DatabaseField(columnName = "heightRate", useGetSet = true, defaultValue = "-1.0f")
    public float heightRate;

    public Calibration() {
        super();
    }

    public Calibration(float rotateStartX1) {
        super();
        this.rotateStartX1 = rotateStartX1;
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

    public float getRotateStartX1() {
        return rotateStartX1;
    }

    public void setRotateStartX1(float rotateStartX1) {
        this.rotateStartX1 = rotateStartX1;
    }

    public float getRotateStartY1() {
        return rotateStartY1;
    }

    public void setRotateStartY1(float rotateStartY1) {
        this.rotateStartY1 = rotateStartY1;
    }

    public float getRotateStartData() {
        return rotateStartData;
    }

    public void setRotateStartData(float rotateStartData) {
        this.rotateStartData = rotateStartData;
    }

    public float getRotateEndX2() {
        return rotateEndX2;
    }

    public void setRotateEndX2(float rotateEndX2) {
        this.rotateEndX2 = rotateEndX2;
    }

    public float getRotateEndY2() {
        return rotateEndY2;
    }

    public void setRotateEndY2(float rotateEndY2) {
        this.rotateEndY2 = rotateEndY2;
    }

    public float getRotateEndData() {
        return rotateEndData;
    }

    public void setRotateEndData(float rotateEndData) {
        this.rotateEndData = rotateEndData;
    }

    public float getRotateRate() {
        return rotateRate;
    }

    public void setRotateRate(float rotateRate) {
        this.rotateRate = rotateRate;
    }

    public float getGearRate1() {
        return GearRate1;
    }

    public void setGearRate1(float gearRate1) {
        GearRate1 = gearRate1;
    }

    public float getGearRate2() {
        return GearRate2;
    }

    public void setGearRate2(float gearRate2) {
        GearRate2 = gearRate2;
    }

    public float getGearRate3() {
        return GearRate3;
    }

    public void setGearRate3(float gearRate3) {
        GearRate3 = gearRate3;
    }

    public float getGearRate4() {
        return GearRate4;
    }

    public void setGearRate4(float gearRate4) {
        GearRate4 = gearRate4;
    }

    public float getGearRate5() {
        return GearRate5;
    }

    public void setGearRate5(float gearRate5) {
        GearRate5 = gearRate5;
    }

    public float getDipAngleStart() {
        return dipAngleStart;
    }

    public void setDipAngleStart(float dipAngleStart) {
        this.dipAngleStart = dipAngleStart;
    }

    public float getDipAngleStartData() {
        return dipAngleStartData;
    }

    public void setDipAngleStartData(float dipAngleStartData) {
        this.dipAngleStartData = dipAngleStartData;
    }

    public float getDipAngleEnd() {
        return dipAngleEnd;
    }

    public void setDipAngleEnd(float dipAngleEnd) {
        this.dipAngleEnd = dipAngleEnd;
    }

    public float getDipAngleEndData() {
        return dipAngleEndData;
    }

    public void setDipAngleEndData(float dipAngleEndData) {
        this.dipAngleEndData = dipAngleEndData;
    }

    public float getDipAngleRate() {
        return dipAngleRate;
    }

    public void setDipAngleRate(float dipAngleRate) {
        this.dipAngleRate = dipAngleRate;
    }

    public float getWeightStart() {
        return weightStart;
    }

    public void setWeightStart(float weightStart) {
        this.weightStart = weightStart;
    }

    public float getWeightStartData() {
        return weightStartData;
    }

    public void setWeightStartData(float weightStartData) {
        this.weightStartData = weightStartData;
    }

    public float getWeightEnd() {
        return weightEnd;
    }

    public void setWeightEnd(float weightEnd) {
        this.weightEnd = weightEnd;
    }

    public float getWeightEndData() {
        return weightEndData;
    }

    public void setWeightEndData(float weightEndData) {
        this.weightEndData = weightEndData;
    }

    public float getWeightRate() {
        return weightRate;
    }

    public void setWeightRate(float weightRate) {
        this.weightRate = weightRate;
    }

    public float getLengthStart() {
        return lengthStart;
    }

    public void setLengthStart(float lengthStart) {
        this.lengthStart = lengthStart;
    }

    public float getLengthStartData() {
        return lengthStartData;
    }

    public void setLengthStartData(float lengthStartData) {
        this.lengthStartData = lengthStartData;
    }

    public float getLengthEnd() {
        return lengthEnd;
    }

    public void setLengthEnd(float lengthEnd) {
        this.lengthEnd = lengthEnd;
    }

    public float getLengthEndData() {
        return lengthEndData;
    }

    public void setLengthEndData(float lengthEndData) {
        this.lengthEndData = lengthEndData;
    }

    public float getLengthRate() {
        return lengthRate;
    }

    public void setLengthRate(float lengthRate) {
        this.lengthRate = lengthRate;
    }

    public float getHeightStart() {
        return heightStart;
    }

    public void setHeightStart(float heightStart) {
        this.heightStart = heightStart;
    }

    public float getHeightStartData() {
        return heightStartData;
    }

    public void setHeightStartData(float heightStartData) {
        this.heightStartData = heightStartData;
    }

    public float getHeightEnd() {
        return heightEnd;
    }

    public void setHeightEnd(float heightEnd) {
        this.heightEnd = heightEnd;
    }

    public float getHeightEndData() {
        return heightEndData;
    }

    public void setHeightEndData(float heightEndData) {
        this.heightEndData = heightEndData;
    }

    public float getHeightRate() {
        return heightRate;
    }

    public void setHeightRate(float heightRate) {
        this.heightRate = heightRate;
    }
}
