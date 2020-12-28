package com.wooddeep.crane.net;

import android.graphics.Color;

import com.wooddeep.crane.CalibrationSetting;
import com.wooddeep.crane.CraneSetting;
import com.wooddeep.crane.MainActivity;
import com.wooddeep.crane.ebus.CalibrationEvent;
import com.wooddeep.crane.net.network.Protocol;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.SysPara;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetClient {

    private volatile Socket socket = null;
    private volatile OutputStream outputStream = null;
    private volatile InputStream inputStream = null;
    private volatile AtomicBoolean reconnFlag = new AtomicBoolean(true);

    private String savedAddr = "47.92.251.221"; //"192.168.141.43";
    private int savedPort = 1733;

    private final byte[] buffer = new byte[10240];//创建接收缓冲区

    private Protocol protocol = new Protocol();

    public static LinkedBlockingQueue mq = new LinkedBlockingQueue(100000);
    public static volatile boolean netOk = false;
    private static SysParaDao paraDao;
    private static CalibrationDao calibrationDao;
    private static CraneDao craneDao;


    public static volatile String sessionId = "initialize";
    public static volatile int timeSlot = 5000;

    public NetClient(String addr, int port) {
        this.savedAddr = addr;
        this.savedPort = port;
    }

    private void reconnect(String mac) {
        System.out.printf("## reconnect to %s:%d\n", savedAddr, savedPort);
        try {
            socket = new Socket(savedAddr, savedPort);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            reconnFlag.set(false);
            netOk = true;
            mq.clear();
            byte[] body = protocol.getSession(paraDao, mac); // 重连之后，
            mq.offer(body);

        } catch (Exception e) {
            reconnFlag.set(true);
            netOk = false;
            System.out.printf("## reconnect[0] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                System.out.printf("## reconnect[1] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
            }
        }
    }

    public void runWrite(String mac) {
        while (true && !MainActivity.sysExit) {

            if (reconnFlag.get()) {
                reconnect(mac);
            }

            try {

                Object message = mq.poll(3, TimeUnit.SECONDS);
                if (message == null) continue;

                if (message instanceof String) { // 配置地址与端口
                    String[] remote = ((String) message).split(":");
                    String addr = remote[0];
                    int port = Integer.parseInt(remote[1]);
                    savedAddr = addr;
                    savedPort = port;
                    reconnFlag.set(true);
                    continue;
                }

                if (socket == null) {
                    continue;
                }

                byte[] body = (byte[]) message;
                int beWriteN = protocol.doPack(body);
                //System.out.printf("## beWriteN = %d: %s\n", beWriteN, message.toString());

                outputStream.write(protocol.getPack(), 0, beWriteN);
                outputStream.flush();

            } catch (Exception e) {
                System.out.printf("## runWrite[0] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    System.out.printf("## runWrite[1] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                }
                reconnFlag.set(true);
            }
        }
    }

    public void runRead() {
        while (true && !MainActivity.sysExit) {
            try {

                if (inputStream == null || reconnFlag.get()) {
                    Thread.sleep(2000);
                    continue;
                }

                int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度

                if (len == -1) {
                    reconnFlag.set(true);
                    continue;
                }

                //System.out.println("## len = " + len);
                JSONObject resp = protocol.unPack(buffer, len);
                //System.out.println(resp.toString());
                if (resp == null) continue;

                // {"cmd":"get.session", "data":{"sessionid": "afdsf123"}}
                String cmd = resp.optString("cmd", "");
                switch (cmd) {
                    case "get.session":
                        sessionId = resp.optJSONObject("data").optString("sessionid", "unknown");
                        break;
                    case "set.timeslot": //{"cmd":"set.timeslot", "data":{"realdata":5000}}
                        timeSlot = resp.optJSONObject("data").optInt("timeslot", 5000); //resp.optJSONObject("data").getInt("timeslot")
                        System.out.println("## timeSlot = " + timeSlot);

                        SysPara timeSlotObj = paraDao.queryParaByName("timeSlot");
                        if (timeSlotObj == null) {
                            timeSlotObj = new SysPara("timeSlot", "5000");
                            paraDao.insert(timeSlotObj);
                        } else {
                            timeSlotObj.setParaValue(String.valueOf(timeSlot));
                            paraDao.update(timeSlotObj); // 存入数据库
                        }

                        byte[] body = protocol.response(cmd); // ack信息
                        NetClient.mq.offer(body);
                        break;

                    case "set.calib":

                        List<Calibration> paras = calibrationDao.selectAll();
                        if (paras.size() < 1) return;
                        Calibration para = paras.get(0);
                        Calibration calibration  = para; // 从系统中导出配置
                        JSONObject calibData = resp.optJSONObject("data");

                        System.out.println("## calib set: " + calibData.toString());

                        float startUartData = (float)calibData.optInt("sad");
                        float endUartData = (float)calibData.optInt("ead");
                        float startDimValue = (float)calibData.optDouble("sval1");
                        float startDimValue2 = (float)calibData.optDouble("sval2");
                        float endDimValue = (float)calibData.optDouble("eval1");
                        float endDimValue2 = (float)calibData.optDouble("eval2");

                        double rate = 1;
                        String type = calibData.optString("type"); // 根据type来做各种设置
                        switch (type) {
                            case "rotate":
                                rate = CalibrationSetting.setRotateRate(calibrationDao, calibration, startUartData,
                                    endUartData, startDimValue, startDimValue2, endDimValue, endDimValue2, MainActivity.gCenterX, MainActivity.gCenterY);
                                break;
                            case "weight":
                                rate = CalibrationSetting.setWeightRate(calibrationDao, calibration, startUartData,
                                    endUartData, startDimValue, endDimValue);
                                break;

                            case "length":
                                rate = CalibrationSetting.setLengthRate(calibrationDao, calibration, startUartData,
                                    endUartData, startDimValue, endDimValue);
                                break;

                            case "height":
                                rate = CalibrationSetting.setHeightRate(calibrationDao, calibration, startUartData,
                                    endUartData, startDimValue, endDimValue);
                                break;

                            default:
                                break;
                        }

                        body = protocol.response(cmd, new JSONObject().put("k", rate)); // ack信息
                        NetClient.mq.offer(body);
                        EventBus.getDefault().post(new CalibrationEvent(calibration)); // TODO 判断正确情况下，何时发起刷新标定信息

                        System.out.println("## calib cal k: " + rate);

                        break;

                    case "start.calib": // 启动标定
                        MainActivity.netCalibFlag.set(true);
                        System.out.println("### cmd: start calibration");

                        protocol.setCfgCalibData(calibrationDao, "all");
                        body = protocol.getCfgCalibData(paraDao);
                        NetClient.mq.offer(body); // 发送传感器数据给服务器

                        break;

                    case "end.calib": // 关闭标定
                        MainActivity.netCalibFlag.set(false);
                        System.out.println("### cmd: end calibration");
                        break;

                    case "get.cfg.crane": // 获取塔基配置
                        CraneSetting.getCraneConfig(craneDao);
                        protocol.setCranePara(craneDao);
                        body = protocol.getCranePara(paraDao);
                        NetClient.mq.offer(body); // 发送传感器数据给服务器
                        break;

                    case "set.cfg.crane":
                        CraneSetting.saveCraneInfo(craneDao, resp.optJSONObject("data"));
                        break;

                    default:
                        //System.out.println("##unkonwn response command!");
                        break;
                }


            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("## runRead[0] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                if (e.getCause() == null) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        System.out.printf("## runRead[1] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                    }
                }
                reconnFlag.set(true);
                System.out.println("## now reconnFlag: " + reconnFlag.get());
            }
        }
    }


    private class NetThread extends Thread {

        private boolean flag;
        private String mac;

        public NetThread(boolean flag, String m) {
            this.flag = flag;
            this.mac = m;
        }

        @Override
        public void run() {
            if (flag) {
                runWrite(mac);
            } else {
                runRead();
            }
        }
    }

    public static void run(SysParaDao dao, CalibrationDao calibDao, CraneDao cd,String mac) {
        paraDao = dao;
        calibrationDao = calibDao;
        craneDao = cd;

        String remoteAddr = "81.69.46.54";
        SysPara ra = paraDao.queryParaByName("remoteAddr");
        if (ra == null) {
            ra = new SysPara("remoteAddr", remoteAddr);
            paraDao.insert(ra);
        } else {
            remoteAddr = ra.getParaValue();
        }

        remoteAddr = "81.69.46.54";

        int remotePort = 1733;
        SysPara rp = paraDao.queryParaByName("remotePort");
        if (rp == null) {
            rp = new SysPara("remotePort", String.valueOf(remotePort));
            paraDao.insert(rp);
        } else {
            remotePort = Integer.parseInt(rp.getParaValue());
        }

        SysPara timeSlotObj = paraDao.queryParaByName("timeSlot");
        if (timeSlotObj == null) {
            timeSlotObj = new SysPara("timeSlot", String.valueOf(5000));
            paraDao.insert(timeSlotObj);
        } else {
            timeSlot = Integer.parseInt(timeSlotObj.getParaValue());
        }

        NetClient client = new NetClient(remoteAddr, remotePort);
        client.new NetThread(true, mac).start();
        client.new NetThread(false, mac).start();
    }
}

