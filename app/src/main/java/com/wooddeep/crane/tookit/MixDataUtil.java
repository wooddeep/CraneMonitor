package com.wooddeep.crane.tookit;

import com.wooddeep.crane.comm.RadioProto;

/**
 * Created by niuto on 2019/9/19.
 */


/*

// 收到主机的查询数据

// 对方主机，我是从机
A9 9A 2C 01 68 4A 66 4A 82 00 4F 01 00 00 A0 0F 7F 0C 00 00 6F 7C 0F 00 E3 E5 AF 43 00 00 16 43 00 00 C8 42 00 00 00 00 00 00 32 E7

// 从机号：68 4A ~ 0x4A68
// 主机号：66 4A ~ 0x4A66

// % 1N 2N  0.88N 51.51N  0.00N  0.00N 0N# // 回转 幅度

A9 9A 2C 01 66 4A 68 4A 02 00 20 03 00 00 AC FF 00 00 00 00 00 00 0F 00 C3 E5 82 43 00 00 16 43 00 00 16 43 00 00 00 00 00 00 CF 4C

*/

public class MixDataUtil {

    private RadioProto radioProto = new RadioProto(); // 本机作为主机时，需要radio通信的对象

    private byte START = (byte) 0xA9;
    private byte END = (byte) 0x9A;

    private byte[] buffer = new byte[512];

    private byte[] out = new byte[44];

    private int currentIndex = 0;

    /**
     * 字节转换为浮点
     *
     * @param b     字节（至少4个字节）
     * @param index 开始位置
     * @return
     */
    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }


    public void add(byte[] buff, int length) {
        for (int i = 0; i < length; i++) {
            buffer[currentIndex++] = buff[i];
        }
    }

    public boolean check() {
        boolean findStart = false;
        boolean findEnd = false;
        int start = 0;
        for (int i = 0; i < currentIndex; i++) {
            if (buffer[i] == START && buffer[i + 1] == END) {
                start = i;
                findStart = true;
            }
        }

        int end = 0;

        for (int i = start; i < currentIndex; i++) {
            if (i - start + 1 == 44) {
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

            if (end - start != 43) return false;

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

        for (int i = 0; i < out.length; i++) {
            System.out.printf("0x%02x ", out[i]);
        }

        System.out.println("");

        int dstNo = ((0x000000FF & out[5]) << 8 | (0x000000FF & out[4])) & 0x0000FFFF; // 目标地址
        int srcNo = ((0x000000FF & out[7]) << 8 | (0x000000FF & out[6])) & 0x0000FFFF; // 源地址

        int iRange = ((0x000000FF & out[15]) << 8 | (0x000000FF & out[14])) & 0x0000FFFF;
        if (iRange >= 32768) iRange = 0;
        float fRange = iRange / 100.0f;

        int l;
        l = out[24];
        l &= 0xff;
        l |= ((long) out[25] << 8);
        l &= 0xffff;
        l |= ((long) out[26] << 16);
        l &= 0xffffff;
        l |= ((long) out[27] << 24);

        float rotate = Float.intBitsToFloat(l);

        System.out.printf("## range = %f, rotate = %f\n", fRange, rotate);

        radioProto.setSourceNo(srcNo);
        radioProto.setTargetNo(dstNo);
        radioProto.setPermitNo(dstNo);
        radioProto.setRange(fRange);
        radioProto.setRotate(rotate);

        radioProto.packReply();
        System.out.printf("%s\n ", new String(radioProto.modleBytes));


        return out;
    }


}
