package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "protect") // 指定数据表的名称
public class Protect {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "height", useGetSet = true, defaultValue = "100")
    private float height;

    @DatabaseField(columnName = "x1", useGetSet = true, defaultValue = "100")
    private float x1;

    @DatabaseField(columnName = "y1", useGetSet = true, defaultValue = "100")
    private float y1;

    @DatabaseField(columnName = "x2", useGetSet = true, defaultValue = "100")
    private float x2;

    @DatabaseField(columnName = "y2", useGetSet = true, defaultValue = "100")
    private float y2;

    @DatabaseField(columnName = "x3", useGetSet = true, defaultValue = "100")
    private float x3;

    @DatabaseField(columnName = "y3", useGetSet = true, defaultValue = "100")
    private float y3;

    @DatabaseField(columnName = "x4", useGetSet = true, defaultValue = "100")
    private float x4;

    @DatabaseField(columnName = "y4", useGetSet = true, defaultValue = "100")
    private float y4;

    @DatabaseField(columnName = "x5", useGetSet = true, defaultValue = "100")
    private float x5;

    @DatabaseField(columnName = "y5", useGetSet = true, defaultValue = "100")
    private float y5;

    @DatabaseField(columnName = "x6", useGetSet = true, defaultValue = "100")
    private float x6;

    @DatabaseField(columnName = "y6", useGetSet = true, defaultValue = "100")
    private float y6;

    public Protect() {
        super();
    }

    public Protect(float height, float x1, float y1, float x2, float y2, float x3, float y3
        , float x4, float y4, float x5, float y5, float x6, float y6) {
        super();

        this.height = height;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;
        this.x5 = x5;
        this.y5 = y5;
        this.x6 = x6;
        this.y6 = y6;
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

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public float getX3() {
        return x3;
    }

    public void setX3(float x3) {
        this.x3 = x3;
    }

    public float getY3() {
        return y3;
    }

    public void setY3(float y3) {
        this.y3 = y3;
    }

    public float getX4() {
        return x4;
    }

    public void setX4(float x4) {
        this.x4 = x4;
    }

    public float getY4() {
        return y4;
    }

    public void setY4(float y4) {
        this.y4 = y4;
    }

    public float getX5() {
        return x5;
    }

    public void setX5(float x5) {
        this.x5 = x5;
    }

    public float getY5() {
        return y5;
    }

    public void setY5(float y5) {
        this.y5 = y5;
    }

    public float getX6() {
        return x6;
    }

    public void setX6(float x6) {
        this.x6 = x6;
    }

    public float getY6() {
        return y6;
    }

    public void setY6(float y6) {
        this.y6 = y6;
    }
}
