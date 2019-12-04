package com.wooddeep.crane.net.network;



import com.wooddeep.crane.net.crypto.Aes;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;
import java.util.UUID;

public class Protocol {

    final static byte[] head = new byte[]{'[', '[', '['};
    final static byte[] tail = new byte[]{']', ']', ']'};

    final static int HEAD_LENGTH = 3;
    final static int TAIL_LENGTH = 3;

    final static byte[] pack = new byte[10240];
    final static byte[] recv = new byte[8000];

    static boolean recvHeadFlag = false;

    // {"cmd":"get.session", "data":{"devid":"12345678"}}
    final static String CMD_GET_SESSION = "get.session";
    final static String CMD_RPT_REALDATA = "rpt.realdata";
    final static String CMD_SET_TIMESLOT = "set.timeslot";

    static String uuid = null;

    static Cipher eCipher = Aes.getECipher("BDE1236987450ACF");
    static Cipher eDipher = Aes.getDCipher("BDE1236987450ACF");

    public Protocol()  {
        try {
            cmdGetSession.put("cmd", "get.session");
            JSONObject data = new JSONObject();
            data.put("devid", "12345678");
            cmdGetSession.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static JSONObject cmdGetSession = new JSONObject();

    // UUID 代替设备唯一标识
    public byte[] getSession() {

        if (uuid == null) uuid = UUID.randomUUID().toString().replaceAll("-", "");

        try {
            cmdGetSession.getJSONObject("data").put("devid", uuid);
            byte[] content = cmdGetSession.toString().getBytes();
            //byte[] encryptOut = eCipher.doFinal(content);// 加密
            byte[] encryptOut = Aes.encrypt(content, "BDE1236987450ACF".getBytes());
            return encryptOut;
        } catch (Exception e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }

        return null;
    }

    public int doPack(byte[] body) {
        int currIndex = 0;
        System.arraycopy(head, 0, pack, currIndex, HEAD_LENGTH);
        currIndex += HEAD_LENGTH;

        System.arraycopy(body, 0, pack, currIndex, body.length);
        currIndex += body.length;

        System.arraycopy(tail, 0, pack, currIndex, TAIL_LENGTH);
        currIndex += TAIL_LENGTH;

        return currIndex;
    }

    public static byte[] getPack() {
        return pack;
    }


    public void unPack(byte[] pack, int n) {
        if (n < 6) return;

        int start = -1;
        int end = -1;

        for (int i = 0; i <= (n - 3); i++) {
            if (pack[i] == '[' && pack[i + 1] == '[' && pack[i + 2] == '[') {
                start = i;
            }

            if (pack[i] == ']' && pack[i + 1] == ']' && pack[i + 2] == ']') {
                end = i;
            }
        }

        if (start < 0 || end < 0) return;


        try {
            int dataLen = (end - 1) - (start + 3) + 1;
            System.arraycopy(pack, start + HEAD_LENGTH, recv, 0, dataLen);


            for (int i = 0; i < dataLen; i++) {
                System.out.printf("0x%02x ", recv[i] & 0xFF);
            }
            System.out.println("");

            //byte[] decryptOut = eDipher.doFinal(pack, start + HEAD_LENGTH, dataLen);
            byte[] decryptOut = Aes.decrypt(pack, start + HEAD_LENGTH, dataLen, "BDE1236987450ACF".getBytes());

            System.out.println(new String(decryptOut));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Protocol().getSession();
    }

}
