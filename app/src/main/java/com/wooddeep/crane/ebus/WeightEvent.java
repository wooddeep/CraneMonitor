package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/14.
 */

public class WeightEvent {

    private float weight;

    public WeightEvent() {
    }

    public WeightEvent(float w) {
        this.weight = w;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
