package com.wooddeep.crane.simulator;

/**
 * Created by niuto on 2019/7/30.
 */

// 回转数据发射器
public class RotateEmitter {
    private int sAmplitude = 3;
    private int sHeight = 3;
    private int sWeight = 3;
    private int sWindSpeed = 3;

    private int eAmplitude = 100;
    private int eHeight = 100;
    private int eWeight = 100;
    private int eWindSpeed = 100;

    public RotateEmitter() {
        this.sAmplitude = 3;
        this.sHeight = 3;
        this.sWeight = 3;
        this.sWindSpeed = 3;
    }

    private boolean increase = true;
    public int getsAmplitude() {
        return sAmplitude;
    }

    public void setsAmplitude(int sAmplitude) {
        this.sAmplitude = sAmplitude;
    }

    public int getsHeight() {
        return sHeight;
    }

    public void setsHeight(int sHeight) {
        this.sHeight = sHeight;
    }

    public int getsWeight() {
        return sWeight;
    }

    public void setsWeight(int sWeight) {
        this.sWeight = sWeight;
    }

    public int getsWindSpeed() {
        return sWindSpeed;
    }

    public void setsWindSpeed(int sWindSpeed) {
        this.sWindSpeed = sWindSpeed;
    }

    public void initData() {
        this.sAmplitude = 3;
        this.sHeight = 3;
        this.sWeight = 3;
        this.sWindSpeed = 3;
    }

    public void emitter() {
        if (increase) {
            this.sAmplitude++;
            this.sHeight++;
            this.sWeight++;
            this.sWindSpeed++;
        } else {
            this.sAmplitude--;
            this.sHeight--;
            this.sWeight--;
            this.sWindSpeed--;
        }

        if (this.sAmplitude >= this.eAmplitude) {
            increase = false;
        }

        if (this.sAmplitude <= 3) {
            increase = true;
        }
    }

    public void adjust(int value) {
        if (value < 3) value = 3;
        this.sAmplitude = value;
        this.sHeight = value;
        this.sWeight = value;
        this.sWindSpeed = value;

        this.eAmplitude = value;
        this.eHeight = value;
        this.eWeight = value;
        this.eWindSpeed = value;

    }

}
