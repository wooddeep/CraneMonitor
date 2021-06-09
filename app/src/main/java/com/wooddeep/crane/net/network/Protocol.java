package com.wooddeep.crane.net.network;


import android.widget.TextView;

import com.wooddeep.crane.CraneSetting;
import com.wooddeep.crane.MainActivity;
import com.wooddeep.crane.R;
import com.wooddeep.crane.net.NetClient;
import com.wooddeep.crane.net.crypto.Aes;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.tookit.EdbTool;
import com.wooddeep.crane.tookit.NetTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Cipher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    // 各种协议
    static JSONObject cmdGetSession = null;
    static JSONObject cmdRptRealData = null;
    static JSONObject cmdResponse = null;

    static JSONObject cmdRptCaliData = null;

    static JSONObject cmdRptCfgCalib = null;


    static JSONObject cmdRptCranePara = null;

    public Protocol() {
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

            cmdRptCaliData = new JSONObject("{\"cmd\":\"rpt.calib\", \"data\":{\n" +
                "    \"devid\":\"12345678\",\n" +
                "    \"sessionid\": \"afdsf123\"\n" +
                "  }\n" +
                "}");

            cmdRptCfgCalib = new JSONObject("{\"cmd\":\"rpt.cfg.calib\", \"data\":{\n" +
                "    \"devid\":\"12345678\",\n" +
                "    \"sessionid\": \"afdsf123\"\n" +
                "  }\n" +
                "}");


            cmdRptCranePara = new JSONObject("{\"cmd\":\"rpt.cfg.crane\", \"data\":{\n" +
                "    \"devid\":\"12345678\",\n" +
                "    \"sessionid\": \"afdsf123\"\n" +
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

    public byte[] response(String cmd, JSONObject data) {
        try {
            cmdResponse.put("cmd", cmd);
            cmdResponse.put("data", data);
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
    public byte[] getSession(SysParaDao paraDao, String mac) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        /*
        JSONArray out = EdbTool.extTableQuery("forever.db", "syspara", "select paraValue from syspara where paraName='uuid'");
        if (out.length() == 0) {
            String uuid = String.format("%s@%s", mac, sdf.format(new Date()));
            EdbTool.extTableExec("forever.db", "syspara", String.format("insert into syspara (paraName, paraValue) values ('uuid', '%s')", uuid));
            devid = uuid;
        } else {
            try {
                devid = out.getJSONObject(0).getString("paraValue");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */

        //String mac = NetTool.getMacAddress(context).replaceAll(":", "");
        //devNoView.setText("DN: " + mac.substring(mac.length() - 6, mac.length()));

        String uuid = paraDao.queryValueByName("uuid");
        if (uuid == null || uuid.length() == 0 || uuid.equals("47d69b6fe798495abd2abdfa05a41b68")) {
            uuid = String.format("%s", mac.replaceAll(":", ""));
            //EdbTool.extTableExec("forever.db", "syspara", String.format("insert into syspara (paraName, paraValue) values ('uuid', '%s')", uuid));
            SysPara uuidPara = new SysPara("uuid", uuid);
            paraDao.insert(uuidPara);
        }

        devid = uuid; // 设备ID是生成的UUID TODO， 主界面显示这个ID

        try {
            cmdGetSession.getJSONObject("data").put("devid", devid);
            byte[] content = cmdGetSession.toString().getBytes();
            //byte[] encryptOut = eCipher.doFinal(content);// 加密
            byte[] encryptOut = Aes.encrypt(content, "BDE1236987450ACF".getBytes());
            return encryptOut;
        } catch (Exception e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }

        return null;
    }


    private static byte[] key = "BDE1236987450ACF".getBytes();


    public void setRealData(float rotate, float angle, float bigArmLen, float carRange,
                            float weight, float moment, float windSpeed, float height,
                            float hookHeight, int ropeNum, boolean isMain, String type, String craneType) {
        try {
            JSONObject data = cmdRptRealData.getJSONObject("data");
            data.put("rotate", rotate);
            data.put("angle", angle);
            data.put("bigArmLen", bigArmLen);
            data.put("carRange", carRange);
            data.put("weight", weight);
            data.put("moment", moment);
            data.put("windSpeed", windSpeed);
            data.put("height", height);
            data.put("hookHeight", hookHeight);
            data.put("ropeNum", ropeNum);
            data.put("isMain", isMain);
            data.put("type", type);
            data.put("craneType", craneType);

            //System.out.println(data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

    public void setCalibData(float centerX, float centerY, float angle, int craneType,
                             float bigArmLen, byte[] rotate, byte[] scale) {
        try {
            JSONObject data = cmdRptCaliData.getJSONObject("data");
            data.put("centerX", centerX);
            data.put("centerY", centerY);
            data.put("angle", angle);
            data.put("craneType", craneType);
            data.put("bigArmLen", bigArmLen);

            JSONArray rArray = new JSONArray();
            if (rotate != null) {
                for (int i = 0; i < rotate.length; i++) {
                    rArray.put(rotate[i] & 0x000000FF);
                }
                data.put("rotate", rArray);
            }

            if (scale != null) {
                JSONArray sArray = new JSONArray();
                for (int i = 0; i < scale.length; i++) {
                    sArray.put(scale[i] & 0x000000FF);
                }
                data.put("scale", sArray);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public byte[] getCalibData(SysParaDao paraDao) {
        try {
            cmdRptCaliData.getJSONObject("data").put("sessionid", NetClient.sessionId);
            cmdRptCaliData.getJSONObject("data").put("devid", devid);

            //System.out.println(cmdRptCaliData.toString());

            byte[] content = cmdRptCaliData.toString().getBytes();
            byte[] encryptOut = Aes.encrypt(content, key);
            return encryptOut;
        } catch (Exception e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }

        return null;
    }

    public static void setCfgCalibData(CalibrationDao dao, String type) {
        try {

            List<Calibration> paras = dao.selectAll();
            if (paras.size() < 1) return;
            Calibration para = paras.get(0);
            cmdRptCfgCalib.put("data", new JSONObject());
            JSONObject data = cmdRptCfgCalib.getJSONObject("data");

            switch (type) {
                case "all":
                    data.put("weight", new JSONObject().put("sad", para.weightStartData).put("sval1", para.weightStart)
                        .put("ead", para.weightEndData).put("eval1", para.weightEnd).put("k", "" + para.weightRate));

                    data.put("height", new JSONObject().put("sad", para.heightStartData).put("sval1", para.heightStart)
                        .put("ead", para.heightEndData).put("eval1", para.heightEnd).put("k", "" + para.heightRate));

                    data.put("length", new JSONObject().put("sad", para.lengthStartData).put("sval1", para.lengthStart)
                        .put("ead", para.lengthEndData).put("eval1", para.lengthEnd).put("k", "" + para.lengthRate));

                    data.put("rotate", new JSONObject().put("sad", para.rotateStartData).put("sval1", para.rotateStartX1).put("sval2", para.rotateStartY1)
                        .put("ead", para.rotateEndData).put("eval1", para.rotateEndX2).put("eval2", para.rotateEndY2).put("k", "" + para.rotateRate));

                    break;
                case "weight":
                    data.put("weight", new JSONObject().put("sad", para.weightStartData).put("sval1", para.weightStart)
                        .put("ead", para.weightEndData).put("eval1", para.weightEnd).put("k", "" + para.weightRate));

                    break;
                case "height":
                    data.put("height", new JSONObject().put("sad", para.heightStartData).put("sval1", para.heightStart)
                        .put("ead", para.heightEndData).put("eval1", para.heightEnd).put("k", "" + para.heightRate));

                    break;
                case "length":
                    data.put("length", new JSONObject().put("sad", para.lengthStartData).put("sval1", para.lengthStart)
                        .put("ead", para.lengthEndData).put("eval1", para.lengthEnd).put("k", para.lengthRate));

                    break;
                case "rotate":
                    data.put("rotate", new JSONObject().put("sad", para.rotateStartData).put("sval1", para.rotateStartX1).put("sval2", para.rotateStartY1)
                        .put("ead", para.rotateEndData).put("eval1", para.rotateEndX2).put("eval2", para.rotateEndY2).put("k", para.rotateRate));
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getCfgCalibData(SysParaDao paraDao) {
        try {
            cmdRptCfgCalib.getJSONObject("data").put("sessionid", NetClient.sessionId);
            cmdRptCfgCalib.getJSONObject("data").put("devid", devid);

            System.out.println(cmdRptCfgCalib.toString());

            byte[] content = cmdRptCfgCalib.toString().getBytes();
            byte[] encryptOut = Aes.encrypt(content, key);
            return encryptOut;
        } catch (Exception e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }

        return null;
    }


    public void setCranePara(CraneDao dao) {
        try {
            JSONObject data = CraneSetting.getCraneConfig(dao);
            cmdRptCranePara.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public byte[] getCranePara(SysParaDao paraDao) {
        try {
            cmdRptCranePara.getJSONObject("data").put("sessionid", NetClient.sessionId);
            cmdRptCranePara.getJSONObject("data").put("devid", devid);

            System.out.println(cmdRptCranePara.toString());

            byte[] content = cmdRptCranePara.toString().getBytes();
            byte[] encryptOut = Aes.encrypt(content, key);
            return encryptOut;
        } catch (Exception e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }

        return null;
    }

    public byte[] createBody(JSONObject raw) {
        try {
            raw.getJSONObject("data").put("sessionid", NetClient.sessionId);
            raw.getJSONObject("data").put("devid", devid);

            System.out.println(raw.toString());

            byte[] content = raw.toString().getBytes();
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

        JSONObject out = null;

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

        int dataLen = (end - 1) - (start + 3) + 1;
        try {
            System.arraycopy(pack, start + HEAD_LENGTH, recv, 0, dataLen);
            byte[] decryptOut = Aes.decrypt(pack, start + HEAD_LENGTH, dataLen, key);
            out = new JSONObject(new String(decryptOut));

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println(e.getMessage());
            MainActivity.netRadioProto.lock.writeLock().lock();
            //out = new JSONObject(new String(recv, 0, dataLen));
            System.arraycopy(recv, 0, MainActivity.netRadioProto.netBuffer, 0, dataLen);
            MainActivity.netRadioProto.length = dataLen;
            MainActivity.netRadioProto.lock.writeLock().unlock();
        }

        //System.out.println(out);
        return out;
    }


}
