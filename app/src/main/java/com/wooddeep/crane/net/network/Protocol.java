package com.wooddeep.crane.net.network;



import com.wooddeep.crane.net.NetClient;
import com.wooddeep.crane.net.crypto.Aes;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.entity.SysPara;

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

    static String devid = null;


    static JSONObject cmdGetSession = null;
    static JSONObject cmdRptRealData = null;
    static JSONObject cmdResponse = null;

    public Protocol()  {
        try {
            cmdGetSession = new JSONObject("{\"cmd\":\"get.session\", \"data\":{\"devid\":\"12345678\"}}");
            cmdRptRealData = new JSONObject("{\"cmd\":\"rpt.realdata\", \"data\":{\n" +
                "    \"devid\":\"12345678\",\n" +
                "    \"sessionid\": \"afdsf123\",\n" +
                "    \"rotate\": 90.5,\n" +
                "    \"angle\": 15.0,\n" +
                "    \"bigArmLen\": 50.0,\n" +
                "    \"carRange\": 40.0,\n" +
                "    \"weight\": 25.0,\n" +
                "    \"moment\": 80,\n" +
                "    \"windSpeed\": 15.0,\n" +
                "    \"height\": 45.0,\n" +
                "    \"hookHeight\": 25.0,\n" +
                "    \"ropeNum\": 4,\n" +
                "    \"isMain\": true,\n" +
                "    \"type\": \"rcv\",\n" +
                "    \"craneType\": \"flat\"\n" +
                "  }\n" +
                "}");

            cmdResponse = new JSONObject("{\"cmd\":\"set.timeslot\", \"data\":{\"ack\": true}}");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public byte[] response(String cmd) {
        try {
            cmdResponse.put("cmd", cmd);
            byte[] content = cmdResponse.toString().getBytes();
            //byte[] encryptOut = eCipher.doFinal(content);// 加密
            byte[] encryptOut = Aes.encrypt(content, "BDE1236987450ACF".getBytes());
            return encryptOut;
        } catch (Exception e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
        return null;
    }

    // UUID 代替设备唯一标识
    public byte[] getSession(SysParaDao paraDao) {
        String uuid = paraDao.queryValueByName("uuid");
        if (uuid == null) {
            uuid = UUID.randomUUID().toString().replaceAll("-", "");
            SysPara uuidObj = new SysPara("uuid", uuid);
            paraDao.insert(uuidObj);
        }
        devid = uuid;

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


    private byte [] key = "BDE1236987450ACF".getBytes();

    public byte[] getRealData(SysParaDao paraDao) {
        try {
            cmdRptRealData.getJSONObject("data").put("sessionid", NetClient.sessionId);
            cmdRptRealData.getJSONObject("data").put("devid", devid);
            byte[] content = cmdRptRealData.toString().getBytes();
            //byte[] encryptOut = eCipher.doFinal(content);// 加密
            byte[] encryptOut = Aes.encrypt(content, key);
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


    public JSONObject unPack(byte[] pack, int n) {
        if (n < 6) return null;

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

        if (start < 0 || end < 0) return null;


        try {
            int dataLen = (end - 1) - (start + 3) + 1;
            System.arraycopy(pack, start + HEAD_LENGTH, recv, 0, dataLen);

            /*
            for (int i = 0; i < dataLen; i++) {
                System.out.printf("0x%02x ", recv[i] & 0xFF);
            }
            System.out.println("");
            */

            //byte[] decryptOut = eDipher.doFinal(pack, start + HEAD_LENGTH, dataLen);
            byte[] decryptOut = Aes.decrypt(pack, start + HEAD_LENGTH, dataLen, key);
            System.out.println(new String(decryptOut));
            return new JSONObject(new String(decryptOut));


        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
        return null;
    }


}
