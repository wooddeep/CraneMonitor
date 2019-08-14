package com.wooddeep.crane.simulator;

/**
 * Created by niuto on 2019/7/30.
 */

public class UartEmitter {

    private int sAmplitude = 0;
    private int sHeight = 0;
    private int sWeight = 0;
    private int sWindSpeed = 0;

    private int eAmplitude = 1000;
    private int eHeight = 1000;
    private int eWeight = 1000;
    private int eWindSpeed = 1000;
    private int eCarRange = 35;

    private int carRange = 0;

    public UartEmitter() {
        this.sAmplitude = 0;
        this.sHeight = 0;
        this.sWeight = 0;
        this.sWindSpeed = 0;
        this.carRange = 0;
    }

    private boolean increase = true;
    private boolean rangeInc = true;
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

    public int getCarRange() {
        return carRange;
    }

    public void setCarRange(int carRange) {
        this.carRange = carRange;
    }

    public void initData() {
        this.sAmplitude = 0;
        this.sHeight = 0;
        this.sWeight = 0;
        this.sWindSpeed = 0;
        this.carRange = 0;
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

        if (this.sAmplitude <= 0) {
            increase = true;
        }

        if (rangeInc) {
            this.carRange++;
        } else {
            this.carRange--;
        }

        if (this.carRange >= this.eCarRange) {
            rangeInc = false;
        }

        if (this.carRange <= 0) {
            rangeInc = true;
        }

    }

    public void adjust(int value) {
        if (value < 0) value = 0;
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
