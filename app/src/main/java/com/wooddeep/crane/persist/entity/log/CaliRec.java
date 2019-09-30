package com.wooddeep.crane.persist.entity.log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.wooddeep.crane.views.TableCell;

@DatabaseTable(tableName = "calirec")
public class CaliRec  extends LogEntity {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "time", useGetSet = true, defaultValue = "2019-10-01 00:00:00")
    private String time;


    @DatabaseField(columnName = "type", useGetSet = true, defaultValue = "dipangle")
    private String type; // dipangle, height, range, weight, rotate

    @DatabaseField(columnName = "startAD", useGetSet = true, defaultValue = "100")
    private float startAD;

    @DatabaseField(columnName = "endAD", useGetSet = true, defaultValue = "200")
    private float endAD;

    @DatabaseField(columnName = "startValue", useGetSet = true, defaultValue = "(100,100)")
    private String startValue;

    @DatabaseField(columnName = "endValue", useGetSet = true, defaultValue = "(200,200)")
    private String endValue;


    @DatabaseField(columnName = "KValue", useGetSet = true, defaultValue = "1.0")
    private float KValue;

    public CaliRec() {
    }

    public CaliRec(int id, String time, String type, float startAD, float endAD, String startValue, String endValue, float kValue) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.startAD = startAD;
        this.endAD = endAD;
        this.startValue = startValue;
        this.endValue = endValue;
        this.KValue = kValue;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getStartAD() {
        return startAD;
    }

    public void setStartAD(float startAD) {
        this.startAD = startAD;
    }

    public float getEndAD() {
        return endAD;
    }

    public void setEndAD(float endAD) {
        this.endAD = endAD;
    }

    public String getStartValue() {
        return startValue;
    }

    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public float getKValue() {
        return KValue;
    }

    public void setKValue(float KValue) {
        this.KValue = KValue;
    }
}
