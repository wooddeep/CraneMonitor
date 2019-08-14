package com.wooddeep.crane.ebus;

public class RadioEvent {

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte [] data ;

    public RadioEvent() {

    }

    public RadioEvent(byte [] d) {
        this.data = d;
    }


}
