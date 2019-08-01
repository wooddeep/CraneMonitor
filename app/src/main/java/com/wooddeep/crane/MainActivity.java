package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.wooddeep.crane.comm.Protocol;
import com.wooddeep.crane.comm.RotateProto;
import com.wooddeep.crane.ebus.CalibrationEvent;
import com.wooddeep.crane.ebus.MessageEvent;
import com.wooddeep.crane.ebus.AlarmSetEvent;
import com.wooddeep.crane.ebus.RotateEvent;
import com.wooddeep.crane.ebus.SimulatorEvent;
import com.wooddeep.crane.ebus.UartEvent;
import com.wooddeep.crane.ebus.UserEvent;
import com.wooddeep.crane.element.CenterCycle;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


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

    private Context context;
    private static final String TAG = "MainActivity";
    private static final ElemMap elemMap = new ElemMap();

    private static HashMap<String, Object> viewMapBak = new HashMap<>();
    private static HashMap<String, Object> zoomMapBak = new HashMap<>();

    private float oscale = 1.0f;
    private String mainCycleId = null; // 主环的uuid
    private String sideCycleId = null;

    private static Protocol packer = new Protocol();
    private static Protocol parser = new Protocol();

    private static RotateProto rotateProto = new RotateProto();

    private int craneType = 0; // 塔基类型: 0 ~ 平臂式, 2动臂式
    private Crane mainCrane;   // 本塔基参数
    private CenterCycle centerCycle; // 本塔基圆环
    private AlarmSet alarmSet = null;
    private Calibration calibration = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimulatorFlags flags = new SimulatorFlags();
    private UartEmitter emitter = new UartEmitter();

    private EventBus eventBus = EventBus.getDefault();

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new MessageEvent(".", ".")); // 告警消息

                    if (flags.isStart() || flags.isStop()) {
                        packer.setAmplitude(emitter.getsAmplitude());
                        packer.setHeight(emitter.getsHeight());
                        packer.setWeight(emitter.getsWeight());
                        packer.setWindSpeed(emitter.getsWindSpeed());

                        rotateProto.setData(emitter.getsAmplitude()); // TODO 单独做回转数据的发射器, 现在借用幅度值的发射器
                    }

                    if (flags.isRuning()) {
                        packer.setAmplitude(emitter.getsAmplitude());
                        packer.setHeight(emitter.getsHeight());
                        packer.setWeight(emitter.getsWeight());
                        packer.setWindSpeed(emitter.getsWindSpeed());

                        rotateProto.setData(emitter.getsAmplitude()); // TODO 单独做回转数据的发射器, 现在借用幅度值的发射器

                        emitter.emitter(); // 累计
                    }

                    byte[] data = packer.pack();
                    eventBus.post(new UartEvent(data));
                    if (mainCrane != null) {
                        eventBus.post(new RotateEvent(rotateProto.pack(), mainCrane.getCoordX1(), mainCrane.getCoordY1()));
                    }
                }
            });
        }
    };

    public float getOscale() {
        return oscale;
    }

    public void setOscale(float oscale) {
        this.oscale = oscale;
    }

    private void startUartThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 01 04 00 01 00 04 A0 09
                    //SerialPort serialttyS0 = new SerialPort(new File("/dev/ttyS0"), 19200, 0); // 19200
                    //SerialPort serialttyS1 = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
                    //OutputStream ttyS0OutputStream = serialttyS0.getOutputStream();
                    //InputStream ttyS1InputStream = serialttyS1.getInputStream();
                    //ttyS0OutputStream.write("hello world".getBytes());
                    //ttyS0OutputStream.write(new byte[]{0x01, 0x04, 0x00, 0x01, 0x00, 0x02, 0x20, 0x0B});
                    //if (ttyS1InputStream.available() > 0) {
                    //    int len = ttyS1InputStream.read(in, 0, 1024);
                    //    byte[] real = Arrays.copyOf(in, len);
                    //    for (byte data : real) {
                    //        System.out.printf("%02x ", data);
                    //    }
                    //} else {
                    //    System.out.println("############# no data !############");
                    //}

                    // 485测试
                    SerialPort serialttyS2 = new SerialPort(new File("/dev/ttyS2"), 115200, 0);
                    SerialPort serialttyS3 = new SerialPort(new File("/dev/ttyS3"), 115200, 0);
                    OutputStream ttyS2OutputStream = serialttyS2.getOutputStream();
                    InputStream ttyS2InputStream = serialttyS2.getInputStream();
                    OutputStream ttyS3OutputStream = serialttyS3.getOutputStream();
                    InputStream ttyS3InputStream = serialttyS3.getInputStream();

                    byte[] in = new byte[1024];

                    while (true) {
                        ttyS2OutputStream.write("hello world".getBytes());
                        int len = ttyS3InputStream.read(in, 0, 1024);
                        byte[] real = Arrays.copyOf(in, len);
                        for (byte data : real) {
                            System.out.printf("%02x ", data);
                        }
                        System.out.println(new String(real));
                        try {
                            Thread.sleep(200);
                        } catch (Exception e) {

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        EventBus.getDefault().register(this);

        timer.schedule(task, 1000, 500); // 测试数据发生器
        initTable(); // 初始化表
        //startUartThread(); // 初始化串口线程
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
        //fanRotate();
        elemMap.alramFlink();
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    //@Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(UserEvent userEvent) {
        try {
            //EditText limitEditText = (EditText) findViewById(R.id.alarm_limit);
            //float limit = Float.parseFloat(limitEditText.getText().toString());
            float limit = 5;
            elemMap.alarmJudge(mainCycleId, limit); // 告警判断
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commEventBus(UartEvent uartEvent) {
        try {
            Date date = new Date();
            String dateNowStr = sdf.format(date);
            String[] cells = dateNowStr.split(" ");
            TextView currTime = (TextView) findViewById(R.id.currTime);
            TextView currDate = (TextView) findViewById(R.id.currDate);
            currDate.setText(cells[0]);
            currTime.setText(cells[1]);

            byte[] data = uartEvent.data;
            parser.parse(data);

            // 重量
            TextView weight = (TextView) findViewById(R.id.weight);
            float weightValue = calibration.getWeightStart() + calibration.getWeightRate() * (parser.getWeight() - calibration.getWeightStartData());
            weightValue = Math.round(weightValue * 10) / 10;
            weight.setText(String.valueOf(weightValue) + "t");

            // 小车位置(length) or 动臂幅度(amplitude)
            TextView armLength = (TextView) findViewById(R.id.length);
            float armLengthValue = calibration.getLengthStart() + calibration.getLengthRate() * (parser.getAmplitude() - calibration.getLengthStartData());
            armLengthValue = Math.round(armLengthValue * 10) / 10;
            armLength.setText(String.valueOf(armLengthValue) + "m");

            // 吊钩高度
            TextView height = (TextView) findViewById(R.id.height);
            float heightValue = calibration.getHeightStart() + calibration.getHeightRate() * (parser.getHeight() - calibration.getHeightStartData());
            heightValue = Math.round(heightValue * 10) / 10;
            height.setText(String.valueOf(heightValue) + "m");

            // 风速 TODO
            TextView windSpeed = (TextView) findViewById(R.id.wind_speed);
            //float windSpeedValue = calibration.getWeightStart() + calibration.getWeightRate() * (parser.getWeight() - calibration.getWeightStartData());
            //windSpeedValue = Math.round(windSpeedValue * 10) / 10;
            //windSpeed.setText(String.valueOf(parser.getWindSpeed()) + "m/s");

            if (mainCrane.getType() == 0) { // 平臂式
                float bigArmLength0 = mainCrane.getBigArmLength(); // 大臂总长度
                float bigArmLength1 = CraneView.maxArmLength - CraneView.minArmLength;

                float startCarRange = calibration.getLengthStart(); // 起始小车位置
                float endCarRange = calibration.getLengthEnd(); // 终止小车位置
                float valueDelat = endCarRange - startCarRange;
                float rate = valueDelat / bigArmLength0; // 距离差 和 大臂的比例 (圆环)
                float ratePerData0 = rate / (calibration.getLengthEndData() - calibration.getLengthStartData()); // 每一个Uart Data 和 大臂的比例
                float ratePerData1 = rate / (calibration.getLengthEndData() - calibration.getLengthStartData());
                float carRange0 = (parser.getAmplitude() - calibration.getLengthStartData()) * ratePerData0 * bigArmLength0; // 圆环的小车当前坐标
                float carRange1 = (parser.getAmplitude() - calibration.getLengthStartData()) * ratePerData1 * bigArmLength1 + CraneView.minArmLength;

                centerCycle.setCarRange(carRange0);
                centerCycle.setHAngle(rotateProto.getData()); // TODO 根据比例值计算角度


                craneView.setArmLenth((int)(carRange1));
                float craneHeight = mainCrane.getCraneHeight(); // 塔身高度
                float craneHeight1 = CraneView.maxHookHeight - CraneView.minHookHeight;
                float startHeight = calibration.getHeightStart(); // 吊钩起始位置
                float endHeight = calibration.getHeightEnd(); // 吊钩终止位置
                float heightDelat = endHeight - startHeight;
                float hrate = heightDelat / craneHeight;
                float hratePerData = hrate / (calibration.getHeightEndData() - calibration.getHeightStartData());
                float hookHeight = (parser.getHeight() - calibration.getHeightStartData()) * hratePerData * craneHeight1 + CraneView.minHookHeight;
                craneView.setHookHeight((int)hookHeight);


            }

            //weightAlarm();
            //System.out.println("## I have get uart0 data!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RotateDateEventBus(RotateEvent event) {
        try {

            byte[] data = event.data;
            rotateProto.parse(data);

            // 重量
            //TextView weight = (TextView) findViewById(R.id.weight);
            //float weightValue = calibration.getWeightStart() + calibration.getWeightRate() * (parser.getWeight() - calibration.getWeightStartData());

            if (mainCrane.getType() == 0) { // 平臂式
                /*
                float bigArmLength0 = mainCrane.getBigArmLength(); // 大臂总长度
                float bigArmLength1 = CraneView.maxArmLength - CraneView.minArmLength;

                float startCarRange = calibration.getLengthStart(); // 起始小车位置
                float endCarRange = calibration.getLengthEnd(); // 终止小车位置
                float valueDelat = endCarRange - startCarRange;
                float rate = valueDelat / bigArmLength0; // 距离差 和 大臂的比例 (圆环)
                float ratePerData0 = rate / (calibration.getLengthEndData() - calibration.getLengthStartData()); // 每一个Uart Data 和 大臂的比例
                float ratePerData1 = rate / (calibration.getLengthEndData() - calibration.getLengthStartData());
                float carRange0 = (parser.getAmplitude() - calibration.getLengthStartData()) * ratePerData0 * bigArmLength0; // 圆环的小车当前坐标
                float carRange1 = (parser.getAmplitude() - calibration.getLengthStartData()) * ratePerData1 * bigArmLength1 + CraneView.minArmLength;

                centerCycle.setCarRange(carRange0);
                craneView.setArmLenth((int)(carRange1));

                float craneHeight = mainCrane.getCraneHeight(); // 塔身高度
                float craneHeight1 = CraneView.maxHookHeight - CraneView.minHookHeight;
                float startHeight = calibration.getHeightStart(); // 吊钩起始位置
                float endHeight = calibration.getHeightEnd(); // 吊钩终止位置
                float heightDelat = endHeight - startHeight;
                float hrate = heightDelat / craneHeight;
                float hratePerData = hrate / (calibration.getHeightEndData() - calibration.getHeightStartData());
                float hookHeight = (parser.getHeight() - calibration.getHeightStartData()) * hratePerData * craneHeight1 + CraneView.minHookHeight;
                craneView.setHookHeight((int)hookHeight);
                */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    private void setAdjustButton() {
        /*
        findViewById(R.id.adjust_hangle_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 发送消息
                centerCycle.hAngleAdd(1);
                EventBus.getDefault().post(
                    new UserEvent("Mr.sorrow", "123456", centerCycle.getUuid(), null)
                );
            }
        });
        */
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

        // 画中心圆环
        centerCycle = new CenterCycle(oscale, mainCrane.getCoordX1(), mainCrane.getCoordY1(),
            mainCrane.getBigArmLength(), mainCrane.getBalancArmLength(), 0, 0, 100);
        elemMap.addElem(centerCycle.getUuid(), centerCycle);
        mainCycleId = centerCycle.getUuid();
        centerCycle.drawCenterCycle(this, mainFrame);

        // 根据数据库的数据画图
        for (Crane cp : paras) {
            if (cp == mainCrane) continue;
            float scale = centerCycle.scale;
            SideCycle sideCycle = new SideCycle(centerCycle, cp.getCoordX1(), cp.getCoordY1(), cp.getBigArmLength(),
                mainCrane.getBalancArmLength(), 0, 0, 10);

            elemMap.addElem(sideCycle.getUuid(), sideCycle);
            sideCycleId = sideCycle.getUuid();
            sideCycle.drawSideCycle(this, mainFrame);
        }

        // 区域
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
                SideArea sideArea = new SideArea(centerCycle, Color.rgb(19, 34, 122), vertex);
                elemMap.addElem(sideArea.getUuid(), sideArea);
                sideArea.drawSideArea(this, mainFrame);
            }

        }

        // 保护区
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
                SideArea sideArea = new SideArea(centerCycle, Color.rgb(212, 35, 122), vertex);
                elemMap.addElem(sideArea.getUuid(), sideArea);
                sideArea.drawSideArea(this, mainFrame, 1);
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

            DatabaseHelper.getInstance(context).createTable(Calibration.class); // 标定
            calibrationDao.insert(Calibration.getInitData());
        }

        alarmSet = alarmSetDao.selectAll().get(0);
        calibration = calibrationDao.selectAll().get(0);

    }


}
