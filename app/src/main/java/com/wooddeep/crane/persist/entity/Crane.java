package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "crane") // 指定数据表的名称
public class Crane {
    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "name", useGetSet = true, defaultValue = "")
    private String name;

    @DatabaseField(columnName = "isMain", useGetSet = true, defaultValue = "false")
    private boolean isMain;

    @DatabaseField(columnName = "type", useGetSet = true, defaultValue = "0")
    private int type;

    @DatabaseField(columnName = "coordX1", useGetSet = true, defaultValue = "100")
    private float coordX1;

    @DatabaseField(columnName = "coordY1", useGetSet = true, defaultValue = "100")
    private float coordY1;

    @DatabaseField(columnName = "coordX2", useGetSet = true, defaultValue = "100")
    private float coordX2;

    @DatabaseField(columnName = "coordY2", useGetSet = true, defaultValue = "100")
    private float coordY2;

    @DatabaseField(columnName = "CraneHeight", useGetSet = true, defaultValue = "0")
    private float CraneHeight;

    @DatabaseField(columnName = "BigArmLength", useGetSet = true, defaultValue = "40")
    private float BigArmLength;

    @DatabaseField(columnName = "BalancArmLength", useGetSet = true, defaultValue = "0")
    private float BalancArmLength;

    @DatabaseField(columnName = "CraneBodyRadius", useGetSet = true, defaultValue = "0")
    private float CraneBodyRadius;

    @DatabaseField(columnName = "BigArmWidth", useGetSet = true, defaultValue = "0")
    private float BigArmWidth;

    @DatabaseField(columnName = "BalancArmWidth", useGetSet = true, defaultValue = "0")
    private float BalancArmWidth;

    @DatabaseField(columnName = "maxAngle", useGetSet = true, defaultValue = "15.0")
    private float maxAngle;

    @DatabaseField(columnName = "minAngle", useGetSet = true, defaultValue = "85.0")
    private float minAngle;

    @DatabaseField(columnName = "archPara", useGetSet = true, defaultValue = "0.0")
    private float archPara;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(boolean main) {
        isMain = main;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getCoordX1() {
        return coordX1;
    }

    public void setCoordX1(float coordX1) {
        this.coordX1 = coordX1;
    }

    public float getCoordY1() {
        return coordY1;
    }

    public void setCoordY1(float coordY1) {
        this.coordY1 = coordY1;
    }

    public float getCoordX2() {
        return coordX2;
    }

    public void setCoordX2(float coordX2) {
        this.coordX2 = coordX2;
    }

    public float getCoordY2() {
        return coordY2;
    }

    public void setCoordY2(float coordY2) {
        this.coordY2 = coordY2;
    }

    public float getCraneHeight() {
        return CraneHeight;
    }

    public void setCraneHeight(float craneHeight) {
        CraneHeight = craneHeight;
    }

    public float getBigArmLength() {
        return BigArmLength;
    }

    public void setBigArmLength(float bigArmLength) {
        BigArmLength = bigArmLength;
    }

    public float getBalancArmLength() {
        return BalancArmLength;
    }

    public void setBalancArmLength(float balancArmLength) {
        BalancArmLength = balancArmLength;
    }

    public float getCraneBodyRadius() {
        return CraneBodyRadius;
    }

    public void setCraneBodyRadius(float craneBodyRadius) {
        CraneBodyRadius = craneBodyRadius;
    }

    public float getBigArmWidth() {
        return BigArmWidth;
    }

    public void setBigArmWidth(float bigArmWidth) {
        BigArmWidth = bigArmWidth;
    }

    public float getBalancArmWidth() {
        return BalancArmWidth;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(float minAngle) {
        this.minAngle = minAngle;
    }

    public float getArchPara() {
        return archPara;
    }

    public void setArchPara(float archPara) {
        this.archPara = archPara;
    }

    public void setBalancArmWidth(float balancArmWidth) {
        BalancArmWidth = balancArmWidth;
    }

    public Crane() {
        super();
    }

    public Crane(boolean isMain, int id, String name, int type, float coordX1, float coordY1,
                     float coordX2, float coordY2, float CraneHeight, float BigArmLength,
                     float BalancArmLength, float CraneBodyRadius, float BigArmWidth, float BalancArmWidth) {
        super();
        this.id = id;
        this.isMain = isMain;
        this.name = name;
        this.type = type;
        this.coordX1 = coordX1;
        this.coordY1 = coordY1;
        this.coordX2 = coordX2;
        this.coordY2 = coordY2;
        this.CraneHeight = CraneHeight;
        this.BigArmLength = BigArmLength;
        this.BalancArmLength = BalancArmLength;
        this.CraneBodyRadius = CraneBodyRadius;
        this.BigArmWidth = BigArmWidth;
        this.BalancArmWidth = BalancArmWidth;
    }

    @Override
    public String toString() {
        return "";
    }

    public static Crane getInitData() {
        Crane crane = new Crane(
            true,
            0,
            String.format("%d号塔基", 1),
            0,
            100,
            100,
            1,
            1,
            1,
            40,
            10,
            1,
            1,
            1);
        return crane;
    }

}
