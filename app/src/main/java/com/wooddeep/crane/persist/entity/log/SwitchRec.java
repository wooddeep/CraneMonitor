package com.wooddeep.crane.persist.entity.log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "switchrec")
public class SwitchRec  extends LogEntity {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "time", useGetSet = true, defaultValue = "2019-10-01 00:00:00")
    private String time;

    @DatabaseField(columnName = "action", useGetSet = true, defaultValue = "2")
    private float action;


    public SwitchRec() {
    }

    public SwitchRec(int id, String time, float action) {
        this.id = id;
        this.time = time;
        this.action = action;
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

    public float getAction() {
        return action;
    }

    public void setAction(float action) {
        this.action = action;
    }
}
