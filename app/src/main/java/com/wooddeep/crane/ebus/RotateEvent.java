package com.wooddeep.crane.ebus;

public class RotateEvent {

    public float centerX = 0;
    public float centerY = 0;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte [] data ;

    public RotateEvent() {

    }

    public RotateEvent(byte [] data, float x, float y) {
        this.data = data;
        this.centerX = x;
        this.centerY = y;
    }
}
