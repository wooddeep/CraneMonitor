package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.x6.serial.SerialPort;
import com.wooddeep.crane.alarm.Alarm;
import com.wooddeep.crane.comm.Protocol;
import com.wooddeep.crane.comm.RadioProto;
import com.wooddeep.crane.comm.RotateProto;
import com.wooddeep.crane.ebus.AlarmDetectEvent;
import com.wooddeep.crane.ebus.AlarmEvent;
import com.wooddeep.crane.ebus.AlarmSetEvent;
import com.wooddeep.crane.ebus.AlarmShowEvent;
import com.wooddeep.crane.ebus.CalibrationCloseEvent;
import com.wooddeep.crane.ebus.CalibrationEvent;
import com.wooddeep.crane.ebus.FanSpeedEvent;
import com.wooddeep.crane.ebus.HeightEvent;
import com.wooddeep.crane.ebus.LengthEvent;
import com.wooddeep.crane.ebus.MessageEvent;
import com.wooddeep.crane.ebus.RadioEvent;
import com.wooddeep.crane.ebus.RotateEvent;
import com.wooddeep.crane.ebus.SimulatorEvent;
import com.wooddeep.crane.ebus.UartEvent;
import com.wooddeep.crane.ebus.WeightEvent;
import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.ElemMap;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.AlarmSetDao;
import com.wooddeep.crane.persist.dao.AreaDao;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.CraneParaDao;
import com.wooddeep.crane.persist.dao.LoadDao;
import com.wooddeep.crane.persist.dao.ProtectAreaDao;
import com.wooddeep.crane.persist.dao.ProtectDao;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.CranePara;
import com.wooddeep.crane.persist.entity.Load;
import com.wooddeep.crane.persist.entity.Protect;
import com.wooddeep.crane.simulator.SimulatorFlags;
import com.wooddeep.crane.simulator.UartEmitter;
import com.wooddeep.crane.tookit.AnimUtil;
import com.wooddeep.crane.tookit.CommTool;
import com.wooddeep.crane.tookit.DrawTool;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;


// 启动mumu之后, 输入：
// adb connect 127.0.0.1:7555
// 然后再调试, 就ok了

// eventbus for android
// https://www.jianshu.com/p/428a5257839c

// set 按钮的前景 后台
// https://blog.csdn.net/oathevil/article/details/23707359

// FrameLayout 分层权重问题
// https://www.jianshu.com/p/c1d17a39bc09

// 仿qq弹出菜单
// https://github.com/Zhaoss/HintPopupWindow

// 仿苹果对话框
// https://github.com/saiwu-bigkoo/Android-AlertView

// 自定义按钮
// https://www.cnblogs.com/Free-Thinker/p/5571876.html

// ormlite
// https://blog.csdn.net/u013501637/article/details/52388802

// 颜色值查询  
// https://www.colorhexa.com/d4237a

/*
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:26.0.0-alpha1'
    implementation 'com.android.support.constraint:constraint-layout:1.0.1'
    implementation 'org.locationtech.jts:jts-core:1.15.0'
    implementation 'org.greenrobot:eventbus:3.0.0'
    }
*/

/**
 * 获取主界面FrameLayout的坐标及长宽
 **/
/*
@Override
public void onWindowFocusChanged (boolean hasFocus) {
    FrameLayout mainFrame = findViewById(R.id.main_frame);
    int[] location = new int[2];
    mainFrame.getLocationInWindow(location);
    Context context = getApplicationContext();
    int width = mainFrame.getMeasuredWidth(); // 获取组件宽度
    int height = mainFrame.getMeasuredHeight(); // 获取组件高度
    Log.i(TAG, String.format("## MainFrame: x0:%d, y0:%d, width:%d, height:%d\n", location[0], location[1], width, height));
}
*/

// 圆环设置
/*
SuperCircleView mSuperCircleView;
mSuperCircleView = findViewById(R.id.superview);
mSuperCircleView.setValue(100);
mSuperCircleView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //随机设定圆环大小
        int i = new Random().nextInt(100) + 1;
        //int i = 2;
        Log.i(TAG, "onClick: i::" + i);
        mSuperCircleView.setValue(i);
    }
});
*/

// 进度条
/*
final SimpleProgressbar spb = (SimpleProgressbar) findViewById(R.id.spb);
final int max = spb.getMax();
new Thread(new Runnable() {
    @Override
    public void run() {
        int progress = spb.getProgress();
        while ((progress + 1) <= max) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spb.setProgress(progress + 1);
            progress = progress + 1;
        }
    }
}).start();

Coordinate pointer0 = new Coordinate(0, 0);
Coordinate pointer1 = new Coordinate(0, 10);
double distance = pointer0.distance(pointer1);
try {
    Geometry g1 = new WKTReader().read("LINESTRING (0 10, 10 0)");
    Geometry g2 = new WKTReader().read("POINTSTRING (0 0)");
    double d = g1.distance(g2);
    Log.d(TAG, "### this distance = " + d);
} catch (ParseException e) {
    e.printStackTrace();
}

// android动画 https://blog.csdn.net/qq_19431333/article/details/87690200

// Android绘制(三):Path结合属性动画, 让图标动起来! https://www.jianshu.com/p/923eb80e80a3

*/

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {

    class SlaveData {
        float angle;
        float range;
        public SlaveData(float a, float r) {
            this.angle = a;
            this.range = r;
        }
    }

    private static final String TAG = "MainActivity";
    private Context context;
    private android.app.Activity activity = this;
    private List<BaseElem> elemList = new ArrayList<>();
    private ElemMap elemMap = new ElemMap();
    private static HashMap<String, Object> viewMapBak = new HashMap<>();
    private static HashMap<String, Object> zoomMapBak = new HashMap<>();

    private float oscale = 1.0f;
    private String mainCycleId = null; // 主环的uuid
    private String sideCycleId = null;

    private Protocol prevProto = new Protocol(); // 记录前次ad数据解析结果
    private Protocol currProto = new Protocol(); // 记录当次ad数据解析结果

    private RotateProto currRotateProto = new RotateProto();
    private RotateProto prevRotateProto = new RotateProto();
    private RadioProto radioProto = new RadioProto();
    private HashMap<String, SlaveData> slaveMap = new HashMap<>();

    private int craneType = 0; // 塔基类型: 0 ~ 平臂式, 2动臂式
    private Crane mainCrane;   // 本塔基参数
    private CenterCycle centerCycle; // 本塔基圆环
    private AlarmSet alarmSet = null;
    private AlarmEvent alarmEvent = null;
    private Calibration calibration = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean isMasterCrane = false; // 是否主塔机
    private String myCraneNo = "1N";
    private List<String> craneNumbers = new ArrayList<>();
    private HashMap<String, CycleElem> craneMap = new HashMap<>();
    private SimulatorFlags flags = new SimulatorFlags();
    private UartEmitter emitter = new UartEmitter();

    private boolean calibrationFlag = false;
    private EventBus eventBus = EventBus.getDefault();

    private boolean iAmMaster = false; // 本机是否为通信主机
    private boolean waitFlag = true; // 等待主机信号标识

    private RadioProto slaveRadioProto = new RadioProto();  // 本机作为从机时，需要radio通信的对象
    private RadioProto masterRadioProto = new RadioProto(); // 本机作为主机时，需要radio通信的对象
    private int currSlaveIndex = 0; // 当前和本主机通信的从机名称

    private SerialPort serialttyS0;
    private SerialPort serialttyS1;
    private InputStream ttyS0InputStream;
    private OutputStream ttyS0OutputStream;
    private InputStream ttyS1InputStream;
    private OutputStream ttyS1OutputStream;

    private byte[] adXBuff = new byte[1024];
    private byte[] rotateXBuff = new byte[1024];
    private byte[] radioXBuff = new byte[1024];
    private byte[] adRBuff = new byte[20];
    private byte[] rotateRBuff = new byte[10];
    private byte[] radioRBuff = new byte[39];

    private HeightEvent heightEvent = new HeightEvent();
    private WeightEvent weightEvent = new WeightEvent();
    private LengthEvent lengthEvent = new LengthEvent();
    private RotateEvent rotateEvent = new RotateEvent();
    private RadioEvent radioEvent = new RadioEvent();
    private AlarmDetectEvent alarmDetectEvent = new AlarmDetectEvent();

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

                    if (alarmTimes % 2 == 0) { // 100毫秒 累计一次
                        emitter.emitter(); // 累计
                    }
                }

                byte[] data = currProto.pack(); // TODO 判断值是否有变化, 无变化，则不发送消息
                if (calibrationFlag) {
                    eventBus.post(new UartEvent(data)); // 发送
                }

                currProto.parse(data);
                currProto.calcRealHeight(calibration);
                currProto.calcRealLength(calibration);
                currProto.calcRealWeigth(calibration);
                if (Math.abs(currProto.getRealLength() - prevProto.getRealLength()) > 0.05f) {
                    lengthEvent.setLength(currProto.getRealLength());
                    eventBus.post(lengthEvent);
                    prevProto.setRealLength(currProto.getRealLength());
                }

                if (mainCrane != null) {  // 次环
                    // 模拟回转
                    data = currRotateProto.pack();
                    rotateEvent.setData(data);
                    currRotateProto.parse(data);
                    currRotateProto.calcAngle(calibration);
                    if (Math.abs(currRotateProto.getAngle() - prevRotateProto.getAngle()) > 0.05f) {
                        rotateEvent.setCenterX(mainCrane.getCoordX1());
                        rotateEvent.setCenterY(mainCrane.getCoordY1());
                        rotateEvent.setAngle(currRotateProto.getAngle());
                        eventBus.post(rotateEvent);
                        prevRotateProto.setAngle(currRotateProto.getAngle());
                    }

                    // TODO
                    radioProto.setSourceNo("2N"); // 从机的ID为2N
                    radioProto.setTargetNo("0N"); // 从机回应
                    radioProto.setRange(emitter.getCarRange());
                    radioProto.setRotate(-emitter.getsAmplitude());

                    // 模拟电台
                    eventBus.post(new RadioEvent(radioProto.packReply()));
                }

                if (alarmTimes % 1000 == 0) {
                    eventBus.post(alarmDetectEvent); // 发送alarm展示消息
                }

                alarmTimes++;
                CommTool.sleep(10);
            }
        }).start();
    }

    private void startAdThread() {
        new Thread(() -> {
            try {
                while (true) {
                    // AD 值
                    if (ttyS0InputStream.available() > 0) {
                        int len = ttyS0InputStream.read(adXBuff, 0, 1024);
                        for (int i = len - 20, j = 0; i < len; j++, i++) {
                            adRBuff[j] = adXBuff[i];
                        }

                        currProto.parse(adRBuff);
                        currProto.calcRealHeight(calibration);
                        currProto.calcRealLength(calibration);
                        currProto.calcRealWeigth(calibration);

                        if (Math.abs(currProto.getRealLength() - prevProto.getRealLength()) > 0.05f) {
                            lengthEvent.setLength(currProto.getRealLength());
                            prevProto.setRealLength(currProto.getRealLength());
                            eventBus.post(lengthEvent);
                        }

                        if (Math.abs(currProto.getRealHeight() - prevProto.getRealHeight()) > 0.05f) {
                            heightEvent.setHeight(currProto.getRealHeight());
                            prevProto.setRealHeight(currProto.getRealHeight());
                            eventBus.post(heightEvent);
                        }

                        if (Math.abs(currProto.getRealWeight() - prevProto.getRealWeight()) > 0.05f) {
                            weightEvent.setWeight(currProto.getRealWeight());
                            prevProto.setRealWeight(currProto.getRealWeight());
                            eventBus.post(weightEvent);
                        }
                    }

                    CommTool.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startRotateThread() {
        new Thread(() -> {
            try {
                // 485 回转, 硬件有点问题, ttyS3 对应单板的串口2, 编号从0开始, 白色R2，黑色 T2
                SerialPort serialttyS2 = new SerialPort(new File("/dev/ttyS3"), 19200, 0);
                OutputStream ttyS2OutputStream = serialttyS2.getOutputStream();
                InputStream ttyS2InputStream = serialttyS2.getInputStream();

                byte[] rotateCmd = new byte[]{0x01, 0x04, 0x00, 0x01, 0x00, 0x02, 0x20, 0x0B};
                while (true) {

                    ttyS2OutputStream.write(rotateCmd);
                    if (ttyS2InputStream.available() > 0) {
                        int len = ttyS2InputStream.read(rotateXBuff, 0, 1024);

                        for (int i = 0; i < 9; i++) {
                            rotateRBuff[i] = rotateXBuff[i];
                        }

                        if (rotateRBuff[0] == 0x01 && rotateRBuff[1] == 0x04) {
                            currRotateProto.parse(rotateRBuff);
                            currRotateProto.calcAngle(calibration);
                            if (Math.abs(currRotateProto.getAngle() - prevRotateProto.getAngle()) >= 0.1f) {
                                rotateEvent.setCenterX(mainCrane.getCoordX1());
                                rotateEvent.setCenterY(mainCrane.getCoordY1());
                                rotateEvent.setAngle(currRotateProto.getAngle());
                                eventBus.post(rotateEvent);
                                prevRotateProto.setAngle(currRotateProto.getAngle());
                            }

                        }
                    }
                    CommTool.sleep(50);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void radioDateParse(byte []  data) {
        int cmdRet = radioProto.parse(data);
        if (cmdRet == RadioProto.CMD_START_MASTER && waitFlag == true) { // 启动主机命令
            iAmMaster = true;
            // 发消息, 启动轮训从机
            if (craneNumbers.size() <= 0) return;
            currSlaveIndex = 0;
            String currSlaveNo = craneNumbers.get(currSlaveIndex);
            if (currSlaveNo.equals(myCraneNo)) return;
            masterRadioProto.setSourceNo(myCraneNo);
            masterRadioProto.setTargetNo(currSlaveNo);
            masterRadioProto.setRotate(centerCycle.hAngle);  // 实际的物理维度值，不是按比例值的值
            masterRadioProto.setRange(centerCycle.carRange); // 实际的物理维度值，不是按比例值的值

            // TODO 发送消息，通知查询第一个从机
            return;
        }

        if (radioProto.isQuery) { // 收到主机的查询命令，本机必然为从机
            waitFlag = false;
            iAmMaster = false;

            if (radioProto.getTargetNo().equals(myCraneNo)) { // 回应
                slaveRadioProto.setSourceNo(myCraneNo);
                slaveRadioProto.setTargetNo(radioProto.getSourceNo());
                slaveRadioProto.setRange(1f); // TODO 根据本塔基的实际数据填充
                slaveRadioProto.setRotate(2f); // TODO 根据本塔基的实际数据填充
                slaveRadioProto.packReply();
                // TODO 发送消息，通知串口通信 或者直接 串口通信
            }

            return;
        }

        if (!radioProto.isQuery) { // 收到其他从机的回应命令 & 分自己的 主从 身份
            waitFlag = false;

            // 更新从机
            CycleElem slave = craneMap.get(radioProto.getSourceNo());
            if (slave != null) {
                SlaveData prevData = slaveMap.get(radioProto.getSourceNo());
                if (prevData == null) {
                    prevData = new SlaveData(radioProto.getRotate(), radioProto.getRange());
                    slaveMap.put(radioProto.getSourceNo(), prevData);
                }

                if (Math.abs(radioProto.getRotate() - prevData.angle) >= 0.1f) {
                    slave.setHAngle(radioProto.getRotate());
                    prevData.angle = radioProto.getRotate();

                    //eventBus.post(radioEvent);
                    //prevRotateProto.setAngle(currRotateProto.getAngle());
                }

                if (Math.abs(radioProto.getRange() - prevData.range) >= 0.1f) {
                    slave.setCarRange(radioProto.getRange());
                    prevData.range = radioProto.getRange();

                    //eventBus.post(radioEvent);
                    //prevRotateProto.setAngle(currRotateProto.getAngle());
                }
            }

            if (iAmMaster) {
                masterRadioProto.setSourceNo(myCraneNo);
                currSlaveIndex = (currSlaveIndex + 1) % craneNumbers.size();
                masterRadioProto.setTargetNo(craneNumbers.get(currSlaveIndex));
                masterRadioProto.setRotate(centerCycle.hAngle);  // 实际的物理维度值，不是按比例值的值
                masterRadioProto.setRange(centerCycle.carRange); // 实际的物理维度值，不是按比例值的值
                // TODO 发消息 触发 轮训，携带 当前从机ID， 进行下一个从机的发送
            }

            return;
        }
    }


    private void startRadioThread() {
        new Thread(() -> {
            try {
                while (true) {

                    if (ttyS1InputStream.available() > 0) {
                        int len = ttyS1InputStream.read(radioXBuff, 0, 1024);
                        for (int i = 0; i < 39; i++) {
                            radioRBuff[i] = radioXBuff[i];
                        }

                        radioDateParse(radioRBuff);

                        // radioEvent.setData(radioRBuff);
                        // eventBus.post(radioEvent); // 发送
                    }

                    CommTool.sleep(80);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 侦听电台数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RadioDateEventBus(RadioEvent event) {
        int cmdRet = radioProto.parse(event.getData());
        if (cmdRet == RadioProto.CMD_START_MASTER && waitFlag == true) { // 启动主机命令
            iAmMaster = true;
            // 发消息, 启动轮训从机
            if (craneNumbers.size() <= 0) return;
            currSlaveIndex = 0;
            String currSlaveNo = craneNumbers.get(currSlaveIndex);
            if (currSlaveNo.equals(myCraneNo)) return;
            masterRadioProto.setSourceNo(myCraneNo);
            masterRadioProto.setTargetNo(currSlaveNo);
            masterRadioProto.setRotate(centerCycle.hAngle);  // 实际的物理维度值，不是按比例值的值
            masterRadioProto.setRange(centerCycle.carRange); // 实际的物理维度值，不是按比例值的值

            // TODO 发送消息，通知查询第一个从机
            return;
        }

        if (radioProto.isQuery) { // 收到主机的查询命令，本机必然为从机
            waitFlag = false;
            iAmMaster = false;

            if (radioProto.getTargetNo().equals(myCraneNo)) { // 回应
                slaveRadioProto.setSourceNo(myCraneNo);
                slaveRadioProto.setTargetNo(radioProto.getSourceNo());
                slaveRadioProto.setRange(1f); // TODO 根据本塔基的实际数据填充
                slaveRadioProto.setRotate(2f); // TODO 根据本塔基的实际数据填充
                slaveRadioProto.packReply();
                // TODO 发送消息，通知串口通信 或者直接 串口通信
            }

            return;
        }

        if (!radioProto.isQuery) { // 收到其他从机的回应命令 & 分自己的 主从 身份
            waitFlag = false;

            // 更新从机
            CycleElem slave = craneMap.get(radioProto.getSourceNo());
            if (slave != null) {
                slave.setCarRange(radioProto.getRange());
                slave.setHAngle(radioProto.getRotate());
            }

            if (iAmMaster) {
                masterRadioProto.setSourceNo(myCraneNo);
                currSlaveIndex = (currSlaveIndex + 1) % craneNumbers.size();
                masterRadioProto.setTargetNo(craneNumbers.get(currSlaveIndex));
                masterRadioProto.setRotate(centerCycle.hAngle);  // 实际的物理维度值，不是按比例值的值
                masterRadioProto.setRange(centerCycle.carRange); // 实际的物理维度值，不是按比例值的值
                // TODO 发消息 触发 轮训，携带 当前从机ID， 进行下一个从机的发送
            }

            return;
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
    public void messageEventBus(MessageEvent userEvent) {
        fanRotate();
        elemMap.alramFlink();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void alarmShowEventBus(AlarmEvent event) {
        alarmEvent = event;
        if (alarmEvent == null) return;
        if (alarmEvent.leftAlarm == true) {
            Alarm.startAlarm(activity, R.id.left_alarm, R.mipmap.left_rotation_alarm);
        } else {
            Alarm.stopAlarm(activity, R.id.left_alarm, R.mipmap.left_rotation);
        }

        if (alarmEvent.rightAlarm == true) {
            Alarm.startAlarm(activity, R.id.right_alarm, R.mipmap.right_rotation_alarm);
        } else {
            Alarm.stopAlarm(activity, R.id.right_alarm, R.mipmap.right_rotation);
        }

        if (alarmEvent.forwardAlarm == true) {
            Alarm.startAlarm(activity, R.id.forward_alarm, R.mipmap.forward_alarm);
        } else {
            Alarm.stopAlarm(activity, R.id.forward_alarm, R.mipmap.forward);
        }

        if (alarmEvent.backendAlarm == true) {
            Alarm.startAlarm(activity, R.id.back_alarm, R.mipmap.backoff_alarm);
        } else {
            Alarm.stopAlarm(activity, R.id.back_alarm, R.mipmap.backoff);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AlarmJudgeEventBus(AlarmDetectEvent event) {
        try {
            if (elemList.size() == 0) return;
            Alarm.alarmDetect(calibration, elemList, craneMap, myCraneNo, alarmSet, eventBus);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCurrTime() {
        Date date = new Date();
        String dateNowStr = sdf.format(date);
        String[] cells = dateNowStr.split(" ");
        TextView currTime = (TextView) findViewById(R.id.currTime);
        TextView currDate = (TextView) findViewById(R.id.currDate);
        currDate.setText(cells[0]);
        currTime.setText(cells[1]);
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void weightEventBus(WeightEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.weight);
        view.setText(event.getWeight() + "t");
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void heigthEventBus(HeightEvent event) {
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

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void fanSpeedEventBus(FanSpeedEvent event) {
        if (calibration == null) return;
        TextView view = (TextView) findViewById(R.id.wind_speed);
        view.setText(event.getSpeed() + "m/s");
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RotateDateEventBus(RotateEvent event) {
        angleView.setText(event.angle + "°");
        centerCycle.setHAngle(event.angle);
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

    private void fanRotate() {
        ImageView windSpeedLog = (ImageView) findViewById(R.id.wind_speed_log);
        Log.i(TAG, "## start rotate!!!");
        //上述参数解释分别为：旋转起始角度，旋转结束角度，相对与自身，x轴方向的一半，相对于自身，y轴方向的一半

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.54f);
        rotateAnimation.setDuration(500);
        windSpeedLog.setAnimation(rotateAnimation);
        windSpeedLog.startAnimation(rotateAnimation);
    }


    private List<CranePara> confLoad(Context contex) {
        CraneParaDao dao = new CraneParaDao(contex);
        List<CranePara> paras = dao.getAllCranePara();
        return paras;
    }

    private void renderMenu() {
        FrameLayout mainFrame = (FrameLayout) findViewById(R.id.main_frame);

        HashMap<String, Object> viewMap = DrawTool.drawMenu(this, mainFrame);
        HashMap<String, Object> zoomMap = DrawTool.drawZoom(this, mainFrame);

        if (!viewMapBak.isEmpty()) {
            DrawTool.eraseMenu(this, mainFrame, viewMapBak);
        }
        viewMapBak = viewMap;
        if (!zoomMapBak.isEmpty()) {
            DrawTool.eraseZoom(this, mainFrame, zoomMapBak);
        }
        zoomMapBak = zoomMap;

        ((ImageView) viewMap.get("R.id.menu")).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView btnMenu = (ImageView) viewMap.get("R.id.menu");
                LinearLayout menuExpand = (LinearLayout) viewMap.get("R.id.menu_expand");
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
            add((ImageView) viewMap.get("R.id.crane_setting"));
            add((ImageView) viewMap.get("R.id.area_setting"));
            add((ImageView) viewMap.get("R.id.protect_area_setting"));
            add((ImageView) viewMap.get("R.id.calibration_setting"));
            add((ImageView) viewMap.get("R.id.alarm_setting"));
            add((ImageView) viewMap.get("R.id.load_attribute"));
            add((ImageView) zoomMap.get("R.id.zoom_in"));
            add((ImageView) zoomMap.get("R.id.zoom_out"));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
        }

        // 放大
        ImageView zoomIn = (ImageView) zoomMap.get("R.id.zoom_in");
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
        ImageView zoomOut = (ImageView) zoomMap.get("R.id.zoom_out");
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
        ImageView craneSetting = (ImageView) viewMap.get("R.id.crane_setting");
        craneSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CraneSetting.class);
                startActivity(intent);
            }
        });


        // 跳到区域设置页面
        ImageView areaSetting = (ImageView) viewMap.get("R.id.area_setting");
        areaSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AreaSetting.class);
                startActivity(intent);
            }
        });

        ImageView protectSetting = (ImageView) viewMap.get("R.id.protect_area_setting");
        protectSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProtectSetting.class);
                startActivity(intent);
            }
        });

        // 跳到标定设置页面
        ImageView calibrationSetting = (ImageView) viewMap.get("R.id.calibration_setting");
        calibrationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calibrationFlag = true; // 标定标识 TODO 放置到页面跳转处触发
                Intent intent = new Intent(MainActivity.this, CalibrationSetting.class);
                startActivity(intent);
            }
        });

        // 跳到告警设置页面
        ImageView alarmSetting = (ImageView) viewMap.get("R.id.alarm_setting");
        alarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlarmSetting.class);
                startActivity(intent);
            }
        });

        // 跳转到负荷特性
        ImageView loadAttribute = (ImageView) viewMap.get("R.id.load_attribute");
        loadAttribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoadAttribute.class);
                startActivity(intent);
            }
        });
    }

    private void renderMain(float oscale) {
        FrameLayout mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        CraneDao craneDao = new CraneDao(MainActivity.this);

        List<Crane> paras = craneDao.selectAll();
        if (paras.size() == 0) return;

        elemMap.delElems(mainFrame);

        mainCrane = paras.get(0);
        for (Crane iterator : paras) {
            if (iterator.getIsMain() == true) {
                mainCrane = iterator;
                break;
            }
        }
        String number = Integer.parseInt(mainCrane.getName().replaceAll("[^0-9]+", "")) + "N";
        myCraneNo = number;
        craneNumbers.add(number); // 本机的编号

        // 画中心圆环
        centerCycle = new CenterCycle(oscale, mainCrane.getCoordX1(), mainCrane.getCoordY1(), mainCrane.getBigArmLength(),
            mainCrane.getBalancArmLength(), 0, 0, 0, mainCrane.getCraneHeight(), number);

        elemMap.addElem(centerCycle.getUuid(), centerCycle);
        mainCycleId = centerCycle.getUuid();
        centerCycle.drawCenterCycle(this, mainFrame);
        craneMap.put(myCraneNo, centerCycle);

        // 根据数据库的数据画图
        for (Crane cp : paras) {
            if (cp == mainCrane) continue;
            float scale = centerCycle.scale;
            number = Integer.parseInt(cp.getName().replaceAll("[^0-9]+", "")) + "N";
            SideCycle sideCycle = new SideCycle(centerCycle, cp.getCoordX1(), cp.getCoordY1(), cp.getBigArmLength(),
                mainCrane.getBalancArmLength(), 0, 0, 0, cp.getCraneHeight(), number);

            elemMap.addElem(sideCycle.getUuid(), sideCycle);
            sideCycleId = sideCycle.getUuid();
            sideCycle.drawSideCycle(this, mainFrame);
            craneNumbers.add(number);
            craneMap.put(number, sideCycle);
        }

        // 区域
        int areaIndex = 1;
        AreaDao areaDao = new AreaDao(MainActivity.this);
        List<Area> areas = areaDao.selectAll();
        if (areas != null && areas.size() > 0) {
            for (Area area : areas) {
                List<Vertex> vertex = new ArrayList<Vertex>();
                vertex.add(new Vertex(area.getX1(), area.getY1()));
                vertex.add(new Vertex(area.getX2(), area.getY2()));
                vertex.add(new Vertex(area.getX3(), area.getY3()));
                vertex.add(new Vertex(area.getX4(), area.getY4()));
                vertex.add(new Vertex(area.getX5(), area.getY5()));
                vertex.add(new Vertex(area.getX6(), area.getY6()));
                vertex = CommTool.arrangeVertexList(vertex);
                //number = Integer.parseInt(area..replaceAll("[^0-9]+", "")) + "N";
                SideArea sideArea = new SideArea(centerCycle, Color.rgb(19, 34, 122),
                    vertex, 0, area.getHeight(), String.format("%dA", areaIndex++));
                elemMap.addElem(sideArea.getUuid(), sideArea);
                elemList.add(sideArea);
                sideArea.drawSideArea(this, mainFrame);
            }

        }

        // 保护区
        int protectIndex = 1;
        ProtectDao protectDao = new ProtectDao(MainActivity.this);
        List<Protect> protects = protectDao.selectAll();
        if (protects != null && protects.size() > 0) {
            for (Protect protect : protects) {
                List<Vertex> vertex = new ArrayList<Vertex>();
                vertex.add(new Vertex(protect.getX1(), protect.getY1()));
                vertex.add(new Vertex(protect.getX2(), protect.getY2()));
                vertex.add(new Vertex(protect.getX3(), protect.getY3()));
                vertex.add(new Vertex(protect.getX4(), protect.getY4()));
                vertex.add(new Vertex(protect.getX5(), protect.getY5()));
                vertex.add(new Vertex(protect.getX6(), protect.getY6()));
                vertex = CommTool.arrangeVertexList(vertex);
                SideArea sideArea = new SideArea(centerCycle, Color.BLACK,
                    vertex, 1, protect.getHeight(), String.format("%dP", protectIndex++));
                elemMap.addElem(sideArea.getUuid(), sideArea);
                elemList.add(sideArea);
                sideArea.drawSideArea(this, mainFrame);
            }
        }
    }

    private CraneView craneView;

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
        LoadDao loadDao = new LoadDao(MainActivity.this);

        List<Crane> cranes = craneDao.selectAll();
        if (cranes == null || cranes.size() == 0) { // 初始状态, 创建表
            DatabaseHelper.getInstance(context).createTable(Crane.class);

            DatabaseHelper.getInstance(context).createTable(Area.class); // 区域

            DatabaseHelper.getInstance(context).createTable(Protect.class); // 保护区

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
        }

        calibration = calibrationDao.selectAll().get(0);

    }

    TextView angleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        angleView = (TextView) findViewById(R.id.angle);

        EventBus.getDefault().register(this);

        try {
            serialttyS0 = new SerialPort(new File("/dev/ttyS0"), 115200, 0); // 19200 // AD数据
            serialttyS1 = new SerialPort(new File("/dev/ttyS1"), 19200, 0);
            ttyS0InputStream = serialttyS0.getInputStream();
            ttyS0OutputStream = serialttyS0.getOutputStream();
            ttyS1InputStream = serialttyS1.getInputStream();
            ttyS1OutputStream = serialttyS1.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initTable(); // 初始化表
        startDataSimThread();
        startAdThread(); // 初始化串口线程
        startRadioThread();

        // 触发判断本机是否为主机
        new Handler().postDelayed(() -> {
            eventBus.post(new RadioEvent(radioProto.startMaster()));
            startRotateThread();
        }, 3000);
    }

}
