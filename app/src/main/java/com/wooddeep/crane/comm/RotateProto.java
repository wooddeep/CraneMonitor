package com.wooddeep.crane.comm;

import com.wooddeep.crane.persist.entity.Calibration;

public class RotateProto {

    // 01 04 00 01 00 02 20 0B
    private byte first = (byte) 0x01;

    private byte second = (byte) 0x04;

    private int data = 0x00010002 & 0xFFFFFFFF; // 数据

    private double angle = 0;

    public RotateProto() {

    }

    public int parse(byte[] data) {
        if (data == null) return -1;
        if (data.length < 9) {
            return -1;
        }

        this.data = (data[3] & 0xFF) << 24 | (data[4] & 0xFF) << 16 | (data[5] & 0xFF) << 8 | (data[6] & 0xFF);

        for (int i = 0; i < out.length; i++) {
            out[i] = data[i];
        }

        /*
        for (int i = 0; i < 9 ; i ++) {
            System.out.printf("%02x ", data[i] & 0xFF); // 01 04 00 01 00 02 20 0b 1d  = 16777760
        }

        System.out.printf(" = %d\n", this.data);
        */

        return 0;
    }

    public byte[] out = new byte[10];

    public byte[] pack() {

        out[0] = 0x01;
        out[1] = 0x04;

        out[3] = (byte) (((this.data & 0xFF000000) >> 24) & 0xFF);
        out[4] = (byte) (((this.data & 0x00FF0000) >> 16) & 0xFF);
        out[5] = (byte) (((this.data & 0x0000FF00) >> 8) & 0xFF);
        out[6] = (byte) (((this.data & 0x000000FF) >> 0) & 0xFF);

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

    public double calcAngle(Calibration calibration) {
        float startAngle = calibration.getRotateStartAngle();
        double currentAngle = startAngle + (getData() - calibration.getRotateStartData()) * calibration.getRotateRate();
        //double degree = Math.toDegrees(currentAngle); //Math.round(Math.toDegrees(currentAngle) * 10) / 10.0f;
        setAngle(currentAngle);
        return currentAngle;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
