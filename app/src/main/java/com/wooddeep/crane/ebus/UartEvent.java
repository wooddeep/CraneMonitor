package com.wooddeep.crane.ebus;

public class UartEvent {

    public byte [] data ;

    public UartEvent(byte [] data) {
        this.data = data;
    }
}
