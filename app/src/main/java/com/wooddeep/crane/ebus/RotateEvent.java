package com.wooddeep.crane.ebus;

public class RotateEvent {

    public float centerX = 0;
    public float centerY = 0;

    public float angle = 0;

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

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
