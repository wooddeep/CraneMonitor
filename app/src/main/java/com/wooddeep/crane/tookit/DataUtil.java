package com.wooddeep.crane.tookit;

/**
 * Created by niuto on 2019/9/19.
 */

public class DataUtil {

    /*

     A9 9A 2C 01 4C 4A 66 4A 82 00 4F 01 00 00 A3 0E 7F 0C 00 00 6F 7C
     0F 00 E3 E5 AF 43 00 00 C8 42 00 00 C8 42 00 00 00 00 00 00 C6 10

     */

    private byte[] header = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] target = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // 4字节

    private byte[] source = new byte[]{(byte) 0x00, (byte) 0x00}; // 2字节

    private byte[] moment = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] ratedWeight = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] weight = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] range = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] height = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] reserve0 = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] digAngle = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] windSpeed = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte[] rotate = new byte[]{(byte) 0xA9, (byte) 0x9A, (byte) 0xA9, (byte) 0x9A}; // 4 字节

    private byte[] xAxis = new byte[]{(byte) 0xA9, (byte) 0x9A, (byte) 0xA9, (byte) 0x9A}; // 4 字节

    private byte[] yAxis = new byte[]{(byte) 0xA9, (byte) 0x9A, (byte) 0xA9, (byte) 0x9A}; // 4 字节

    private byte[] reserve1 = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // 6 字节

    private byte[] crc = new byte[]{(byte) 0xA9, (byte) 0x9A}; // 2 字节

    private byte START = '%';
    private byte END = '#';

    private byte[] buffer = new byte[512];

    private byte[] out = new byte[40];

    private int currentIndex = 0;

    public void add(byte[] buff, int length) {
        for (int i = 0; i < length; i++) {
            buffer[currentIndex++] = buff[i];
        }
        //currentIndex = currentIndex + length;
    }

    public boolean check() {
        boolean findStart = false;
        boolean findEnd = false;
        int start = 0;
        for (int i = 0; i < currentIndex; i++) {
            if (buffer[i] == START) {
                start = i;
                findStart = true;
            }
        }

        int end = 0;
        for (int i = start; i < currentIndex; i++) {
            if (buffer[i] == END) {
                end = i;
                findEnd = true;
            }
        }

        if (findStart && findEnd) {
            for (int i = start; i <= end; i++) {
                out[i - start] = buffer[i];
            }

            System.arraycopy(buffer, end + 1, buffer, 0, currentIndex - end); // 移动
            currentIndex = currentIndex - end;

            if (end - start != 38) return false;

            for (int i = 1; i <= 37; i++) {
                if (out[i] != ' ' &&
                    out[i] != 'N' &&
                    out[i] != '.' &&
                    out[i] != '0' &&
                    out[i] != '1' &&
                    out[i] != '2' &&
                    out[i] != '3' &&
                    out[i] != '4' &&
                    out[i] != '5' &&
                    out[i] != '6' &&
                    out[i] != '7' &&
                    out[i] != '8' &&
                    out[i] != '9') {
                    return false;
                }
            }

            return true;
        }

        if (!findStart && findEnd) {
            System.arraycopy(buffer, end + 1, buffer, 0, currentIndex - end); // 移动
            currentIndex = currentIndex - end;
        }

        if (findStart && !findEnd) {
            System.arraycopy(buffer, start, buffer, 0, currentIndex - start); // 移动
            currentIndex = currentIndex - start;
        }

        if (!findStart && !findEnd) {
            currentIndex = 0;
        }

        return false;
    }

    public byte[] get() {
        return out;
    }

}
