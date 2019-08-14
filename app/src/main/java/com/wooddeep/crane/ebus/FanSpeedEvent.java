package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/14.
 */

public class FanSpeedEvent {
    private float speed;

    public FanSpeedEvent() {
    }

    public FanSpeedEvent(float w) {
        this.speed = w;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
