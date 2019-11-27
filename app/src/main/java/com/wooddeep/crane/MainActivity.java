package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.x6.serialportlib.SerialPort;
import com.wooddeep.crane.alarm.Alarm;
import com.wooddeep.crane.alarm.AlertSound;
import com.wooddeep.crane.comm.ControlProto;
import com.wooddeep.crane.comm.Protocol;
import com.wooddeep.crane.comm.RadioProto;
import com.wooddeep.crane.comm.RotateProto;
import com.wooddeep.crane.ebus.AlarmDetectEvent;
import com.wooddeep.crane.ebus.AlarmEvent;
import com.wooddeep.crane.ebus.AlarmSetEvent;
import com.wooddeep.crane.ebus.CalibrationCloseEvent;
import com.wooddeep.crane.ebus.CalibrationEvent;
import com.wooddeep.crane.ebus.FanSpeedEvent;
import com.wooddeep.crane.ebus.HeightEvent;
import com.wooddeep.crane.ebus.LengthEvent;
import com.wooddeep.crane.ebus.RadioEvent;
import com.wooddeep.crane.ebus.RotateEvent;
import com.wooddeep.crane.ebus.SimulatorEvent;
import com.wooddeep.crane.ebus.SysParaEvent;
import com.wooddeep.crane.ebus.UartEvent;
import com.wooddeep.crane.ebus.WeightEvent;
import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.ElemMap;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.main.Constant;
import com.wooddeep.crane.main.SavedData;
import com.wooddeep.crane.main.ShowData;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.LoadDbHelper;
import com.wooddeep.crane.persist.LogDbHelper;
import com.wooddeep.crane.persist.dao.AlarmSetDao;
import com.wooddeep.crane.persist.dao.AreaDao;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.CraneParaDao;
import com.wooddeep.crane.persist.dao.ProtectAreaDao;
import com.wooddeep.crane.persist.dao.ProtectDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.dao.TcParamDao;
import com.wooddeep.crane.persist.dao.log.CtrlRecDao;
import com.wooddeep.crane.persist.dao.log.RealDataDao;
import com.wooddeep.crane.persist.dao.log.SwitchRecDao;
import com.wooddeep.crane.persist.dao.log.WorkRecDao;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.CranePara;
import com.wooddeep.crane.persist.entity.Protect;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.persist.entity.TcParam;
import com.wooddeep.crane.persist.entity.log.CaliRec;
import com.wooddeep.crane.persist.entity.log.CtrlRec;
import com.wooddeep.crane.persist.entity.log.RealData;
import com.wooddeep.crane.persist.entity.log.SwitchRec;
import com.wooddeep.crane.persist.entity.log.WorkRecrod;
import com.wooddeep.crane.simulator.SimulatorFlags;
import com.wooddeep.crane.simulator.UartEmitter;
import com.wooddeep.crane.tookit.AnimUtil;
import com.wooddeep.crane.tookit.CommTool;
import com.wooddeep.crane.tookit.DataUtil;
import com.wooddeep.crane.tookit.MathTool;
import com.wooddeep.crane.tookit.MomentOut;
import com.wooddeep.crane.tookit.SysTool;
import com.wooddeep.crane.tookit.TcpClient;
import com.wooddeep.crane.views.CraneView;
import com.wooddeep.crane.views.Vertex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//import androidx.annotation.RequiresApi;

// adb shell am start com.android.settings/com.android.settings.Settings

// 启动mumu之后, 输入：
// adb connect 127.0.0.1:7555
// 然后再调试, 就ok了


@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TAG = "MainActivity";
    private volatile android.app.Activity activity = this;
    private Context context;

    private ConcurrentHashMap<String, SavedData> savedDataMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CycleElem> craneMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, SavedData> slaveMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> radioStatusMap = new ConcurrentHashMap();
    private List<String> craneNumbers = new ArrayList<>();
    private List<BaseElem> elemList = new ArrayList<>();
    private List<TcParam> loadParas = new ArrayList<>();
    private ElemMap elemMap = new ElemMap();

    private int currSlaveIndex = 0; // 当前和本主机通信的从机名称
    private float shadowLength = 0;
    private String myCraneNo = "";
    private float oscale = 1.0f;
    private int craneType = 0; // 塔基类型: 0 ~ 平臂式, 2动臂式
    private int iPower = 2; // 倍率

    private RotateProto currRotateProto = new RotateProto();
    private RotateProto prevRotateProto = new RotateProto();
    private RadioProto radioProto = new RadioProto();
    private Protocol prevProto = new Protocol(); // 记录前次ad数据解析结果
    private Protocol currProto = new Protocol(); // 记录当次ad数据解析结果

    private CenterCycle centerCycle; // 本塔基圆环
    private Crane mainCrane;   // 本塔基参数

    private EventBus eventBus = EventBus.getDefault();
    private UartEmitter emitter = new UartEmitter();
    private Calibration calibration = new Calibration();
    private AlarmSet alarmSet = new AlarmSet();

    private AtomicBoolean iAmMaster = new AtomicBoolean(false); // 本机是否为通信主机
    private SimulatorFlags flags = new SimulatorFlags();
    public static boolean calibrationFlag = false;
    private boolean isMasterCrane = false; // 是否主塔机
    private boolean waitFlag = true; // 等待主机信号标识
    private boolean superSuper = false;
    public static AtomicBoolean isRvcMode = new AtomicBoolean(false);

    private ControlProto controlProto = new ControlProto();
    private RadioProto slaveRadioProto = new RadioProto();  // 本机作为从机时，需要radio通信的对象
    private RadioProto masterRadioProto = new RadioProto(); // 本机作为主机时，需要radio通信的对象

    private SerialPort serialttyS0;
    private SerialPort serialttyS1;
    private SerialPort serialttyS2;
    private InputStream ttyS0InputStream = null;
    private OutputStream ttyS0OutputStream = null;
    private InputStream ttyS1InputStream = null;
    private OutputStream ttyS1OutputStream = null;
    private OutputStream ttyS2OutputStream = null;
    private InputStream ttyS2InputStream = null;

    private byte[] rotateCmd = new byte[]{0x01, 0x04, 0x00, 0x01, 0x00, 0x02, 0x20, 0x0B};
    private byte[] adXBuff = new byte[2048];
    private byte[] rotateXBuff = new byte[1024];
    private byte[] radioXBuff = new byte[1024];
    private byte[] radioYBuff = new byte[1024];
    private byte[] adRBuff = new byte[20];
    private byte[] rotateRBuff = new byte[10];
    private byte[] radioRBuff = new byte[40];

    private AlarmDetectEvent alarmDetectEvent = new AlarmDetectEvent();
    private HeightEvent heightEvent = new HeightEvent();
    private WeightEvent weightEvent = new WeightEvent();
    private LengthEvent lengthEvent = new LengthEvent();
    private RotateEvent rotateEvent = new RotateEvent();
    private RadioEvent radioEvent = new RadioEvent();
    private UartEvent uartEvent = new UartEvent();
    private AlarmEvent alarmEvent = null;

    private SysParaDao paraDao; // 系统参数
    private TcParamDao tcParamDao;
    private RealDataDao realDataDao; // 工作记录DAO
    private RealData realData = new RealData(); // 工作记录
    private CtrlRecDao ctrlRecDao;
    private CtrlRec ctrlRec = new CtrlRec(); // 控制记录
    private WorkRecDao workRecDao; // 工作记录DAO
    private WorkRecrod workRec = new WorkRecrod(); // 工作记录

    private SwitchRecDao switchRecDao;
    private SwitchRec switchRec = new SwitchRec();

    private CraneView craneView;
    private TextView angleView;
    private TextView vAngleView;
    private TextView leftAlarmView;
    private TextView rightAlarmView;
    private TextView forwardAlarmView;
    private TextView backwardAlarmView;
    private TextView weightAlarmView;
    private TextView hookAlarmView;
    private TextView momentAlarmView;
    private TextView momentView;
    private TextView ratedWeightView;
    private TextView weightView;
    private TextView masterNoView;
    private TextView heightView;
    private TextView windSpeedView;
    private MediaPlayer player;

    private boolean sysExit = false;
    private Intent intent = null;

    private float startWeight = 0.3f;
    private float endWeight = 0.2f;
    private float currWeight = 0.3f;
    private PackageManager mPackageManager;
    private DataUtil dataUtil = new DataUtil();
    public static ShowData showData = new ShowData();

    public static AtomicInteger alarmLevel = new AtomicInteger(100);

    public Float currXAngle = 0.0f;

    public float getOscale() {
        return oscale;
    }

    public void setOscale(float oscale) {
        this.oscale = oscale;
    }

    private void startSensorThread() {
        new Thread(() -> {
            int counter = 0;
            while (true && !sysExit) {

                if (centerCycle == null) {
                    CommTool.sleep(100);
                    continue;
                }

                if (ttyS0InputStream == null || ttyS1InputStream == null || ttyS2InputStream == null) {
                    CommTool.sleep(100);
                    continue;
                }

                try {
                    if (ttyS0InputStream.available() > 0) { // AD数据
                        int len = ttyS0InputStream.read(adXBuff, 0, 2048);

                        for (int i = 0; i < 20; i++) {
                            adRBuff[i] = adXBuff[i];
                        }

                        currProto.parse(adRBuff);
                        currProto.calcRealLength(calibration);
                        currProto.calcRealWeigth(calibration);
                        currProto.calcRealVAngle(calibration);
                        currProto.calcRealHookHeight(calibration, centerCycle.getType(), centerCycle.getBigArmLen(), currProto.getRealVAngle()); // 计算吊钩高度

                        if (centerCycle.getType() == 1) { // 动臂式
                            if (Math.abs(currProto.getRealVAngle() - prevProto.getRealVAngle()) > 0.05f) { // TODO 调试
                                prevProto.setRealVAngle(currProto.getRealVAngle());
                                runOnUiThread(() -> {
                                    craneView.setArmAngle(currProto.getRealVAngle());
                                    centerCycle.setCarRange(centerCycle.getBigArmLen());
                                    centerCycle.setVAngle(currProto.getRealVAngle());
                                    vAngleView.setText(currProto.getRealVAngle() + "°");
                                    double deltaHeight = centerCycle.getBigArmLen() * Math.sin(Math.toRadians(currProto.getRealVAngle()));

                                    //centerCycle.setHeight(centerCycle.getOrgHeight() + (float) deltaHeight); // 修改高度
                                    centerCycle.setHeight(centerCycle.getOrgHeight() + (float) 0);

                                    System.out.println("### " + centerCycle.getOrgHeight() + "@@" + deltaHeight + "$$" + centerCycle.height + "&&" + currProto.getRealVAngle());

                                    float shadow = centerCycle.getBigArmLen() * (float) Math.cos(Math.toRadians(currProto.getRealVAngle())) - centerCycle.getArchPara();
                                    shadow = Math.round(shadow * 10) / 10.0f;
                                    //lengthShow(shadow);
                                    shadowLength = shadow;
                                    //System.out.println("--1--" + shadowLength);
                                    lengthEvent.setLength(shadow);
                                    eventBus.post(lengthEvent);
                                    MomentOut moment = MathTool.momentCalc(loadParas, currProto.getRealWeight(), shadow);
                                    weigthChangeShow(moment.moment, moment.ratedWeight);
                                });
                            }
                        } else { // 平臂式
                            if (Math.abs(currProto.getRealLength() - prevProto.getRealLength()) > 0.05f) {
                                lengthEvent.setLength(currProto.getRealLength());
                                prevProto.setRealLength(currProto.getRealLength());
                                shadowLength = currProto.getRealLength();
                                eventBus.post(lengthEvent);
                                MomentOut moment = MathTool.momentCalc(loadParas, currProto.getRealWeight(), currProto.getRealLength());
                                runOnUiThread(() -> {
                                    weigthChangeShow(moment.moment, moment.ratedWeight);
                                    vAngleView.setText("0°");
                                });
                            }
                        }

                        if (Math.abs(currProto.getRealHookHeight() - prevProto.getRealHookHeight()) > 0.05f) { // 吊钩高度变化
                            heightEvent.setHeight(currProto.getRealHookHeight());
                            prevProto.setRealHookHeight(currProto.getRealHookHeight()); // 替换吊钩实际高度
                            eventBus.post(heightEvent);
                        }

                        if (Math.abs(currProto.getRealWeight() - prevProto.getRealWeight()) > 0.05f) {
                            weightEvent.setWeight(currProto.getRealWeight());
                            prevProto.setRealWeight(currProto.getRealWeight());
                            eventBus.post(weightEvent);

                            if (centerCycle.getType() == 1) {
                                MomentOut moment = MathTool.momentCalc(loadParas, currProto.getRealWeight(), shadowLength);
                                //System.out.println("### 1 = " + moment.moment);
                                runOnUiThread(() -> momentShow(moment.moment));
                            } else {
                                MomentOut moment = MathTool.momentCalc(loadParas, currProto.getRealWeight(), currProto.getRealLength());
                                //System.out.println("### 0 = " + moment.moment);
                                runOnUiThread(() -> momentShow(moment.moment));
                            }
                        }

                        if (Math.abs(currProto.getWindSpeed() - prevProto.getWindSpeed()) >= 17) { // 风速
                            prevProto.setWindSpeed(currProto.getWindSpeed());
                            runOnUiThread(() -> {
                                float windSpeed = currProto.getWindSpeed() * 30 / 4096;
                                windSpeed = Math.round(windSpeed * 10) / 10.0f;
                                ((TextView) findViewById(R.id.wind_speed)).setText(windSpeed + "m/s");
                            });
                        }

                        if (calibrationFlag) {
                            uartEvent.craneType = centerCycle.getType();
                            uartEvent.bigArmLength = centerCycle.getBigArmLen();
                            uartEvent.setData(adRBuff);
                            eventBus.post(uartEvent); // 发送通知标定模块数据
                        }
                    }

                    // 编码器100毫秒一次读一次
                    ttyS2OutputStream.write(rotateCmd);
                    if (ttyS2InputStream.available() > 0) { // 回转数据
                        int len = ttyS2InputStream.read(rotateXBuff, 0, 1024);
                        for (int i = 0; i < 9; i++) {
                            rotateRBuff[i] = rotateXBuff[i];
                        }
                        if (rotateRBuff[0] == 0x01 && rotateRBuff[1] == 0x04 && rotateRBuff[2] == 0x04) {
                            currRotateProto.parse(rotateRBuff);
                            currRotateProto.calcAngle(calibration);

                            double xangle = currRotateProto.getAngle();
                            if (xangle < 0) {
                                xangle = 360 - (Math.abs(xangle) % 360);
                            } else {
                                xangle = xangle % 360;
                            }
                            //xangle = Math.round(xangle * 10) / 10.0f;
                            //System.out.println("xangle:" + xangle);
                            currXAngle = (float)xangle;

                            if (Math.abs(currRotateProto.getAngle() - prevRotateProto.getAngle()) >= 0.1f) {
                                rotateEvent.setCenterX(mainCrane.getCoordX1());
                                rotateEvent.setCenterY(mainCrane.getCoordY1());
                                rotateEvent.setAngle((float) currRotateProto.getAngle());
                                rotateEvent.setData(rotateRBuff);
                                prevRotateProto.setAngle(currRotateProto.getAngle());

                                double angle = currRotateProto.getAngle();
                                if (angle < 0) {
                                    angle = 360 - (Math.abs(angle) % 360);
                                } else {
                                    angle = angle % 360;
                                }
                                angle = Math.round(angle * 10) / 10.0f;

                                final float minAngle = (float) angle;
                                runOnUiThread(() -> rotateShow(minAngle));
                            }
                        }

                        if (calibrationFlag) {
                            eventBus.post(rotateEvent);
                        }
                    }

                    CommTool.sleep(100);
                    counter++;

                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }).start();
    }

    private void startRadioReadThread() {

        new Thread(() -> {

            while (true && !sysExit) {

                if (ttyS0InputStream == null || ttyS1InputStream == null || ttyS2InputStream == null) {
                    CommTool.sleep(100);
                    continue;
                }

                try {
                    if (ttyS1InputStream.available() > 0) {
                        int len = ttyS1InputStream.read(radioXBuff, 0, 1024);

                        dataUtil.add(radioXBuff, len);

                        if (dataUtil.check()) {
                            radioEvent.setData(dataUtil.get());
                            RadioDateEventOps(radioEvent);
                        }
                    }

                    CommTool.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }).start();
    }

    private void startRadioWriteThread() {
        new Thread(() -> {

            while (true && !sysExit) {

                if (ttyS0InputStream == null || ttyS1InputStream == null || ttyS2InputStream == null) {
                    CommTool.sleep(100);
                    continue;
                }

                try {
                    if (iAmMaster.get() && craneNumbers.size() >= 1) { // 主机
                        //System.out.println("##### I am master!!!");
                        int iMyCraneNo = Integer.parseInt(myCraneNo);
                        currSlaveIndex = (currSlaveIndex + 1) % craneNumbers.size();
                        int targetNo = Integer.parseInt(craneNumbers.get(currSlaveIndex));
                        //if (iMyCraneNo == targetNo) continue;
                        masterRadioProto.setSourceNo(iMyCraneNo);
                        masterRadioProto.setTargetNo(targetNo);
                        masterRadioProto.setPermitNo(targetNo);

                        double currAngle = Math.max(Math.toRadians(currXAngle/*centerCycle.getHAngle()*/), 0); // 当前回转角度
                        double currRange = Math.max(shadowLength, 0);

                        masterRadioProto.setRotate(Math.round(currAngle * 10) / 10.0f);
                        masterRadioProto.setRange(Math.round(currRange * 10) / 10.0f);

                        //System.out.printf("### master write data: %f -- %f \n", currAngle, currRange); // TODO 幅度值，角度值不对
                        masterRadioProto.packReply(); // 生成回应报文
                        //StringTool.showCharArray1(masterRadioProto.modleChars);

                        try {
                            //ttyS1OutputStream.write(masterRadioProto.modleBytes); // 发送应答报文
                            ttyS1OutputStream.write(masterRadioProto.modleBytes, 0, 39);
                            ttyS1OutputStream.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }

                    CommTool.sleep(130);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }).start();
    }

    private void recPowerOnOff(Date date) {
        RealData realData = realDataDao.queryLatestOne();
        try {
            Date latest = new Date();
            if (realData != null) {
                latest = sdf.parse(realData.getTime());
            }

            long prevMsec = latest.getTime();
            long currMesc = System.currentTimeMillis();

            if (currMesc - prevMsec > 60 * 1000) { // 大于1分钟, 则记录
                switchRec.setTime(realData.getTime());
                switchRec.setAction("power off");
                switchRecDao.insert(switchRec);
                switchRec.setTime(sdf.format(date));
                switchRec.setAction("power on");
                switchRecDao.insert(switchRec);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTimerThread() {

        AlertSound.init(getApplicationContext());
        new Thread(() -> {
            int count = 0;

            while (true && !sysExit) {
                CommTool.sleep(100);
                count++;

                if (count % 500 == 0) {
                    setCurrTime();
                }

                if (count % 20 == 0) {
                    AlertSound.open(alarmLevel.get());
                }

                //倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
                if (count % 50 == 0) { // 没5秒钟记录一次
                    Date date = new Date();
                    recPowerOnOff(date);
                    String dateNowStr = sdf.format(date);
                    realData.setTime(dateNowStr);
                    realData.setRopenum(iPower); // 倍率
                    realData.setHeigth(Float.parseFloat(heightView.getText().toString().split("m")[0]));
                    if (centerCycle != null) {
                        realData.setRange(centerCycle.carRange);
                        realData.setRotate(centerCycle.getHAngle());
                        realData.setDipange(centerCycle.vAngle);
                    }
                    realData.setRatedweight(Float.parseFloat(ratedWeightView.getText().toString().split("t")[0]));
                    realData.setWeight(Float.parseFloat(weightView.getText().toString().split("t")[0]));
                    realData.setWindspeed(Float.parseFloat(windSpeedView.getText().toString().split("m")[0]));
                    realDataDao.insert(realData);
                }

                if (count % 6 == 0) { // 喂软件狗
                    feedWatchDog();
                }

                if (ttyS0InputStream == null || ttyS1InputStream == null || ttyS2InputStream == null) {
                    continue;
                }

                if (craneMap.isEmpty()) {
                    continue;
                }

                if (count % 6 == 0) { // 告警判断
                    try {
                        Alarm.alarmDetect(calibration, currProto.getRealHookHeight(), shadowLength,
                            elemList, craneMap, myCraneNo, alarmSet, eventBus); // 回转告警判断
                        Alarm.weightAlarmDetect(calibration, loadParas, alarmSet, eventBus,
                            currProto.getRealWeight(), shadowLength); // 吊重告警判断
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        }).start();
    }

    private void watchDogThread() {

        new Thread(() -> {

            boolean live = true;

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for (Thread thread : threadSet) {
                if (thread.getName().equals("watchdog")) {
                    System.out.println("## thread exist, I will exit!");
                    live = false;
                }
            }

            int count = 0;
            if (live) {
                Thread.currentThread().setName("watchdog");
                while (true && !sysExit) {
                    CommTool.sleep(100);
                    count++;
                    if (count % 100 == 0) {
                        //System.out.println(sdf.format(new Date()));
                        System.out.println("## I will finish!");
                        System.out.println("## activity = " + activity);
                        // 获取activity的值
                        activity.finish();
                    }

                    if (sysExit) {
                        CommTool.sleep(2000);
                        System.out.println(sdf.format(new Date()) + ": I will launch again");
                        launchPackage("com.wooddeep.crane", 1);
                        sysExit = false;
                    }
                }
            }
        }).start();
    }

    // 侦听电台数据
    public void RadioDateEventOps(RadioEvent event) {
        int cmdRet = radioProto.parse(event.getData()); // 解析电台数据
        if (cmdRet == -1) return;

        if (iAmMaster.get() && radioProto.isQuery) return;

        if (cmdRet == RadioProto.CMD_START_MASTER && waitFlag == true) { // 启动主机命令
            iAmMaster.set(true);
            runOnUiThread(() -> masterNoView.setText(myCraneNo));
            waitFlag = false;
            return;
        }

        if (radioProto.sourceNo.equals(myCraneNo)) return; // TODO 再次验证

        long currTime = System.currentTimeMillis(); // 当前时间

        if (radioProto.isQuery) { // 收到主机的查询命令，本机必然为从机
            waitFlag = false;

            CycleElem master = craneMap.get(radioProto.getSourceNo()); // 作为从机, 更新主机的信息 // TODO 根据塔基类型，计算仰角
            runOnUiThread(() -> masterNoView.setText(radioProto.getSourceNo())); // TODO 缓存

            if (master != null) {
                runOnUiThread(() -> {
                    master.setColor(Color.rgb(46, 139, 87));
                });

                SavedData savedData = savedDataMap.get(radioProto.getSourceNo());
                if (savedData == null) {
                    savedData = new SavedData(0, 0);
                    savedDataMap.put(radioProto.getSourceNo(), savedData);
                }

                if (Math.abs(radioProto.getRange() - savedData.range) >= 0.1f) {
                    runOnUiThread(() -> {
                        if (master.type == 1) { // 动臂式
                            double vangle = MathTool.calcVAngle(master.getBigArmLen(), radioProto.getRange(), master.archPara);
                            master.setVAngle((float) vangle); // 设置动臂式的仰角
                            master.setHeight(master.getOrgHeight() + master.getBigArmLen() * (float) Math.sin(Math.toRadians(vangle))); // 0 -> vangle
                            master.setCarRange(master.getBigArmLen()); // 动臂式 幅度最大
                        } else {
                            master.setCarRange(radioProto.getRange()); // 平臂式实际幅度
                        }
                    });
                    savedData.range = radioProto.getRange();
                }

                float hangle = MathTool.radiansToAngle(radioProto.getRotate());
                if (Math.abs(hangle - savedData.angle) >= 0.1) {
                    runOnUiThread(() -> {
                        //System.out.println("## rotateShow, angle = " + hangle);
                        master.setHAngle(hangle);
                        master.setOnline(true);
                    });
                    savedData.angle = hangle;
                }
            }

            if (radioProto.getTargetNo() == null || radioProto.getSourceNo() == null) return;

            if (radioProto.getSourceNo().equals(radioProto.getTargetNo()) // 源ID和目标ID相同
                || radioProto.getTargetNo().equals(myCraneNo)) { // 目标ID相同是本机
                slaveRadioProto.setSourceNo(Integer.parseInt(myCraneNo));
                slaveRadioProto.setTargetNo(0); // 固定为0
                slaveRadioProto.setPermitNo(0);

                if (centerCycle.type == 1) { // 动臂式, 计算投影值
                    float shadow = (float) MathTool.calcShadow(centerCycle.getBigArmLen(), currProto.getRealVAngle(), centerCycle.archPara);
                    shadow = Math.round(shadow * 10) / 10.0f;
                    slaveRadioProto.setRange(Math.max(shadow, 0));
                } else {
                    slaveRadioProto.setRange(Math.max(centerCycle.carRange, 0));
                }

                slaveRadioProto.setRotate(Math.max(0, ((currXAngle /*centerCycle.getHAngle()*/ % 360) * 2 * (float) Math.PI / 360)));
                slaveRadioProto.packReply(); // 生成回应报文
                try {
                    if (ttyS1OutputStream != null) {
                        ttyS1OutputStream.write(slaveRadioProto.modleBytes, 0, 39);
                        ttyS1OutputStream.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }

            radioStatusMap.put(radioProto.getSourceNo(), currTime);
        }

        if (!radioProto.isQuery) { // 收到其他从机的回应命令 & 分自己的 主从 身份
            waitFlag = false;
            // 更新从机

            CycleElem slave = craneMap.get(radioProto.getSourceNo());
            if (slave != null) {

                SavedData savedData = savedDataMap.get(radioProto.getSourceNo());
                if (savedData == null) {
                    savedData = new SavedData(0, 0);
                    savedDataMap.put(radioProto.getSourceNo(), savedData);
                }

                runOnUiThread(() -> {
                    slave.setColor(Color.rgb(46, 139, 87));
                });

                if (Math.abs(radioProto.getRange() - savedData.range) >= 0.1f) {
                    runOnUiThread(() -> {
                        if (slave.type == 1) { // 动臂式
                            System.out.println("## slave0: " + slave.getBigArmLen() + "@@" + radioProto.getRange() + "$$" + slave.archPara);
                            double vangle = MathTool.calcVAngle(slave.getBigArmLen(), radioProto.getRange(), slave.archPara);
                            slave.setVAngle((float) vangle); // 设置动臂式的仰角
                            System.out.println("## slave1: " + slave.getOrgHeight() + "@@" + slave.getBigArmLen() + "$$" + vangle);
                            slave.setHeight(slave.getOrgHeight() + slave.getBigArmLen() * (float) Math.sin(Math.toRadians(vangle))); // 0 -> vangle
                            slave.setCarRange(slave.getBigArmLen()); // 动臂式 幅度最大
                        } else {
                            slave.setCarRange(radioProto.getRange()); // 平臂式实际幅度
                        }

                    });
                    savedData.range = radioProto.getRange();
                }

                float hangle = MathTool.radiansToAngle(radioProto.getRotate()); // 水平方向的夹角
                if (Math.abs(hangle - savedData.angle) >= 0.1) {
                    runOnUiThread(() -> {
                        slave.setHAngle(hangle);
                    });
                    savedData.angle = hangle;
                }

                slave.setOnline(true); // 设置离线状态
                radioStatusMap.put(radioProto.getSourceNo(), currTime);
            }
        }

        Set<String> radioRecSet = radioStatusMap.keySet();
        for (String no : radioRecSet) {
            long prevRecTimer = radioStatusMap.get(no); // 上次记录时间
            if (currTime - prevRecTimer > 60000) { // 通信10失联，判断超时
                runOnUiThread(() -> {
                    craneMap.get(no).setColor(Color.LTGRAY);
                    craneMap.get(no).setCarRange(0); // 失联设备, 设置小车幅度为0
                });
                craneMap.get(no).setOnline(false); // 设置离线状态
            }
        }
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AlarmSetEventBus(AlarmSetEvent event) {
        alarmSet = event.alarmSet; // 更新配置
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CalibrationEventBus(CalibrationEvent event) {
        calibration = event.calibration;
    }

    private void recCtrlLog() {
        ctrlRec.setTime(sdf.format(new Date()));
        ctrlRec.setCarOut2(controlProto.isCarOut2());
        ctrlRec.setCarOut1(controlProto.isCarOut1());
        ctrlRec.setRotate5(controlProto.isRotate5());
        ctrlRec.setRotate4(controlProto.isRotate4());
        ctrlRec.setRotate3(controlProto.isRotate3());
        ctrlRec.setRotate2(controlProto.isRotate2());
        ctrlRec.setLeftRote(controlProto.isLeftRote());
        ctrlRec.setRightRote(controlProto.isRightRote());
        ctrlRec.setMoment3(controlProto.isMoment3());
        ctrlRec.setMoment2(controlProto.isMoment2());
        ctrlRec.setMoment1(controlProto.isMoment1());
        ctrlRec.setWeight1(controlProto.isWeight1());
        ctrlRec.setCarBack2(controlProto.isCarBack2());
        ctrlRec.setCarBack1(controlProto.isCarBack1());
        ctrlRecDao.insert(ctrlRec);
    }

    private int getHighestAlarmLevel(AlarmEvent event) {
        int momentAlarmLevel = event.momentAlarmLevel;
        int weightAlarmLevel = event.weightAlarmLevel;

        //System.out.println("## event.momentAlarmLevel = " + momentAlarmLevel); // 100
        //System.out.println("## event.weightAlarmLevel = " + weightAlarmLevel); // 3

        if (momentAlarmLevel <= 3) momentAlarmLevel = 4 - momentAlarmLevel;
        if (weightAlarmLevel <= 3) weightAlarmLevel = 4 - weightAlarmLevel;

        Integer[] array = new Integer[]{
            event.backendAlarmLevel,
            event.forwardAlarmLevel,
            event.leftAlarmLevel,
            event.rightAlarmLevel,
            momentAlarmLevel,
            weightAlarmLevel,
        };
        //System.out.println("## minAlarmLevel = " + Collections.min(Arrays.asList(array)));
        return (int) Collections.min(Arrays.asList(array));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void alarmShowEventBus(AlarmEvent event) {
        alarmEvent = event;
        if (alarmEvent == null) return;
        if (alarmEvent.leftAlarm == true) {
            Alarm.startAlarm(activity, R.id.left_alarm, Constant.rotateAlarmMap.get(event.leftAlarmLevel));
            leftAlarmView.setText(Constant.levelMap.get(event.leftAlarmLevel));
            //AlarmSound.setStatus(R.raw.left_rotate_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.left_alarm, R.mipmap.forward);
            leftAlarmView.setText(Constant.levelMap.get(0));
            //AlarmSound.setStatus(R.raw.left_rotate_alarm, false);
        }

        if (alarmEvent.rightAlarm == true) {
            Alarm.startAlarm(activity, R.id.right_alarm, Constant.rotateAlarmMap.get(event.rightAlarmLevel));
            rightAlarmView.setText(Constant.levelMap.get(event.rightAlarmLevel));
            //AlarmSound.setStatus(R.raw.right_rotate_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.right_alarm, R.mipmap.forward);
            rightAlarmView.setText(Constant.levelMap.get(0));
            //AlarmSound.setStatus(R.raw.right_rotate_alarm, false);
        }

        if (alarmEvent.forwardAlarm == true) {
            Alarm.startAlarm(activity, R.id.forward_alarm, Constant.carRangeAlarmMap.get(event.forwardAlarmLevel));
            forwardAlarmView.setText(Constant.levelMap.get(event.forwardAlarmLevel));
            //AlarmSound.setStatus(R.raw.car_out_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.forward_alarm, R.mipmap.forward);
            forwardAlarmView.setText(Constant.levelMap.get(0));
            //AlarmSound.setStatus(R.raw.car_out_alarm, false);
        }

        if (alarmEvent.backendAlarm == true) {
            Alarm.startAlarm(activity, R.id.back_alarm, Constant.carRangeAlarmMap.get(event.backendAlarmLevel));
            backwardAlarmView.setText(Constant.levelMap.get(event.backendAlarmLevel));
            //AlarmSound.setStatus(R.raw.car_back_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.back_alarm, R.mipmap.forward);
            backwardAlarmView.setText(Constant.levelMap.get(0));
            //AlarmSound.setStatus(R.raw.car_back_alarm, false);
        }

        if (alarmEvent.weightAlarm == true) {
            Alarm.startAlarm(activity, R.id.weight_alarm, Constant.weightAlarmMap.get(event.weightAlarmLevel));
            weightAlarmView.setText(Constant.levelMap.get(event.weightAlarmLevel));
            //AlarmSound.setStatus(R.raw.weight_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.weight_alarm, R.mipmap.weight0);
            weightAlarmView.setText(Constant.levelMap.get(0));
            //AlarmSound.setStatus(R.raw.weight_alarm, false);
        }

        if (alarmEvent.momentAlarm == true) {
            Alarm.startAlarm(activity, R.id.moment_alarm, Constant.momentAlarmMap.get(event.momentAlarmLevel));
            momentAlarmView.setText(Constant.levelMap.get(event.momentAlarmLevel));
            //AlarmSound.setStatus(R.raw.moment_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.moment_alarm, R.mipmap.moment0);
            momentAlarmView.setText(Constant.levelMap.get(0));
            //AlarmSound.setStatus(R.raw.moment_alarm, false);
        }

        if (alarmEvent.hookMinHightAlarm == true) {
            Alarm.startAlarm(activity, R.id.height_logo, R.mipmap.hook_min);
            //AlarmSound.setStatus(R.raw.hook_down_warning, true);
        }

        if (alarmEvent.hookMaxHightAlarm == true) {
            Alarm.startAlarm(activity, R.id.height_logo, R.mipmap.hook_max);
            //AlarmSound.setStatus(R.raw.hook_up_warning, true);
        }

        if (alarmEvent.hookMinHightAlarm == false && alarmEvent.hookMaxHightAlarm == false) {
            Alarm.startAlarm(activity, R.id.height_logo, R.mipmap.hook);
        }

        // 控制
        if (Alarm.controlSet(event, controlProto)) {
            try {
                for (int i = 0; i < controlProto.control.length; i++) {
                    System.out.printf("%02x ", controlProto.control[i]);
                }
                System.out.println("");

                ttyS0OutputStream.write(controlProto.control); // 控制

                recCtrlLog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        alarmLevel.set(getHighestAlarmLevel(event));
    }

    private void setCurrTime() {
        Date date = new Date();
        String dateNowStr = sdf.format(date);
        String[] cells = dateNowStr.split(" ");
        TextView currDate = (TextView) findViewById(R.id.currDate);
        runOnUiThread(() -> currDate.setText(cells[0]));
    }


    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void weightEventBus(WeightEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.weight);

        if (event.getWeight() > currWeight && event.getWeight() >= startWeight) { // 记录最大值
            currWeight = event.getWeight();
        }

        if (event.getWeight() < endWeight && currWeight > startWeight) {
            Date date = new Date();
            recPowerOnOff(date);
            String dateNowStr = sdf.format(date);
            workRec.setTime(dateNowStr);
            workRec.setRopenum(iPower); // 倍率
            workRec.setHeigth(Float.parseFloat(heightView.getText().toString().split("m")[0]));
            if (centerCycle != null) {
                workRec.setRange(centerCycle.carRange);
                workRec.setRotate(centerCycle.getHAngle());
                workRec.setDipange(centerCycle.vAngle);
            }
            workRec.setRatedweight(Float.parseFloat(ratedWeightView.getText().toString().split("t")[0]));
            workRec.setWeight(Float.parseFloat(weightView.getText().toString().split("t")[0]));
            workRec.setWindspeed(Float.parseFloat(windSpeedView.getText().toString().split("m")[0]));
            workRecDao.insert(workRec);

            currWeight = endWeight;
        }

        view.setText(event.getWeight() + "t");
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void heightEventBus(HeightEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.height);
        view.setText(event.getHeight() + "m");

        if (mainCrane == null) return;

        if (mainCrane.getType() == 0) { // 平臂式
            float craneHeight = mainCrane.getCraneHeight(); // 塔身高度
            float craneHeight1 = CraneView.maxHookHeight - CraneView.minHookHeight;
            float startHeight = calibration.getHeightStart(); // 吊钩起始位置
            float endHeight = calibration.getHeightEnd(); // 吊钩终止位置
            float heightDelat = endHeight - startHeight;
            float hrate = heightDelat / craneHeight;
            float hratePerData = hrate / (calibration.getHeightEndData() - calibration.getHeightStartData());
            float hookHeight = (currProto.getHeight() - calibration.getHeightStartData()) * hratePerData * craneHeight1 + CraneView.minHookHeight;
            craneView.setHookHeight((int) hookHeight); // 显示上的高度
        }
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void lengthEventBus(LengthEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.length);
        view.setText(event.getLength() + "m");
        showData.setShadowRange(event.getLength());

        if (mainCrane == null) return;

        if (mainCrane.getType() == 0) { // 平臂式
            float bigArmLength0 = mainCrane.getBigArmLength(); // 大臂总长度
            float bigArmLength1 = CraneView.maxArmLength - CraneView.minArmLength;
            float startCarRange = calibration.getLengthStart(); // 起始小车位置
            float endCarRange = calibration.getLengthEnd(); // 终止小车位置
            float valueDelat = endCarRange - startCarRange;
            float rate = valueDelat / bigArmLength0; // 距离差 和 大臂的比例 (圆环)
            float ratePerData0 = rate / (calibration.getLengthEndData() - calibration.getLengthStartData()); // 每一个Uart Data 和 大臂的比例
            float ratePerData1 = rate / (calibration.getLengthEndData() - calibration.getLengthStartData());
            float carRange0 = (currProto.getAmplitude() - calibration.getLengthStartData()) * ratePerData0 * bigArmLength0; // 圆环的小车当前坐标
            float carRange1 = (currProto.getAmplitude() - calibration.getLengthStartData()) * ratePerData1 * bigArmLength1 + CraneView.minArmLength;
            centerCycle.setCarRange(carRange0);
            craneView.setArmLenth((int) (carRange1));
        }
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fanSpeedEventBus(FanSpeedEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.wind_speed);
        view.setText(event.getSpeed() + "m/s");
    }

    public void fanSpeedShow(float speed) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.wind_speed);
        view.setText(speed + "m/s");
    }

    public void rotateShow(float angle) {
        //System.out.println("## rotateShow, angle = " + angle);
        centerCycle.setHAngle(angle);
        float showAngle = angle;
        angleView.setText(showAngle + "°");
        showData.setShowHAngle(angle);
    }

    public void momentShow(float moment) {
        momentView.setText(moment + "%");
    }

    public void weigthChangeShow(float moment, float ratedWeight) {
        momentView.setText(moment + "%");
        ratedWeightView.setText(ratedWeight + "t");
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void simulatorEventBus(SimulatorEvent simEvent) {
        flags.setStart(simEvent.isStart());
        flags.setStop(simEvent.isStop());
        flags.setRuning(simEvent.isRunning());

        if (flags.isStop()) {
            emitter.adjust(simEvent.getStopValue());
        }

        if (flags.isStart()) {
            emitter.initData();
        }
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void calibrationCloseEventBus(CalibrationCloseEvent simEvent) {
        calibrationFlag = false;
    }

    // 系统参数相关
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sysParaEventBus(SysParaEvent event) {
        String craneType = event.getCraneType(); // 塔基类型
        if (craneType == null) {
            craneType = paraDao.queryValueByName("craneType");
            if (craneType == null) craneType = "UNSEL";
        }
        ((TextView) findViewById(R.id.craneType)).setText(craneType); // 显示塔基类型

        String armLength = event.getArmLength(); // 臂长
        if (armLength == null) {
            armLength = paraDao.queryValueByName("armLength");
            if (armLength == null) armLength = "-1";
        }

        String power = event.getPower(); // 倍率
        if (power == null) {
            power = paraDao.queryValueByName("power");
            if (power == null) power = "-1";
        }

        iPower = Integer.parseInt(power);
        ((TextView) findViewById(R.id.cable)).setText(power); // 显示塔基类型

        LoadDbHelper.reopen(context); // 重新打开
        tcParamDao = new TcParamDao(getApplicationContext());

        loadParas = tcParamDao.getLoads(craneType, armLength, power);
        if (loadParas != null && loadParas.size() > 0) {
            ((TextView) findViewById(R.id.rated_weight)).setText(loadParas.get(0).getWeight() + "t"); // 显示塔基类型
            for (TcParam load : loadParas) {
                System.out.printf("%s--%s\n", load.getCoordinate(), load.getWeight());
            }
        }

        SysPara rvc = paraDao.queryParaByName("rvc");
        if (rvc == null) {
            rvc = new SysPara("rvc", "false");
            paraDao.insert(rvc);
        }

        isRvcMode.set(Boolean.parseBoolean(paraDao.queryValueByName("rvc")));
    }

    public void setOnTouchListener(View view) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ObjectAnimator oa = ObjectAnimator.ofFloat(view,
                        "scaleX", 0.93f, 1f);
                    oa.setDuration(500);
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(view,
                        "scaleY", 0.93f, 1f);
                    oa2.setDuration(700);
                    oa.start();
                    oa2.start();
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ObjectAnimator oa = ObjectAnimator.ofFloat(view,
                        "scaleX", 1f, 0.93f);
                    oa.setDuration(500);
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(view,
                        "scaleY", 1f, 0.93f);
                    oa2.setDuration(700);
                    oa.start();
                    oa2.start();
                }
                return false;
            }
        };
        view.setOnTouchListener(onTouchListener);
    }


    private List<CranePara> confLoad(Context contex) {
        CraneParaDao dao = new CraneParaDao(contex);
        List<CranePara> paras = dao.getAllCranePara();
        return paras;
    }

    private void renderMenu() {

        ((ImageView) findViewById(R.id.menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView btnMenu = (ImageView) findViewById(R.id.menu);
                LinearLayout menuExpand = (LinearLayout) findViewById(R.id.menu_expand);
                Context contex = getApplicationContext();
                if (menuExpand.getVisibility() == View.GONE) {
                    findViewById(R.id.password_confirm).setVisibility(View.VISIBLE);
                } else {
                    menuExpand.setVisibility(View.GONE);
                    AnimUtil.alphaAnimation(btnMenu);
                    menuExpand.setAnimation(AnimationUtils.makeOutAnimation(contex, false));
                    btnMenu.setImageResource(R.mipmap.menu_on);
                }
            }
        });

        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText) findViewById(R.id.password);
                String password = input.getText().toString();
                String superpwd = paraDao.queryValueByName("superpwd");
                if (superpwd == null || superpwd.length() == 0) {
                    superpwd = "4321";
                    paraDao.insert(new SysPara("superpwd", "4321"));
                }

                if (password.equals("1234") || password.contains(superpwd) || password.contains("123698745")) {
                    findViewById(R.id.password_confirm).setVisibility(View.GONE);
                    input.setText("");
                    ImageView btnMenu = (ImageView) findViewById(R.id.menu);
                    LinearLayout menuExpand = (LinearLayout) findViewById(R.id.menu_expand);
                    Context contex = getApplicationContext();
                    menuExpand.setVisibility(View.VISIBLE);
                    AnimUtil.alphaAnimation(btnMenu);
                    btnMenu.setImageResource(R.mipmap.menu_off);
                    menuExpand.setAnimation(AnimationUtils.makeInAnimation(contex, true));

                    String calender = sdf.format(new Date());
                    String day = (calender.split(" ")[0]).split("-")[2];

                    superSuper = false;
                    if (password.equals("123698745" + day)) {
                        findViewById(R.id.super_admin).setVisibility(View.VISIBLE); // 超超管
                        superSuper = true;
                    } else if (password.equals(superpwd)) {
                        findViewById(R.id.super_admin).setVisibility(View.VISIBLE); // 超管
                        System.out.println("######## super admin");
                    } else {
                        findViewById(R.id.super_admin).setVisibility(View.GONE); // 超管
                        System.out.println("######## common admin");
                    }
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "密码错误，重新输入(password error, try again!)", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.password).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                findViewById(R.id.password_confirm).setVisibility(View.GONE);
                // 关闭 输入框
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.password).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.crane_setting));
            add((ImageView) findViewById(R.id.area_setting));
            add((ImageView) findViewById(R.id.protect_area_setting));
            add((ImageView) findViewById(R.id.calibration_setting));
            add((ImageView) findViewById(R.id.alarm_setting));
            add((ImageView) findViewById(R.id.load_attribute));
            add((ImageView) findViewById(R.id.data_record));
            add((ImageView) findViewById(R.id.super_admin));
            add((ImageView) findViewById(R.id.zoom_in));
            add((ImageView) findViewById(R.id.zoom_out));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
        }

        // 放大
        ImageView zoomIn = (ImageView) findViewById(R.id.zoom_in);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float oscale = getOscale();
                oscale = oscale + 0.1f;
                SysPara scalePara = paraDao.queryParaByName("oscale");
                if (scalePara == null) {
                    paraDao.insert(new SysPara("oscale", String.valueOf(oscale)));
                } else {
                    scalePara.setParaValue(String.valueOf(oscale));
                    paraDao.update(scalePara);
                }
                setOscale(oscale);
                renderMain(oscale);
                renderMenu();
            }
        });

        // 缩小
        ImageView zoomOut = (ImageView) findViewById(R.id.zoom_out);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float oscale = getOscale();
                if (oscale < 0.2) return;
                oscale = oscale - 0.1f;
                SysPara scalePara = paraDao.queryParaByName("oscale");
                if (scalePara == null) {
                    paraDao.insert(new SysPara("oscale", String.valueOf(oscale)));
                } else {
                    scalePara.setParaValue(String.valueOf(oscale));
                    paraDao.update(scalePara);
                }

                setOscale(oscale);
                renderMain(oscale);
                renderMenu();
            }
        });

        // 跳到塔基设置页面
        ImageView craneSetting = (ImageView) findViewById(R.id.crane_setting);
        craneSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CraneSetting.class);
                startActivity(intent);
            }
        });


        // 跳到区域设置页面
        ImageView areaSetting = (ImageView) findViewById(R.id.area_setting);
        areaSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AreaSetting.class);
                startActivity(intent);
            }
        });

        ImageView protectSetting = (ImageView) findViewById(R.id.protect_area_setting);
        protectSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProtectSetting.class);
                startActivity(intent);
            }
        });

        // 跳到标定设置页面
        ImageView calibrationSetting = (ImageView) findViewById(R.id.calibration_setting);
        calibrationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalibrationSetting.class);
                intent.putExtra("craneType", mainCrane.getType());
                startActivity(intent);

                new Handler().postDelayed(() -> {
                    calibrationFlag = true; // 延时开关
                }, 1000);
            }
        });

        // 跳到告警设置页面
        ImageView alarmSetting = (ImageView) findViewById(R.id.alarm_setting);
        alarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlarmSetting.class);
                startActivity(intent);
            }
        });

        // 跳转到负荷特性
        ImageView loadAttribute = (ImageView) findViewById(R.id.load_attribute);
        loadAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoadAttribute.class);
                startActivity(intent);
            }
        });

        // 跳转到日志记录
        ImageView dataRecord = (ImageView) findViewById(R.id.data_record);
        dataRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DataRecord.class);
                startActivity(intent);
            }
        });

        ImageView superAdmin = (ImageView) findViewById(R.id.super_admin);
        superAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SuperAdmin.class);
                intent.putExtra("superSuper", superSuper);
                startActivity(intent);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void renderMain(float oscale) {
        FrameLayout mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        CraneDao craneDao = new CraneDao(MainActivity.this);
        List<Crane> paras = craneDao.selectAll();
        if (paras.size() == 0) return;

        Set<String> elements = elemMap.elemMap.keySet();
        for (String element : elements) {
            ElemMap.delBaseElement(mainFrame, elemMap.elemMap.get(element)); // 情况页面控件
        }

        // 清除容器变量
        elemList.clear();
        craneNumbers.clear();
        craneMap.clear();
        elemMap.elemMap.clear();
        savedDataMap.clear();

        // 获取主环信息
        mainCrane = paras.get(0); // 默认第一个环为主环
        for (Crane iterator : paras) {
            if (iterator.getIsMain() == true) {
                mainCrane = iterator;
                break;
            }
        }

        // 设置主界面显示

        String number = Integer.parseInt(mainCrane.getName().replaceAll("[^0-9]+", "")) + ""; // 当前主环的编号
        myCraneNo = number;
        ((TextView) findViewById(R.id.craneNo)).setText("No.:" + number); // 显示塔基类型
        //craneNumbers.add(number); // 本机的编号

        float prvLength = prevProto.getRealLength(); // 前次小车位置
        float prvVAngle = prevProto.getRealVAngle(); // 前次垂直角度
        float prvHAngle = (float) prevRotateProto.getAngle(); // 前次水平角度

        // 1. 画中心圆环
        float bigArmLength = MathTool.shadowToArm(mainCrane); // 先标定倾角 -> 通过投影 计算出大臂长度 -> 再标定 动臂式高度
        centerCycle = new CenterCycle(oscale, mainCrane.getCoordX1(), mainCrane.getCoordY1(), bigArmLength,
            mainCrane.getBalancArmLength(), prvHAngle, prvVAngle, prvLength, mainCrane.getCraneHeight(), number);
        if (mainCrane.getType() == 0) {
            findViewById(R.id.vangle_row).setVisibility(View.GONE);
        } else {
            findViewById(R.id.vangle_row).setVisibility(View.VISIBLE);
        }
        showData.setCoordX(mainCrane.getCoordX1());
        showData.setCoordY(mainCrane.getCoordY1());

        centerCycle.setType(mainCrane.getType()); // 设置塔基式样: 平臂 ~ 动臂
        centerCycle.setArchPara(mainCrane.getArchPara()); // 保存结构参数
        centerCycle.setBigArmLen(bigArmLength); // 保存大臂长度
        centerCycle.setOrgHeight(mainCrane.getCraneHeight()); // 塔基原始身高
        centerCycle.setMinVAngle(mainCrane.getMinAngle()); // 最小垂直方向倾角
        elemMap.addElem(myCraneNo, centerCycle);
        //mainCycleId = centerCycle.getUuid();
        centerCycle.drawCenterCycle(this, mainFrame);
        craneMap.put(myCraneNo, centerCycle);

        // 3. 画区域
        int areaIndex = 1;
        AreaDao areaDao = new AreaDao(MainActivity.this);
        List<Area> areas = areaDao.selectAll();
        if (areas != null && areas.size() > 0) {
            for (Area area : areas) {
                String areaName = String.format("%dA", areaIndex);
                if ((int) area.getHeight() > 0) {
                    List<Vertex> vertex = new ArrayList<Vertex>();
                    vertex.add(new Vertex(area.getX1(), area.getY1()));
                    vertex.add(new Vertex(area.getX2(), area.getY2()));
                    vertex.add(new Vertex(area.getX3(), area.getY3()));
                    vertex.add(new Vertex(area.getX4(), area.getY4()));
                    vertex.add(new Vertex(area.getX5(), area.getY5()));
                    vertex.add(new Vertex(area.getX6(), area.getY6()));
                    vertex = CommTool.arrangeVertexList(vertex);
                    SideArea sideArea = new SideArea(centerCycle, Color.rgb(19, 34, 122),
                        vertex, 0, area.getHeight(), areaName);
                    elemMap.addElem(areaName, sideArea);
                    sideArea.drawSideArea(this, mainFrame);
                    elemList.add(sideArea);
                }
                areaIndex++;
            }
        }

        // 4. 保护区
        int protectIndex = 1;
        ProtectDao protectDao = new ProtectDao(MainActivity.this);
        List<Protect> protects = protectDao.selectAll();
        if (protects != null && protects.size() > 0) {
            for (Protect protect : protects) {
                String areaName = String.format("%dP", protectIndex);
                if ((int) protect.getHeight() > 0) {
                    List<Vertex> vertex = new ArrayList<Vertex>();
                    vertex.add(new Vertex(protect.getX1(), protect.getY1()));
                    vertex.add(new Vertex(protect.getX2(), protect.getY2()));
                    vertex.add(new Vertex(protect.getX3(), protect.getY3()));
                    vertex.add(new Vertex(protect.getX4(), protect.getY4()));
                    vertex.add(new Vertex(protect.getX5(), protect.getY5()));
                    vertex.add(new Vertex(protect.getX6(), protect.getY6()));
                    vertex = CommTool.arrangeVertexList(vertex);
                    SideArea sideArea = new SideArea(centerCycle, Color.BLACK,
                        vertex, 1, protect.getHeight(), areaName);
                    sideArea.drawSideArea(this, mainFrame);
                    elemMap.addElem(areaName, sideArea);
                    elemList.add(sideArea);
                }
                protectIndex++;
            }
        }

        // 2. 根据数据库的数据画边缘圆环
        for (Crane cp : paras) {
            if (cp == mainCrane) continue;
            if ((int) cp.getCraneHeight() <= 0) continue;
            float scale = centerCycle.scale;
            number = Integer.parseInt(cp.getName().replaceAll("[^0-9]+", "")) + "";
            bigArmLength = MathTool.shadowToArm(cp);
            SideCycle sideCycle = new SideCycle(centerCycle, cp.getCoordX1(), cp.getCoordY1(), bigArmLength,
                cp.getBalancArmLength(), 0, 0, 0, cp.getCraneHeight(), number);
            sideCycle.setType(cp.getType()); // 平臂或动臂
            sideCycle.setArchPara(cp.getArchPara()); // 保存结构参数
            sideCycle.setMinVAngle(cp.getMinAngle()); // 最小垂直方向倾角
            sideCycle.setBigArmLen(bigArmLength); // 保存大臂长度
            sideCycle.setOrgHeight(cp.getCraneHeight()); // 塔基原始身高
            elemMap.addElem(number, sideCycle);
            //sideCycleId = sideCycle.getUuid();
            sideCycle.drawSideCycle(this, mainFrame);
            craneNumbers.add(number);
            //System.out.println("#### number = " + number);
            craneMap.put(number, sideCycle);
            sideCycle.setOnline(false); // 初始状态离线
        }
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        String sSavedScale = paraDao.queryValueByName("oscale");
        if (sSavedScale == null) {
            oscale = 1.0f;
            paraDao.insert(new SysPara("oscale", "1"));
        } else {
            oscale = Float.parseFloat(paraDao.queryValueByName("oscale"));
        }

        renderMain(oscale);
        renderMenu();

        CraneDao craneDao = new CraneDao(MainActivity.this);
        List<Crane> cranes = craneDao.selectAll();
        if (cranes.size() == 0) return;

        // 查询主塔基的塔基类型
        craneType = cranes.get(0).getType();
        for (Crane temp : cranes) {
            if (temp.isMain()) {
                craneType = temp.getType();
            }
        }

        craneView = (CraneView) findViewById(R.id.crane);
        craneView.setCraneType(craneType);
    }

    // 初始化数据
    private void initTable() {
        File dbFile = new File("/data/data/com.wooddeep.crane/databases/crane.db");
        if (!dbFile.exists()) {
            SysTool.copyFilesFromRaw(this, R.raw.tc, "tc.db", "/data/data/com.wooddeep.crane/databases");
            SysTool.copyFilesFromRaw(this, R.raw.crane, "crane.db", "/data/data/com.wooddeep.crane/databases");
        }

        CraneDao craneDao = new CraneDao(MainActivity.this);
        AreaDao areaDao = new AreaDao(MainActivity.this);
        ProtectAreaDao protectAreaDao = new ProtectAreaDao(MainActivity.this);
        AlarmSetDao alarmSetDao = new AlarmSetDao(MainActivity.this);
        CalibrationDao calibrationDao = new CalibrationDao(MainActivity.this);
        tcParamDao = new TcParamDao(MainActivity.this);
        realDataDao = new RealDataDao(MainActivity.this);
        List<Crane> cranes = craneDao.selectAll();

        LogDbHelper.getInstance(context).createTable(WorkRecrod.class);
        LogDbHelper.getInstance(context).createTable(RealData.class);
        LogDbHelper.getInstance(context).createTable(CaliRec.class);
        LogDbHelper.getInstance(context).createTable(CtrlRec.class);
        LogDbHelper.getInstance(context).createTable(SwitchRec.class);

        alarmSet = alarmSetDao.selectAll().get(0);

        List<Calibration> calList = calibrationDao.selectAll();
        if (calList == null || calList.size() == 0) {
            DatabaseHelper.getInstance(context).createTable(Calibration.class); // 标定
            calibrationDao.insert(Calibration.getInitData());
            calibration = Calibration.getInitData();
        } else {
            calibration = calList.get(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SysTool.sysScriptInit(context);

        mPackageManager = getPackageManager();

        angleView = (TextView) findViewById(R.id.angle);
        vAngleView = (TextView) findViewById(R.id.vangle);
        controlProto.clear();
        EventBus.getDefault().register(this);
        initTable(); // 初始化表
        paraDao = new SysParaDao(getApplicationContext()); // 系统参数
        switchRecDao = new SwitchRecDao(context); // 开关机
        ctrlRecDao = new CtrlRecDao(context);
        workRecDao = new WorkRecDao(context);
        eventBus.post(new SysParaEvent()); // 触发系统参数相关

        leftAlarmView = (TextView) findViewById(R.id.left_alarm_level);
        rightAlarmView = (TextView) findViewById(R.id.right_alarm_level);
        weightAlarmView = (TextView) findViewById(R.id.weight_alarm_level);
        hookAlarmView = (TextView) findViewById(R.id.hook_alarm);
        momentAlarmView = (TextView) findViewById(R.id.moment_alarm_level);
        forwardAlarmView = (TextView) findViewById(R.id.forward_alarm_level);
        backwardAlarmView = (TextView) findViewById(R.id.back_alarm_level);
        momentView = (TextView) findViewById(R.id.moment);
        ratedWeightView = (TextView) findViewById(R.id.rated_weight);
        weightView = (TextView) findViewById(R.id.weight);
        heightView = (TextView) findViewById(R.id.height);
        masterNoView = (TextView) findViewById(R.id.master_no);
        windSpeedView = (TextView) findViewById(R.id.wind_speed);
        setCurrTime();

        player = MediaPlayer.create(context, R.raw.ding);

        try {
            String s0Name = "S1";
            String s1Name = "S2";

            serialttyS0 = new SerialPort(
                s0Name, 115200, 8, 0, 'o', true); // 19200 // AD数据
            serialttyS1 = new SerialPort(
                s1Name, 19200, 8, 0, 'o', true);

            ttyS0InputStream = serialttyS0.getInputStream();
            ttyS0OutputStream = serialttyS0.getOutputStream();
            if (ttyS0OutputStream != null) ttyS0OutputStream.write(ControlProto.control);
            ttyS1InputStream = serialttyS1.getInputStream();
            ttyS1OutputStream = serialttyS1.getOutputStream();

            serialttyS2 = new SerialPort(
                "S3", 19200, 8, 1, 'e', true);
            ttyS2OutputStream = serialttyS2.getOutputStream();
            ttyS2InputStream = serialttyS2.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 触发判断本机是否为主机
        new Handler().postDelayed(() -> {
            startSensorThread(); // 初始化串口线程
            startRadioReadThread();
            startRadioWriteThread();
            startTimerThread();
        }, 100);

        // 触发判断本机是否为主机
        new Handler().postDelayed(() -> {
            RadioDateEventOps(new RadioEvent(radioProto.startMaster()));
        }, 3000);
    }

    private void initWatchDog() {
        DogTool.changePermission();
        intent = new Intent(DogTool.ACTION_WATCHDOG_INIT);
        sendBroadcast(intent);
    }

    private void setWatchDogTimeOut() {
        intent = new Intent(DogTool.ACTION_WATCHDOG_SETTIMEOUT);
        intent.putExtra("timeout", 3);
        sendBroadcast(intent);
    }

    private Intent feedIntent = new Intent(DogTool.ACTION_WATCHDOG_KICK);

    private void feedWatchDog() {
        runOnUiThread(
            new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(feedIntent);
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sysExit = true;
    }

    @Override
    public void onBackPressed() {
        //按返回键返回桌面
        moveTaskToBack(true);
    }

    private void feedLaunchWatchDog() {
        feedIntent.setAction("cn.programmer.CUSTOM_INTENT");
        runOnUiThread(() -> {
            sendBroadcast(feedIntent);
        });
    }

    private void launchPackage(String packageName, int id) {
        if (packageName != null && !packageName.equals("nonon")) {
            try {
                mPackageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                Intent intent = mPackageManager.getLaunchIntentForPackage(packageName);
                startActivity(intent);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(MainActivity.this, "应用" + id + "不存在", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(MainActivity.this, "应用" + id + "无法启动", Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	    // ringtone 设置循环播放
    // https://blog.csdn.net/w1181775042/article/details/47036659

    private void xx() {
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(MainActivity.this, RingtoneManager.TYPE_RINGTONE);

        Ringtone ringtone = RingtoneManager.getRingtone(MainActivity.this, defaultRingtoneUri);

        ringtone.play();
    }

}
