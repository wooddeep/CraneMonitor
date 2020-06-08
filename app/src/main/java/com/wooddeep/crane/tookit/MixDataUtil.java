package com.wooddeep.crane.tookit;

import com.wooddeep.crane.comm.RadioProto;
import com.wooddeep.crane.comm.RecenProto;

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
        /*
        for (int i = 0; i < out.length; i++) {
            System.out.printf("%02x ", out[i]);
        }
        System.out.println();
        */

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

        //float rotate = Float.intBitsToFloat(l);
        float rotate = (float)Math.toRadians(Float.intBitsToFloat(l));

        //System.out.printf("src: %d, dst: %d , ## range = %f, rotate = %f\n", srcNo, dstNo, fRange, rotate);

        radioProto.setSourceNo(srcNo);

        radioProto.setTargetNo(dstNo); // 如果为回文，则target为0
        radioProto.setPermitNo(dstNo);
        radioProto.setRange(fRange);
        radioProto.setRotate(rotate);
        //System.out.printf("%s\n ", new String(radioProto.modleBytes));

        byte flag = (byte) (out[8] & 0x000000F0); // 区分主机查询 和 从机 回应 报文
        //System.out.println("#### flag = " + flag);

        if (flag == 0) radioProto.setTargetNo(0);

        return radioProto.packReply(); // 44字节转换为 39字节
    }

    // 对方主机，我是从机
    // A9 9A 2C 01 68 4A 66 4A 82 00 4F 01 00 00 A0 0F 7F 0C 00 00 6F 7C 0F 00 E3 E5 AF 43 00 00 16 43 00 00 C8 42 00 00 00 00 00 00 32 E7


    public byte[] slaveResp(RecenProto proto, int sourceNo, float x, float y, float ratedWeight, float weight, float height, float dipAngle, float windSpeed) {
        byte[] out = new byte[44];

        out[0] = (byte) 0xA9; // 头
        out[1] = (byte) 0x9A;

        out[2] = (byte) 0x2C; // 版本
        out[3] = (byte) 0x01;

        try {
            int masterNo = Integer.parseInt(proto.getMasterNo());
            out[4] = (byte) ((masterNo >> 0) & 0x000000FF); // 主机 编号
            out[5] = (byte) ((masterNo >> 8) & 0x000000FF); // 主机 编号
            out[6] = (byte) ((sourceNo >> 0) & 0x000000FF); // 从机 编号
            out[7] = (byte) ((sourceNo >> 8) & 0x000000FF);

            //System.out.printf("## m = %d, s = %d\n", masterNo, sourceNo);

        } catch (Exception e) {
            System.out.println(e.getCause());
        }

        out[8] = (byte) 0x00; // 主机82，从机02
        out[9] = (byte) 0x00; // 力矩百分比

        if (weight < 0) weight = 0;
        int iweight =  (int)(weight * 100);
        out[10] = (byte) 0x4f; // 额重
        out[11] = (byte) 0x01;

        if (ratedWeight < 0) ratedWeight = 0;
        int iratedWeight =  (int)(ratedWeight * 100);
        out[12] = (byte) ((iratedWeight >> 0) & 0x000000FF); // 重量
        out[13] = (byte) ((iratedWeight >> 8) & 0x000000FF);

        int range =  (int)(proto.getRange() * 100);
        out[14] = (byte) ((range >> 0) & 0x000000FF); // 幅度
        out[15] = (byte) ((range >> 8) & 0x000000FF);

        if (height < 0) height = 0;
        int iheight =  (int)(height * 100);
        out[16] = (byte) ((iheight >> 0) & 0x000000FF); // 高度
        out[17] = (byte) ((iheight >> 8) & 0x000000FF);

        out[18] = (byte) 0x00; //
        out[19] = (byte) 0x00;

        if (dipAngle < 0) dipAngle = 0;
        int idipAngle =  (int)(dipAngle * 100);
        out[20] = (byte) ((idipAngle >> 0) & 0x000000FF);  // 仰角
        out[21] = (byte) ((idipAngle >> 8) & 0x000000FF);

        if (windSpeed < 0) windSpeed = 0;
        int iwindSpeed =  (int)(windSpeed * 100);
        out[22] = (byte) ((iwindSpeed >> 0) & 0x000000FF);  // 风速
        out[23] = (byte) ((iwindSpeed >> 8) & 0x000000FF);

        int rotate = Float.floatToIntBits((float) Math.toDegrees(proto.rotate));
        out[24] = (byte) ((rotate >> 0) & 0x000000FF); //0xE3; // 回转角度
        out[25] = (byte) ((rotate >> 8) & 0x000000FF);
        ;
        out[26] = (byte) ((rotate >> 16) & 0x000000FF);
        out[27] = (byte) ((rotate >> 24) & 0x000000FF);

        int ix = Float.floatToIntBits(x);

        out[28] = (byte) ((ix >> 0) & 0x000000FF);
        out[29] = (byte) ((ix >> 8) & 0x000000FF);
        out[30] = (byte) ((ix >> 16) & 0x000000FF);
        out[31] = (byte) ((ix >> 24) & 0x000000FF);

        int iy = Float.floatToIntBits(y);
        out[32] = (byte) ((iy >> 0) & 0x000000FF);
        out[33] = (byte) ((iy >> 8) & 0x000000FF);
        out[34] = (byte) ((iy >> 16) & 0x000000FF);
        out[35] = (byte) ((iy >> 24) & 0x000000FF);

        out[36] = (byte) 0x42;
        out[37] = (byte) 0x42;
        out[38] = (byte) 0x42;
        out[39] = (byte) 0x42;
        out[40] = (byte) 0x42;
        out[41] = (byte) 0x42;

        int crc = CrcUtil.CRC_16_X25_INT(out, 42);

        out[42] = (byte) (crc & 0x000000FF);
        out[43] = (byte) ((crc >> 8) & 0x000000FF);

        return out;
    }

}
