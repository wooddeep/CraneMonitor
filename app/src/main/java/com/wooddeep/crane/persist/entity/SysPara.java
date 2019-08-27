package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "syspara") // 指定数据表的名称
public class SysPara {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    // craneType & armLength & power 三个为联合键值
    @DatabaseField(columnName = "paraName", useGetSet = true, defaultValue = "name")
    private String paraName;

    @DatabaseField(columnName = "paraValue", useGetSet = true, defaultValue = "value") //臂长
    private String paraValue;


    public SysPara() {
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


    public SysPara(String name, String value) {
        this.paraName = name;
        this.paraValue = value;
    }

    public static SysPara getInitData() {
        return new SysPara(
            "name",
            "value"
        );
    }

    public String getParaName() {
        return paraName;
    }

    public void setParaName(String paraName) {
        this.paraName = paraName;
    }

    public String getParaValue() {
        return paraValue;
    }

    public void setParaValue(String paraValue) {
        this.paraValue = paraValue;
    }
}
