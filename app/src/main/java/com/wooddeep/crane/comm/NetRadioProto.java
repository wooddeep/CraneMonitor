package com.wooddeep.crane.comm;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NetRadioProto {

    public byte[] netBuffer = new byte[10240];
    public int length = 0;
    public ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

}
