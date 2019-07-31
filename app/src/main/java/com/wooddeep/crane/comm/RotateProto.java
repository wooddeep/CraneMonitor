package com.wooddeep.crane.comm;

public class RotateProto {
    //    幅度   高度  重量   风速  校验码？
    // 01 04 00 01 00 02 20 0B
    private byte first = (byte) 0x01;

    private byte second = (byte) 0x04;

    private int data = 0x00010002 & 0xFFFFFFFF; // 数据

    public RotateProto() {

    }

    public int parse(byte[] data) {
        if (data.length != 8) {
            return -1;
        }

        this.data = (data[2] & 0xFF) << 24 | (data[3] & 0xFF) << 16 | (data[4] & 0xFF) << 8 | (data[5] & 0xFF);

        return 0;
    }

    public byte[] pack() {
        byte[] out = new byte[8];
        out[0] = 0x01;
        out[1] = 0x04;

        out[2] = (byte) (((this.data & 0xFF000000) >> 24) & 0xFF);
        out[3] = (byte) (((this.data & 0x00FF0000) >> 16) & 0xFF);
        out[4] = (byte) (((this.data & 0x0000FF00) >> 8) & 0xFF);
        out[5] = (byte) (((this.data & 0x000000FF) >> 0) & 0xFF);

        return out;
    }

    public byte getFirst() {
        return first;
    }

    public void setFirst(byte first) {
        this.first = first;
    }

    public byte getSecond() {
        return second;
    }

    public void setSecond(byte second) {
        this.second = second;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

}
