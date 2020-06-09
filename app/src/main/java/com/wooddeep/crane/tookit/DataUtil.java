package com.wooddeep.crane.tookit;

/**
 * Created by niuto on 2019/9/19.
 */

public class DataUtil {

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
