package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.x6.serialportlib.SerialPort;
import com.wooddeep.crane.alarm.Alarm;
import com.wooddeep.crane.alarm.AlarmSound;
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
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.AlarmSetDao;
import com.wooddeep.crane.persist.dao.AreaDao;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.CraneParaDao;
import com.wooddeep.crane.persist.dao.LoadDao;
import com.wooddeep.crane.persist.dao.ProtectAreaDao;
import com.wooddeep.crane.persist.dao.ProtectDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.CranePara;
import com.wooddeep.crane.persist.entity.Load;
import com.wooddeep.crane.persist.entity.Protect;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.simulator.SimulatorFlags;
import com.wooddeep.crane.simulator.UartEmitter;
import com.wooddeep.crane.tookit.AnimUtil;
import com.wooddeep.crane.tookit.CommTool;
import com.wooddeep.crane.tookit.MathTool;
import com.wooddeep.crane.tookit.MomentOut;
import com.wooddeep.crane.tookit.StringTool;
import com.wooddeep.crane.views.CraneView;
import com.wooddeep.crane.views.Vertex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

//import androidx.annotation.RequiresApi;


// 启动mumu之后, 输入：
// adb connect 127.0.0.1:7555
// 然后再调试, 就ok了


// TODO list 20190908
// 1. 不在线，幅度为0，角度为0, 不参与碰撞检测 （ok）
// 2. 告警铃声 更新
// 3. 动臂式, 修改 塔基的 高度 和 大臂长度。（ok）

// 4. 右回转，灯没有亮 ~ 控制问题 ！！！ (ok)

// 6. 绘主界面时，特别是主环的 幅度 和 回转，要用当前的实际值 (ok)

// 5. 小车出入的 告警控制 (a. 小车出入达到最大/最小值(ok), b. 力矩达到最大值(ok), c. 回转小车出入到警戒值(ok))

// 9. 吊钩高度告警(ok)

// 7. 力矩没有变化? 幅度为0，也需要计算力矩
// 8. 数字更新

// 10. 坐标标定

//////////////////////////////

// 11. 动臂式, 倾角变化，导致力矩的变化

// 13. calcRealLength 根据 vangle计算

// 14. 设置之后， 通信数据不更新

// 15. 通信从机, 动臂式，投影（非大臂）

// TODO list 20190908
// 1. 动臂式吊钩高度变化
// 2. 吊钩 高度 0 是地面
// 3. 塔身 作为 固定障碍物

// TODO list 20190916
// 1. 无数据的情况下，需要周期刷新，每隔10s
// 1. 主机接收到 其他主机的查询命令，收到目标是自己的编号的查询，不管
// 3. 修改主机编号
// 4. 主从切换, 逻辑上再验证
// 5. 默认值修改成0
// 6. 中英文对照
// 7. 数字键盘
// 8. 告警数字变小 50 -> 25
// 9.

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TAG = "MainActivity";
    private android.app.Activity activity = this;
    private Context context;

    private HashMap<String, SavedData> savedDataMap = new HashMap<>();
    private HashMap<String, CycleElem> craneMap = new HashMap<>();
    private HashMap<String, SavedData> slaveMap = new HashMap<>();
    private HashMap<String, Long> radioStatusMap = new HashMap();
    private List<String> craneNumbers = new ArrayList<>();
    private List<BaseElem> elemList = new ArrayList<>();
    private List<Load> loadParas = new ArrayList<>();
    private ElemMap elemMap = new ElemMap();

    private int currSlaveIndex = 0; // 当前和本主机通信的从机名称
    private float shadowLength = 0;
    private String myCraneNo = "";
    private float oscale = 1.0f;
    private int craneType = 0; // 塔基类型: 0 ~ 平臂式, 2动臂式

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
    private boolean calibrationFlag = false;
    private boolean isMasterCrane = false; // 是否主塔机
    private boolean waitFlag = true; // 等待主机信号标识

    private ControlProto controlProto = new ControlProto();
    private RadioProto slaveRadioProto = new RadioProto();  // 本机作为从机时，需要radio通信的对象
    private RadioProto masterRadioProto = new RadioProto(); // 本机作为主机时，需要radio通信的对象

    private SerialPort serialttyS0;
    private SerialPort serialttyS1;
    private SerialPort serialttyS2;
    private InputStream ttyS0InputStream;
    private OutputStream ttyS0OutputStream;
    private InputStream ttyS1InputStream;
    private OutputStream ttyS1OutputStream;
    private OutputStream ttyS2OutputStream;
    private InputStream ttyS2InputStream;

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
    private LoadDao loadDao; // 负荷特性

    private CraneView craneView;
    private TextView angleView;
    private TextView vAngleView;
    private TextView leftAlarmView;
    private TextView rightAlarmView;
    private TextView forwardAlarmView;
    private TextView backwardAlarmView;
    private TextView weightAlarmView;
    private TextView hookAlarmVeiw;
    private TextView momentAlarmView;
    private TextView momentView;
    private TextView ratedWeightView;
    private TextView masterNoView;
    private MediaPlayer player;

    public float getOscale() {
        return oscale;
    }

    public void setOscale(float oscale) {
        this.oscale = oscale;
    }

    private void startDataSimThread() {
        new Thread(() -> {
            int alarmTimes = 0;

            while (true) {
                /*
                if (flags.isStart() || flags.isStop()) {
                    currProto.setAmplitude(emitter.getsAmplitude());
                    currProto.setHeight(emitter.getsHeight());
                    currProto.setWeight(emitter.getsWeight());
                    currProto.setWindSpeed(emitter.getsWindSpeed());
                    currRotateProto.setData(emitter.getsAmplitude()); // TODO 单独做回转数据的发射器, 现在借用幅度值的发射器
                }

                if (flags.isRuning()) {
                    currProto.setAmplitude(emitter.getsAmplitude());
                    currProto.setHeight(emitter.getsHeight());
                    currProto.setWeight(emitter.getsWeight());
                    currProto.setWindSpeed(emitter.getsWindSpeed());
                    currRotateProto.setData(emitter.getsAmplitude()); // TODO 单独做回转数据的发射器, 现在借用幅度值的发射器

                    if (alarmTimes % 1 == 0) { // 100毫秒 累计一次
                        emitter.emitter(); // 累计
                    }
                }

                byte[] data = currProto.pack(); // TODO 判断值是否有变化, 无变化，则不发送消息
                if (calibrationFlag) {
                    eventBus.post(new UartEvent(data)); // 发送通知标定模块数据
                }

                currProto.parse(data);
                currProto.calcRealHeight(calibration);
                currProto.calcRealLength(calibration);
                currProto.calcRealWeigth(calibration);
                if (Math.abs(currProto.getRealLength() - prevProto.getRealLength()) >= 0.05f) {
                    //lengthEvent.setLength(currProto.getRealLength());
                    //eventBus.post(lengthEvent);
                    prevProto.setRealLength(currProto.getRealLength());
                    runOnUiThread(() -> lengthShow(currProto.getRealLength()));
                    //alarmJdugeFlag = true;
                }

                if (Math.abs(currProto.getRealWeight() - prevProto.getRealWeight()) >= 0.05f) {
                    //lengthEvent.setLength(currProto.getRealLength());
                    //eventBus.post(lengthEvent);
                    prevProto.setRealWeight(currProto.getRealWeight());
                    runOnUiThread(() -> weightShow(currProto.getRealWeight()));
                    //alarmJdugeFlag = true;
                }

                if (Math.abs(currProto.getRealHeight() - prevProto.getRealHeight()) >= 0.05f) {
                    //lengthEvent.setLength(currProto.getRealLength());
                    //eventBus.post(lengthEvent);
                    prevProto.setRealHeight(currProto.getRealHeight());
                    runOnUiThread(() -> heightShow(currProto.getRealHeight()));
                    //alarmJdugeFlag = true;
                }
                */

                if (mainCrane != null) {  // 次环
                    // 模拟回转

                    /*
                    data = currRotateProto.pack();
                    rotateEvent.setData(data);
                    currRotateProto.parse(data);
                    currRotateProto.calcAngle(calibration);
                    if (Math.abs(currRotateProto.getAngle() - prevRotateProto.getAngle()) >= 0.2f) {
                        rotateEvent.setCenterX(mainCrane.getCoordX1());
                        rotateEvent.setCenterY(mainCrane.getCoordY1());
                        rotateEvent.setAngle((float) currRotateProto.getAngle());
                        //eventBus.post(rotateEvent);
                        runOnUiThread(() -> rotateShow((float) currRotateProto.getAngle()));
                        prevRotateProto.setAngle(currRotateProto.getAngle());
                        //alarmJdugeFlag = true;
                    }
                    */

                    // TODO
                    radioProto.setSourceNo(2); // 从机的ID为2N
                    radioProto.setTargetNo(0); // 从机回应
                    radioProto.setRotate(Math.round(Math.toRadians(alarmTimes % 360) * 10) / 10.0f);
                    radioProto.setRange(30.0f);

                    //System.out.println(Math.round(emitter.getsAmplitude() * 10) / 10.00f);

                    // 模拟电台
                    radioEvent.setData(radioProto.packReply());
                    eventBus.post(radioEvent);

                }

                /*
                if (alarmTimes % 9 == 0) { // TODO 数据有变化才触发告警判断
                    //eventBus.post(alarmDetectEvent); // 消息触发有延时
                    System.out.println("#### I will do alarm judge!!!");
                    try {

                        Alarm.alarmDetect(calibration, currProto.getRealHeight(), currProto.getRealLength(),
                            elemList, craneMap, myCraneNo, alarmSet, eventBus);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                */

                alarmTimes++;
                CommTool.sleep(500);
            }
        }).start();
    }


    private void startSensorThread() {
        new Thread(() -> {
            try {
                int counter = 0;
                while (true) {

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
                                    centerCycle.setHeight(centerCycle.getOrgHeight() + (float) deltaHeight); // 修改高度

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
                                runOnUiThread(() -> momentShow(moment.moment));
                            } else {
                                MomentOut moment = MathTool.momentCalc(loadParas, currProto.getRealWeight(), currProto.getRealLength());
                                runOnUiThread(() -> momentShow(moment.moment));
                            }
                        }

                        if (Math.abs(currProto.getWindSpeed() - prevProto.getWindSpeed()) >= 218) {
                            prevProto.setWindSpeed(currProto.getWindSpeed());
                            runOnUiThread(() -> {
                                float windSpeed = currProto.getWindSpeed() * 30 / 65536;
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startRadioReadThread() {

        new Thread(() -> {
            try {
                int index = 0;

                while (true) {

                    /*
                    if (ttyS1InputStream.available() > 0) {
                        int len = ttyS1InputStream.read(radioXBuff, 0, 1024);
                        for (int i = 0; i < len; i++) {
                            radioYBuff[i + index] = radioXBuff[i]; // 把XBuffer的数据暂存到YBuffer
                        }
                        index = index + len;

                        //System.out.println(new String(radioYBuff));

                        int start = 0;
                        int end = 0;

                        for (int i = 0; i < 128; i++) {
                            if (radioYBuff[i] == '%') start = i;
                        }

                        for (int i = start; i < 128; i++) {
                            if (radioYBuff[i] == '#') end = i;
                        }

                        if (index > 128) {
                            index = 0;
                            System.out.println("@@@@@@@@@@@@1");
                            continue;
                        }

                        if (end > start) { // TODO: 如果收到的数据1024中，都没有 % #， 则退出
                            index = 0;
                            for (int i = start; i <= start + 39; i++ ) {
                                radioRBuff[i - start] = radioYBuff[i];
                            }
                            System.out.println(new String(radioRBuff));
                            radioEvent.setData(radioRBuff);

                            try {
                                RadioDateEventOps(radioEvent);
                            } catch (Exception e) {
                                index = 0;
                                System.out.println("@@@@@@@@@@@@2");
                                continue;
                            }
                        }
                    }
                    */

                    int start = 0;
                    while (true) {
                        if (ttyS1InputStream.available() > 0) {
                            int len = ttyS1InputStream.read(radioXBuff, 0, 1024);

                            //System.out.println("## length = " + len);
                            for (int i = 0; i < Math.min(len, 39); i++) {
                                radioRBuff[Math.min(i + start, 39)] = radioXBuff[i]; // 把XBuffer的数据暂存到YBuffer
                            }
                            start = start + len;
                            //System.out.println("## start = " + start);
                            for (int i = 0; i < 0; i++) {

                            }

                            if (start >= 39) {
                                System.out.println(new String(radioRBuff));
                                radioEvent.setData(radioRBuff);
                                try {
                                    RadioDateEventOps(radioEvent);
                                } catch (Exception e) {
                                    break;
                                }
                                break;
                            }
                        }
                    }


                    CommTool.sleep(20);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startRadioWriteThread() {
        new Thread(() -> {
            try {
                while (true) {
                    System.out.println("### " + iAmMaster.get() + " ### " + craneNumbers.size());
                    if (iAmMaster.get() && craneNumbers.size() > 1) { // 主机
                        //System.out.println("##### I am master!!!");
                        int iMyCraneNo = Integer.parseInt(myCraneNo);
                        currSlaveIndex = (currSlaveIndex + 1) % craneNumbers.size();
                        int targetNo = Integer.parseInt(craneNumbers.get(currSlaveIndex));
                        if (iMyCraneNo == targetNo) continue;
                        masterRadioProto.setSourceNo(iMyCraneNo);
                        masterRadioProto.setTargetNo(targetNo);
                        masterRadioProto.setPermitNo(targetNo);

                        double currAngle = Math.max(Math.toRadians(centerCycle.hAngle), 0);
                        double currRange = Math.max(shadowLength, 0);

                        masterRadioProto.setRotate(Math.round(currAngle * 10) / 10.0f);
                        masterRadioProto.setRange(Math.round(currRange * 10) / 10.0f);

                        //System.out.printf("### master write data: %f -- %f \n", currAngle, currRange); // TODO 幅度值，角度值不对
                        masterRadioProto.packReply(); // 生成回应报文
                        //StringTool.showCharArray1(masterRadioProto.modleChars);

                        try {
                            //ttyS1OutputStream.write(masterRadioProto.modleBytes); // 发送应答报文
                            ttyS1OutputStream.write(masterRadioProto.modleBytes, 0, 39);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    CommTool.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startTimerThread() {
        AlarmSound.init(getApplicationContext());
        //AlarmSound.setStatus(R.raw.left_rotate_alarm, true);
        //AlarmSound.start(0);

        new Thread(() -> {
            try {
                while (true) {
                    try {
                        //System.out.println("--2--" + shadowLength);
                        Alarm.alarmDetect(calibration, currProto.getRealHookHeight(), shadowLength,
                            elemList, craneMap, myCraneNo, alarmSet, eventBus); // 回转告警判断
                        Alarm.weightAlarmDetect(calibration, loadParas, alarmSet, eventBus,
                            currProto.getRealWeight(), shadowLength); // 吊重告警判断

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    CommTool.sleep(600);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 侦听电台数据
    //@Subscribe(threadMode = ThreadMode.MAIN)
    public void RadioDateEventOps(RadioEvent event) {
        int cmdRet = radioProto.parse(event.getData()); // 解析电台数据

        if (radioProto.sourceNo.equals(myCraneNo)) return; // TODO 再次验证

        //System.out.printf("#$$$$  %f -- %f \n", radioProto.getRotate(), radioProto.getRange());
        // System.out.println("####  ---a---");
        if (cmdRet == RadioProto.CMD_START_MASTER && waitFlag == true) { // 启动主机命令
            // System.out.println("####  ---a.0---");
            iAmMaster.set(true);
            runOnUiThread(() -> masterNoView.setText(myCraneNo));
            waitFlag = false;
            return;
        }

        long currTime = System.currentTimeMillis(); // 当前时间


        if (radioProto.isQuery) { // 收到主机的查询命令，本机必然为从机

            waitFlag = false;

            System.out.println("-------------------------");
            System.out.println(new String(event.getData()));
            System.out.println("-------------------------");
            iAmMaster.set(false);

            CycleElem master = craneMap.get(radioProto.getSourceNo()); // 作为从机, 更新主机的信息 // TODO 根据塔基类型，计算仰角
            runOnUiThread(() -> masterNoView.setText(radioProto.getSourceNo()));
            //System.out.println("####  ---b---");
            if (master != null) {
                //System.out.println("####  ---b.1---");
                SavedData savedData = savedDataMap.get(radioProto.getSourceNo());
                if (savedData == null) {
                    savedData = new SavedData(0, 0);
                    savedDataMap.put(radioProto.getSourceNo(), savedData);
                }

                //if (Math.abs(radioProto.getRange() - savedData.range) >= 0.1f) {
                runOnUiThread(() -> {
                    if (master.type == 1) { // 动臂式
                        double vangle = MathTool.calcVAngle(master.getBigArmLen(), radioProto.getRange(), master.archPara);
                        master.setVAngle((float) vangle); // 设置动臂式的仰角
                        master.setHeight(master.getOrgHeight() + master.getBigArmLen() * (float) Math.sin(Math.toRadians(vangle)));
                        master.setCarRange(master.getBigArmLen()); // 动臂式 幅度最大
                    } else {
                        master.setCarRange(radioProto.getRange()); // 平臂式实际幅度
                    }
                    master.setColor(Color.rgb(46, 139, 87));
                });
                //    savedData.range = radioProto.getRange();
                //}

                //System.out.printf("@@@@  %f -- %f \n", radioProto.getRotate(), radioProto.getRange());

                float hangle = MathTool.radiansToAngle(radioProto.getRotate());
                //if (Math.abs(hangle - savedData.angle) >= 0.1) {
                runOnUiThread(() -> {
                    master.setHAngle(hangle);
                    master.setColor(Color.rgb(46, 139, 87));
                    master.setOnline(true);
                    radioStatusMap.put(radioProto.getSourceNo(), currTime);
                });
                savedData.angle = hangle;
                //}
            }

            if (radioProto.getSourceNo().equals(radioProto.getTargetNo()) // 源ID和目标ID相同
                || radioProto.getTargetNo().equals(myCraneNo)) { // 目标ID相同是本机
                slaveRadioProto.setSourceNo(Integer.parseInt(myCraneNo));
                slaveRadioProto.setTargetNo(0); // 固定为0
                slaveRadioProto.setPermitNo(0);

                if (centerCycle.type == 1) { // 动臂式, 计算投影值
                    float shadow = (float) MathTool.calcShadow(centerCycle.getBigArmLen(), currProto.getRealVAngle(), centerCycle.archPara);
                    shadow = Math.round(shadow * 10) / 10.0f;
                    slaveRadioProto.setRange(shadow);
                } else {
                    slaveRadioProto.setRange(centerCycle.carRange);
                }

                slaveRadioProto.setRotate((centerCycle.hAngle % 360) * 2 * (float) Math.PI / 360);
                slaveRadioProto.packReply(); // 生成回应报文
                //StringTool.showCharArray1(slaveRadioProto.modleChars);
                try {
                    if (ttyS1OutputStream != null) {
                        //ttyS1OutputStream.write(slaveRadioProto.modleBytes); // 发送应答报文
                        ttyS1OutputStream.write(slaveRadioProto.modleBytes, 0, 39);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

                //if (Math.abs(radioProto.getRange() - savedData.range) >= 0.1f) {
                runOnUiThread(() -> {
                    if (slave.type == 1) { // 动臂式
                        double vangle = MathTool.calcVAngle(slave.getBigArmLen(), radioProto.getRange(), slave.archPara);
                        slave.setVAngle((float) vangle); // 设置动臂式的仰角
                        slave.setHeight(slave.getOrgHeight() + slave.getBigArmLen() * (float) Math.sin(Math.toRadians(vangle)));
                        slave.setCarRange(slave.getBigArmLen()); // 动臂式 幅度最大
                    } else {
                        slave.setCarRange(radioProto.getRange()); // 平臂式实际幅度
                    }
                });
                savedData.range = radioProto.getRange();
                //}

                float hangle = MathTool.radiansToAngle(radioProto.getRotate()); // 水平方向的夹角
                //if (Math.abs(hangle - savedData.angle) >= 0.1) {
                runOnUiThread(() -> {
                    slave.setHAngle(hangle);
                    slave.setColor(Color.rgb(46, 139, 87));
                });
                //}
                savedData.angle = hangle;
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
        System.out.printf("######## userEvent = %f\n", event.alarmSet.t2cDistGear1);
        alarmSet = event.alarmSet; // 更新配置
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CalibrationEventBus(CalibrationEvent event) {
        calibration = event.calibration;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void alarmShowEventBus(AlarmEvent event) {
        alarmEvent = event;
        if (alarmEvent == null) return;
        if (alarmEvent.leftAlarm == true) {
            Alarm.startAlarm(activity, R.id.left_alarm, Constant.rotateAlarmMap.get(event.leftAlarmLevel));
            leftAlarmView.setText(Constant.levelMap.get(event.leftAlarmLevel));
            AlarmSound.setStatus(R.raw.left_rotate_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.left_alarm, R.mipmap.forward);
            leftAlarmView.setText(Constant.levelMap.get(0));
            AlarmSound.setStatus(R.raw.left_rotate_alarm, false);
        }

        if (alarmEvent.rightAlarm == true) {
            Alarm.startAlarm(activity, R.id.right_alarm, Constant.rotateAlarmMap.get(event.rightAlarmLevel));
            rightAlarmView.setText(Constant.levelMap.get(event.rightAlarmLevel));
            AlarmSound.setStatus(R.raw.right_rotate_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.right_alarm, R.mipmap.forward);
            rightAlarmView.setText(Constant.levelMap.get(0));
            AlarmSound.setStatus(R.raw.right_rotate_alarm, false);
        }

        if (alarmEvent.forwardAlarm == true) {
            Alarm.startAlarm(activity, R.id.forward_alarm, Constant.carRangeAlarmMap.get(event.forwardAlarmLevel));
            forwardAlarmView.setText(Constant.levelMap.get(event.forwardAlarmLevel));
            AlarmSound.setStatus(R.raw.car_out_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.forward_alarm, R.mipmap.forward);
            forwardAlarmView.setText(Constant.levelMap.get(0));
            AlarmSound.setStatus(R.raw.car_out_alarm, false);
        }

        if (alarmEvent.backendAlarm == true) {
            Alarm.startAlarm(activity, R.id.back_alarm, Constant.carRangeAlarmMap.get(event.backendAlarmLevel));
            backwardAlarmView.setText(Constant.levelMap.get(event.backendAlarmLevel));
            AlarmSound.setStatus(R.raw.car_back_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.back_alarm, R.mipmap.forward);
            backwardAlarmView.setText(Constant.levelMap.get(0));
            AlarmSound.setStatus(R.raw.car_back_alarm, false);
        }

        if (alarmEvent.weightAlarm == true) {
            Alarm.startAlarm(activity, R.id.weight_alarm, Constant.weightAlarmMap.get(event.weightAlarmLevel));
            weightAlarmView.setText(Constant.levelMap.get(event.weightAlarmLevel));
            AlarmSound.setStatus(R.raw.weight_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.weight_alarm, R.mipmap.weight0);
            weightAlarmView.setText(Constant.levelMap.get(0));
            AlarmSound.setStatus(R.raw.weight_alarm, false);
        }

        if (alarmEvent.momentAlarm == true) {
            Alarm.startAlarm(activity, R.id.moment_alarm, Constant.momentAlarmMap.get(event.momentAlarmLevel));
            momentAlarmView.setText(Constant.levelMap.get(event.momentAlarmLevel));
            AlarmSound.setStatus(R.raw.moment_alarm, true);
        } else {
            Alarm.stopAlarm(activity, R.id.moment_alarm, R.mipmap.moment0);
            momentAlarmView.setText(Constant.levelMap.get(0));
            AlarmSound.setStatus(R.raw.moment_alarm, false);
        }

        if (alarmEvent.hookMinHightAlarm == true) {
            Alarm.startAlarm(activity, R.id.hook_alarm_logo, R.mipmap.hook_mix);
            hookAlarmVeiw.setText("T");
            hookAlarmVeiw.setRotation(180);
            AlarmSound.setStatus(R.raw.hook_down_warning, true);
        } else {
            AlarmSound.setStatus(R.raw.hook_down_warning, false);
        }

        if (alarmEvent.hookMaxHightAlarm == true) {
            Alarm.startAlarm(activity, R.id.hook_alarm_logo, R.mipmap.hook_max);
            hookAlarmVeiw.setText("T");
            AlarmSound.setStatus(R.raw.hook_up_warning, true);
        } else {
            AlarmSound.setStatus(R.raw.hook_up_warning, false);
        }

        if (alarmEvent.hookMinHightAlarm == false && alarmEvent.hookMaxHightAlarm == false) {
            Alarm.startAlarm(activity, R.id.hook_alarm_logo, R.mipmap.hook);
            hookAlarmVeiw.setText("OK");
        }

        // 控制
        if (Alarm.controlSet(event, controlProto)) {
            try {
                for (int i = 0; i < controlProto.control.length; i++) {
                    System.out.printf("%02x ", controlProto.control[i]);
                }
                System.out.println("");

                ttyS0OutputStream.write(controlProto.control);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //AlarmSound.setStatus(R.raw.left_rotate_alarm, true);
        //AlarmSound.start(0);

        // 告警铃声
        if (event.hasAlarm) {
            AlarmSound.start(0);
        }
        // 告警铃声清除
        if (!event.hasAlarm) {
            AlarmSound.pause();
        }

    }

    private void setCurrTime() {
        Date date = new Date();
        String dateNowStr = sdf.format(date);
        String[] cells = dateNowStr.split(" ");
        //TextView currTime = (TextView) findViewById(R.id.currTime);
        TextView currDate = (TextView) findViewById(R.id.currDate);
        currDate.setText(cells[0]);
        //currTime.setText(cells[1]);
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void weightEventBus(WeightEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.weight);
        view.setText(event.getWeight() + "t");
    }

    public void weightShow(float weight) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.weight);
        view.setText(weight + "t");
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
            craneView.setHookHeight((int) hookHeight);
        }
    }

    public void heightShow(float height) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.height);
        view.setText(height + "m");

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
            craneView.setHookHeight((int) hookHeight);
        }
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void lengthEventBus(LengthEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.length);
        view.setText(event.getLength() + "m");

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

    public void lengthShow(float length) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.length);
        view.setText(length + "m");

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
        centerCycle.setHAngle(angle);
        float showAngle = angle;
        angleView.setText(showAngle + "°");
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
            if (craneType == null) craneType = "UNSELECTED";
            ((TextView) findViewById(R.id.craneType)).setText(craneType); // 显示塔基类型
        }

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
        ((TextView) findViewById(R.id.cable)).setText(power); // 显示塔基类型

        loadParas = loadDao.getLoads(craneType, armLength, power); // 获取负荷特性
        if (loadParas != null) {
            ((TextView) findViewById(R.id.rated_weight)).setText(loadParas.get(0).getWeight() + "t"); // 显示塔基类型
            for (Load load : loadParas) {
                //System.out.printf("%s--%s\n", load.getCoordinate(), load.getWeight());
            }
        }
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
                    menuExpand.setVisibility(View.VISIBLE);
                    AnimUtil.alphaAnimation(btnMenu);
                    btnMenu.setImageResource(R.mipmap.menu_off);
                    menuExpand.setAnimation(AnimationUtils.makeInAnimation(contex, true));
                } else {
                    menuExpand.setVisibility(View.GONE);
                    AnimUtil.alphaAnimation(btnMenu);
                    menuExpand.setAnimation(AnimationUtils.makeOutAnimation(contex, false));
                    btnMenu.setImageResource(R.mipmap.menu_on);
                }
                Log.i(TAG, "## click!");
            }
        });

        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.crane_setting));
            add((ImageView) findViewById(R.id.area_setting));
            add((ImageView) findViewById(R.id.protect_area_setting));
            add((ImageView) findViewById(R.id.calibration_setting));
            add((ImageView) findViewById(R.id.alarm_setting));
            add((ImageView) findViewById(R.id.load_attribute));
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
        craneNumbers.add(number); // 本机的编号

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
                mainCrane.getBalancArmLength(), 0, 0, 0, cp.getCraneHeight(), number);
            sideCycle.setType(cp.getType()); // 平臂或动臂
            sideCycle.setArchPara(cp.getArchPara()); // 保存结构参数
            sideCycle.setMinVAngle(cp.getMinAngle()); // 最小垂直方向倾角
            sideCycle.setBigArmLen(bigArmLength); // 保存大臂长度
            sideCycle.setOrgHeight(cp.getCraneHeight()); // 塔基原始身高
            elemMap.addElem(number, sideCycle);
            //sideCycleId = sideCycle.getUuid();
            sideCycle.drawSideCycle(this, mainFrame);
            craneNumbers.add(number);
            craneMap.put(number, sideCycle);
            sideCycle.setOnline(false); // 初始状态离线
        }
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
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
        CraneDao craneDao = new CraneDao(MainActivity.this);
        AreaDao areaDao = new AreaDao(MainActivity.this);
        ProtectAreaDao protectAreaDao = new ProtectAreaDao(MainActivity.this);
        AlarmSetDao alarmSetDao = new AlarmSetDao(MainActivity.this);
        CalibrationDao calibrationDao = new CalibrationDao(MainActivity.this);
        loadDao = new LoadDao(MainActivity.this);

        List<Crane> cranes = craneDao.selectAll();
        if (cranes == null || cranes.size() == 0) { // 初始状态, 创建表
            DatabaseHelper.getInstance(context).createTable(Crane.class);

            DatabaseHelper.getInstance(context).createTable(Area.class); // 区域

            DatabaseHelper.getInstance(context).createTable(Protect.class); // 保护区
            DatabaseHelper.getInstance(context).createTable(SysPara.class);
            DatabaseHelper.getInstance(context).createTable(AlarmSet.class); // 告警
            alarmSetDao.insert(AlarmSet.getInitData());

            DatabaseHelper.getInstance(context).createTable(Load.class); // 负载
            loadDao.insert(Load.getInitData());
        }

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

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        angleView = (TextView) findViewById(R.id.angle);
        vAngleView = (TextView) findViewById(R.id.vangle);
        controlProto.clear();
        EventBus.getDefault().register(this);
        initTable(); // 初始化表
        paraDao = new SysParaDao(getApplicationContext()); // 系统参数
        eventBus.post(new SysParaEvent()); // 触发系统参数相关

        leftAlarmView = (TextView) findViewById(R.id.left_alarm_level);
        rightAlarmView = (TextView) findViewById(R.id.right_alarm_level);
        weightAlarmView = (TextView) findViewById(R.id.weight_alarm_level);
        hookAlarmVeiw = (TextView) findViewById(R.id.hook_alarm);
        momentAlarmView = (TextView) findViewById(R.id.moment_alarm_level);
        forwardAlarmView = (TextView) findViewById(R.id.forward_alarm_level);
        backwardAlarmView = (TextView) findViewById(R.id.back_alarm_level);
        momentView = (TextView) findViewById(R.id.moment);
        ratedWeightView = (TextView) findViewById(R.id.rated_weight);
        masterNoView = (TextView) findViewById(R.id.master_no);

        try {
            String s0Name = "S0";
            String s1Name = "S1";
            String model = android.os.Build.MODEL;
            if (model.equals("rk3288")) {
                s0Name = "S1";
                s1Name = "S2";
            }

            serialttyS0 = new SerialPort(
                s0Name, 115200, 8, 0, 'o', true); // 19200 // AD数据
            serialttyS1 = new SerialPort(
                s1Name, 19200, 8, 0, 'o', true);

            ttyS0InputStream = serialttyS0.getInputStream();
            ttyS0OutputStream = serialttyS0.getOutputStream();
            ttyS0OutputStream.write(ControlProto.control);
            ttyS1InputStream = serialttyS1.getInputStream();
            ttyS1OutputStream = serialttyS1.getOutputStream();

            serialttyS2 = new SerialPort(
                "S3", 19200, 8, 1, 'e', true);
            ttyS2OutputStream = serialttyS2.getOutputStream();
            ttyS2InputStream = serialttyS2.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setCurrTime();

        // 触发判断本机是否为主机
        new Handler().postDelayed(() -> {
            startSensorThread(); // 初始化串口线程
            startRadioReadThread();
            startRadioWriteThread();
            startTimerThread();
        }, 1000);

        // 触发判断本机是否为主机
        new Handler().postDelayed(() -> {
            RadioDateEventOps(new RadioEvent(radioProto.startMaster()));
        }, 3000);
    }

}
