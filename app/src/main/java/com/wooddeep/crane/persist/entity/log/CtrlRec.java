package com.wooddeep.crane.persist.entity.log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ctrlrec")
public class CtrlRec  extends LogEntity {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "time", useGetSet = true, defaultValue = "2019-10-01 00:00:00")
    private String time;

    // 倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
    @DatabaseField(columnName = "ropenum", useGetSet = true, defaultValue = "2")
    private float ropenum;

    @DatabaseField(columnName = "moment", useGetSet = true, defaultValue = "100")
    private float moment;

    @DatabaseField(columnName = "heigth", useGetSet = true, defaultValue = "100")
    private float heigth;

    @DatabaseField(columnName = "range", useGetSet = true, defaultValue = "40")
    private float range;

    @DatabaseField(columnName = "ratedweight", useGetSet = true, defaultValue = "100")
    private float ratedweight;

    @DatabaseField(columnName = "weight", useGetSet = true, defaultValue = "100")
    private float weight;

    @DatabaseField(columnName = "rotate", useGetSet = true, defaultValue = "100")
    private float rotate;

    @DatabaseField(columnName = "walk", useGetSet = true, defaultValue = "100")
    private float walk;

    @DatabaseField(columnName = "dipange", useGetSet = true, defaultValue = "15")
    private float dipange;

    @DatabaseField(columnName = "windspeed", useGetSet = true, defaultValue = "5")
    private float windspeed;

    @DatabaseField(columnName = "remark", useGetSet = true, defaultValue = "无")
    private String remark;

    public CtrlRec() {
    }

    public CtrlRec(int id, String time, float ropenum, float moment, float heigth, float range, float ratedweight, float weight, float rotate, float walk, float dipange, float windspeed, String remark) {
        this.id = id;
        this.time = time;
        this.ropenum = ropenum;
        this.moment = moment;
        this.heigth = heigth;
        this.range = range;
        this.ratedweight = ratedweight;
        this.weight = weight;
        this.rotate = rotate;
        this.walk = walk;
        this.dipange = dipange;
        this.windspeed = windspeed;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getRopenum() {
        return ropenum;
    }

    public void setRopenum(float ropenum) {
        this.ropenum = ropenum;
    }

    public float getMoment() {
        return moment;
    }

    public void setMoment(float moment) {
        this.moment = moment;
    }

    public float getHeigth() {
        return heigth;
    }

    public void setHeigth(float heigth) {
        this.heigth = heigth;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getRatedweight() {
        return ratedweight;
    }

    public void setRatedweight(float ratedweight) {
        this.ratedweight = ratedweight;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public float getWalk() {
        return walk;
    }

    public void setWalk(float walk) {
        this.walk = walk;
    }

    public float getDipange() {
        return dipange;
    }

    public void setDipange(float dipange) {
        this.dipange = dipange;
    }

    public float getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(float windspeed) {
        this.windspeed = windspeed;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
