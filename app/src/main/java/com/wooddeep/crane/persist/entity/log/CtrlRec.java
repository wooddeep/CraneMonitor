package com.wooddeep.crane.persist.entity.log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ctrlrec")
public class CtrlRec extends LogEntity {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    @DatabaseField(columnName = "time", useGetSet = true, defaultValue = "2019-10-01 00:00:00")
    private String time;


    // 倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
    @DatabaseField(columnName = "carOut2", useGetSet = true, defaultValue = "false")
    private boolean carOut2;

    @DatabaseField(columnName = "carOut1", useGetSet = true, defaultValue = "false")
    private boolean carOut1;

    @DatabaseField(columnName = "rotate5", useGetSet = true, defaultValue = "false")
    private boolean rotate5;

    @DatabaseField(columnName = "rotate4", useGetSet = true, defaultValue = "false")
    private boolean rotate4;

    @DatabaseField(columnName = "rotate3", useGetSet = true, defaultValue = "false")
    private boolean rotate3;

    @DatabaseField(columnName = "rotate2", useGetSet = true, defaultValue = "false")
    private boolean rotate2;

    @DatabaseField(columnName = "leftRote", useGetSet = true, defaultValue = "false")
    private boolean leftRote;

    @DatabaseField(columnName = "rightRote", useGetSet = true, defaultValue = "false")
    private boolean rightRote;

    @DatabaseField(columnName = "moment3", useGetSet = true, defaultValue = "false")
    private boolean moment3;

    @DatabaseField(columnName = "moment2", useGetSet = true, defaultValue = "false")
    private boolean moment2;

    @DatabaseField(columnName = "moment1", useGetSet = true, defaultValue = "false")
    private boolean moment1;

    @DatabaseField(columnName = "weight1", useGetSet = true, defaultValue = "false")
    private boolean weight1;

    @DatabaseField(columnName = "carBack2", useGetSet = true, defaultValue = "false")
    private boolean carBack2;

    @DatabaseField(columnName = "carBack1", useGetSet = true, defaultValue = "false")
    private boolean carBack1;

    public CtrlRec() {
    }

    public CtrlRec(int id, String time, boolean carOut2, boolean carOut1, boolean rotate5, boolean rotate4, boolean rotate3, boolean rotate2, boolean leftRote, boolean rightRote, boolean moment3, boolean moment2, boolean moment1, boolean weight1, boolean carBack2, boolean carBack1) {
        this.id = id;
        this.time = time;
        this.carOut2 = carOut2;
        this.carOut1 = carOut1;
        this.rotate5 = rotate5;
        this.rotate4 = rotate4;
        this.rotate3 = rotate3;
        this.rotate2 = rotate2;
        this.leftRote = leftRote;
        this.rightRote = rightRote;
        this.moment3 = moment3;
        this.moment2 = moment2;
        this.moment1 = moment1;
        this.weight1 = weight1;
        this.carBack2 = carBack2;
        this.carBack1 = carBack1;
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

    public boolean isCarOut2() {
        return carOut2;
    }

    public void setCarOut2(boolean carOut2) {
        this.carOut2 = carOut2;
    }

    public boolean isCarOut1() {
        return carOut1;
    }

    public void setCarOut1(boolean carOut1) {
        this.carOut1 = carOut1;
    }

    public boolean isRotate5() {
        return rotate5;
    }

    public void setRotate5(boolean rotate5) {
        this.rotate5 = rotate5;
    }

    public boolean isRotate4() {
        return rotate4;
    }

    public void setRotate4(boolean rotate4) {
        this.rotate4 = rotate4;
    }

    public boolean isRotate3() {
        return rotate3;
    }

    public void setRotate3(boolean rotate3) {
        this.rotate3 = rotate3;
    }

    public boolean isRotate2() {
        return rotate2;
    }

    public void setRotate2(boolean rotate2) {
        this.rotate2 = rotate2;
    }

    public boolean isLeftRote() {
        return leftRote;
    }

    public void setLeftRote(boolean leftRote) {
        this.leftRote = leftRote;
    }

    public boolean isRightRote() {
        return rightRote;
    }

    public void setRightRote(boolean rightRote) {
        this.rightRote = rightRote;
    }

    public boolean isMoment3() {
        return moment3;
    }

    public void setMoment3(boolean moment3) {
        this.moment3 = moment3;
    }

    public boolean isMoment2() {
        return moment2;
    }

    public void setMoment2(boolean moment2) {
        this.moment2 = moment2;
    }

    public boolean isMoment1() {
        return moment1;
    }

    public void setMoment1(boolean moment1) {
        this.moment1 = moment1;
    }

    public boolean isWeight1() {
        return weight1;
    }

    public void setWeight1(boolean weight1) {
        this.weight1 = weight1;
    }

    public boolean isCarBack2() {
        return carBack2;
    }

    public void setCarBack2(boolean carBack2) {
        this.carBack2 = carBack2;
    }

    public boolean isCarBack1() {
        return carBack1;
    }

    public void setCarBack1(boolean carBack1) {
        this.carBack1 = carBack1;
    }
}
