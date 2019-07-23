package com.wooddeep.crane.ebus;

public class UartEvent {

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte [] data ;

    public UartEvent() {

    }

    public UartEvent(byte [] data) {
        this.data = data;
    }
}
