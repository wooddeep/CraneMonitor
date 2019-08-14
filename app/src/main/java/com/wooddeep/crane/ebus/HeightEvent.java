package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/14.
 */

public class HeightEvent {

    private float height;

    public HeightEvent() {
    }

    public HeightEvent(float w) {
        this.height = w;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float weight) {
        this.height = weight;
    }
}
