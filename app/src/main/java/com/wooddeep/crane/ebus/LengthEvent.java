package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/14.
 */

public class LengthEvent {

    private float length;

    public LengthEvent() {
    }

    public LengthEvent(float w) {
        this.length = w;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float weight) {
        this.length = weight;
    }
}
