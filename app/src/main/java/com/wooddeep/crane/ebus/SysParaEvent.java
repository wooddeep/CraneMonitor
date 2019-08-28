package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/28.
 */

public class SysParaEvent {

    private String craneType = null;
    private String armLength = null;
    private String power = null;

    public SysParaEvent() {}

    public SysParaEvent(String craneType, String armLength, String power) {
        this.craneType = craneType;
        this.armLength = armLength;
        this.power = power;
    }

    public String getCraneType() {
        return craneType;
    }

    public void setCraneType(String craneType) {
        this.craneType = craneType;
    }

    public String getArmLength() {
        return armLength;
    }

    public void setArmLength(String armLength) {
        this.armLength = armLength;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }
}
