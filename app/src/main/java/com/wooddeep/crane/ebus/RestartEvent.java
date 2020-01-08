package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/14.
 */

public class RestartEvent {
    private float speed;

    public RestartEvent() {
    }

    public RestartEvent(float w) {
        this.speed = w;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
