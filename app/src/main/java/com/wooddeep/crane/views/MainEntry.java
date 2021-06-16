package com.wooddeep.crane.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.example.x6.serialportlib.SerialPort;
import com.wooddeep.crane.R;
import com.wooddeep.crane.comm.ControlProto;
import com.wooddeep.crane.comm.Protocol;
import com.wooddeep.crane.comm.RadioProto;
import com.wooddeep.crane.comm.RotateProto;
import com.wooddeep.crane.ebus.AlarmDetectEvent;
import com.wooddeep.crane.ebus.AlarmEvent;
import com.wooddeep.crane.ebus.AlarmSetEvent;
import com.wooddeep.crane.ebus.CalibrationCloseEvent;
import com.wooddeep.crane.ebus.CalibrationEvent;
import com.wooddeep.crane.ebus.HeightEvent;
import com.wooddeep.crane.ebus.LengthEvent;
import com.wooddeep.crane.ebus.RadioEvent;
import com.wooddeep.crane.ebus.RotateEvent;
import com.wooddeep.crane.ebus.SysParaEvent;
import com.wooddeep.crane.ebus.UartEvent;
import com.wooddeep.crane.ebus.WeightEvent;
import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.CycleElem;
import com.wooddeep.crane.element.ElemMap;
import com.wooddeep.crane.main.SavedData;
import com.wooddeep.crane.main.ShowData;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.LogDbHelper;
import com.wooddeep.crane.persist.dao.AlarmSetDao;
import com.wooddeep.crane.persist.dao.AreaDao;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.CraneParaDao;
import com.wooddeep.crane.persist.dao.ProtectAreaDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.dao.TcParamDao;
import com.wooddeep.crane.persist.dao.log.CtrlRecDao;
import com.wooddeep.crane.persist.dao.log.RealDataDao;
import com.wooddeep.crane.persist.dao.log.SwitchRecDao;
import com.wooddeep.crane.persist.dao.log.WorkRecDao;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.CranePara;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.persist.entity.TcParam;
import com.wooddeep.crane.persist.entity.log.CaliRec;
import com.wooddeep.crane.persist.entity.log.CtrlRec;
import com.wooddeep.crane.persist.entity.log.RealData;
import com.wooddeep.crane.persist.entity.log.SwitchRec;
import com.wooddeep.crane.persist.entity.log.WorkRecrod;
import com.wooddeep.crane.simulator.SimulatorFlags;
import com.wooddeep.crane.simulator.UartEmitter;
import com.wooddeep.crane.tookit.CommTool;
import com.wooddeep.crane.tookit.DataUtil;
import com.wooddeep.crane.tookit.MathTool;
import com.wooddeep.crane.tookit.MomentOut;
import com.wooddeep.crane.tookit.SysTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//import androidx.annotation.RequiresApi;

// adb shell am start com.android.settings/com.android.settings.Settings

// 启动mumu之后, 输入：
// adb connect 127.0.0.1:7555
// 然后再调试, 就ok了


@SuppressWarnings("unused")
public class MainEntry {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String TAG = "MainActivity";
    //private volatile Activity activity;
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

    public static volatile boolean sysExit = false;
    private Intent intent = null;

    private PackageManager mPackageManager;
    private DataUtil dataUtil = new DataUtil();

    public static ShowData showData = new ShowData();
    public static AtomicInteger alarmLevel = new AtomicInteger(100);
    public Float currXAngle = 0.0f;
    public static AtomicBoolean registered = new AtomicBoolean(false);

    public static AtomicBoolean channelOps = new AtomicBoolean(false);


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
                            }
                        } else { // 平臂式
                            if (Math.abs(currProto.getRealLength() - prevProto.getRealLength()) > 0.05f) {
                                lengthEvent.setLength(currProto.getRealLength());
                                prevProto.setRealLength(currProto.getRealLength());
                                shadowLength = currProto.getRealLength();
                                eventBus.post(lengthEvent);
                                MomentOut moment = MathTool.momentCalc(loadParas, currProto.getRealWeight(), currProto.getRealLength());

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
                                //runOnUiThread(() -> momentShow(moment.moment));
                            } else {
                                MomentOut moment = MathTool.momentCalc(loadParas, currProto.getRealWeight(), currProto.getRealLength());
                                //runOnUiThread(() -> momentShow(moment.moment));
                            }
                        }

                        if (Math.abs(currProto.getWindSpeed() - prevProto.getWindSpeed()) >= 17) { // 风速
                            prevProto.setWindSpeed(currProto.getWindSpeed());
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

                            currXAngle = (float) xangle;

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
                            }
                        }

                        if (calibrationFlag) {
                            eventBus.post(rotateEvent);
                        }
                    }

                    CommTool.sleep(100);
                    counter++;

                } catch (IOException ioe) {
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }).start();
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

        /*
        LoadDbHelper.reopen(context); // 重新打开
        tcParamDao = new TcParamDao(getApplicationContext());

        loadParas = tcParamDao.getLoads(craneType, armLength, power);
        if (loadParas != null && loadParas.size() > 0) {
            ((TextView) findViewById(R.id.rated_weight)).setText(loadParas.get(0).getWeight() + "t"); // 显示塔基类型
            for (TcParam load : loadParas) {
                System.out.printf("%s--%s\n", load.getCoordinate(), load.getWeight());
            }
        }
        */

        SysPara rvc = paraDao.queryParaByName("rvc");
        if (rvc == null) {
            rvc = new SysPara("rvc", "false");
            paraDao.insert(rvc);
        }

        isRvcMode.set(Boolean.parseBoolean(paraDao.queryValueByName("rvc")));
    }

    private List<CranePara> confLoad(Context contex) {
        CraneParaDao dao = new CraneParaDao(contex);
        List<CranePara> paras = dao.getAllCranePara();
        return paras;
    }

    public final static int REQUEST_READ_PHONE_STATE = 1;


    // 初始化数据
    private void initTable(Activity activity) {
        File dbFile = new File("/data/data/com.wooddeep.crane/databases/crane.db");
        if (!dbFile.exists()) {
            SysTool.copyFilesFromRaw(activity, R.raw.tc, "tc.db", "/data/data/com.wooddeep.crane/databases");
            SysTool.copyFilesFromRaw(activity, R.raw.crane, "crane.db", "/data/data/com.wooddeep.crane/databases");
            SysTool.copyFilesFromRaw(activity, R.raw.crane, "crane.db", "/data/data/com.wooddeep.crane/databases");
        }

        CraneDao craneDao = new CraneDao(activity);
        AreaDao areaDao = new AreaDao(activity);
        ProtectAreaDao protectAreaDao = new ProtectAreaDao(activity);
        AlarmSetDao alarmSetDao = new AlarmSetDao(activity);
        CalibrationDao calibrationDao = new CalibrationDao(activity);
        tcParamDao = new TcParamDao(activity);
        realDataDao = new RealDataDao(activity);
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

    public void onCreate(Activity activity) {
        SysTool.sysScriptInit(context);
        controlProto.clear();
        EventBus.getDefault().register(this);
        initTable(activity); // 初始化表
        paraDao = new SysParaDao(activity); // 系统参数
        switchRecDao = new SwitchRecDao(context); // 开关机
        ctrlRecDao = new CtrlRecDao(context);
        workRecDao = new WorkRecDao(context);
        eventBus.post(new SysParaEvent()); // 触发系统参数相关

        // 串口设置: 8N1,一个起始位,8个数据位,一个停止位

        try {
            String s0Name = "S1"; // AD
            String s1Name = "S2"; // 电台

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
                "S3", 19200, 8, 1, 'e', true); // 编码
            ttyS2OutputStream = serialttyS2.getOutputStream();
            ttyS2InputStream = serialttyS2.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 触发判断本机是否为主机
        new Handler().postDelayed(() -> {
            startSensorThread(); // 初始化串口线程
        }, 10);
    }

}
