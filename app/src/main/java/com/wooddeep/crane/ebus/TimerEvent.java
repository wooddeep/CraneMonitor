package com.wooddeep.crane.ebus;

public class TimerEvent {

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte [] data ;

    public TimerEvent() {

    }

    public TimerEvent(byte [] data) {
        this.data = data;
    }
}
