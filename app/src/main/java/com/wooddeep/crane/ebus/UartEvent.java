package com.wooddeep.crane.ebus;

public class UartEvent {

    public int craneType = 0; // 塔基类型 0 ~ 平臂, 1 ~ 大臂

    public float bigArmLength = 40.0f;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte [] data ;

    public UartEvent() {

    }

    public UartEvent(byte [] data) {
        this.data = data;
    }
}
