package com.wooddeep.crane.persist.entity.log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "calirec")
public class CaliRec  extends LogEntity {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "time", useGetSet = true, defaultValue = "2019-10-01 00:00:00")
    private String time;

    @DatabaseField(columnName = "dipangle", useGetSet = true, defaultValue = "100")
    private float dipangle;

    @DatabaseField(columnName = "heigth", useGetSet = true, defaultValue = "100")
    private float heigth;

    @DatabaseField(columnName = "range", useGetSet = true, defaultValue = "40")
    private float range;

    @DatabaseField(columnName = "weight", useGetSet = true, defaultValue = "100")
    private float weight;

    @DatabaseField(columnName = "rotate", useGetSet = true, defaultValue = "100")
    private float rotate;


    public CaliRec() {
    }

    public CaliRec(int id, String time, float dipangle, float heigth, float range, float weight, float rotate) {
        this.id = id;
        this.time = time;
        this.dipangle = dipangle;
        this.heigth = heigth;
        this.range = range;
        this.weight = weight;
        this.rotate = rotate;
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

    public float getDipangle() {
        return dipangle;
    }

    public void setDipangle(float dipangle) {
        this.dipangle = dipangle;
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
}
