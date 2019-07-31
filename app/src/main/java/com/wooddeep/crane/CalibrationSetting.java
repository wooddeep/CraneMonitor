package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wooddeep.crane.comm.Protocol;
import com.wooddeep.crane.comm.RotateProto;
import com.wooddeep.crane.ebus.AlarmSetEvent;
import com.wooddeep.crane.ebus.CalibrationEvent;
import com.wooddeep.crane.ebus.MessageEvent;
import com.wooddeep.crane.ebus.RotateEvent;
import com.wooddeep.crane.ebus.SimulatorEvent;
import com.wooddeep.crane.ebus.UartEvent;
import com.wooddeep.crane.ebus.UserEvent;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.tookit.CommTool;
//import com.wooddeep.crane.tookit.Coordinate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


// https://locationtech.github.io/jts/javadoc/org/locationtech/jts/algorithm/Angle.html


//double degrees = 45.0;
//double radians = Math.toRadians(degrees);
//System.out.format("pi 的值为 %.4f%n", Math.PI);
//System.out.format("%.4f 的反正弦值为 %.4f 度 %n", Math.sin(radians), Math.toDegrees(Math.asin(Math.sin(radians))));
//Math.asin();

@SuppressWarnings("unused")
public class CalibrationSetting extends AppCompatActivity {

    abstract class CalibrationCell {
        public String dimValueName = null; // 对应的 维度起始值 名称
        public String uartDataName = null; // 对应的 uart起始值 名称

        public String rateValueName = null; // 对应的斜率值名称

        public int dimValueEditTextId = -1;  // 用于编辑纬度值输入框
        public int uartDataTextViewId = -1;  // 用于显示串口值文本框
        public int buttonId = -1;            // 触发获取串口值的按钮

        public int rateShowId = -1;  // 用于显示斜率值的TextView的ID

        public float dimValue; // 物理值，如高度，长度, 坐标值等
        public int uartData; // read from uart

        public float rateValue; // 最终计算的斜率值

        public boolean calcRate = false; // 点击按钮后是否计算斜率

        public CalibrationCell(String name, int dveti, int udtvi, int bi, int rsi) {
            this.dimValueName = name;
            this.dimValueEditTextId = dveti;
            this.uartDataTextViewId = udtvi;
            this.buttonId = bi;
            this.rateShowId = rsi;
        }

        public CalibrationCell(String name, String uartDataName, String rateValueName, int dveti, int udtvi, int bi, int rsi) {
            this.dimValueName = name;
            this.uartDataName = uartDataName;
            this.rateValueName = rateValueName;
            this.dimValueEditTextId = dveti;
            this.uartDataTextViewId = udtvi;
            this.buttonId = bi;
            this.rateShowId = rsi;
        }

        abstract public void setOnClickListener();

    }

    private MessageEvent gevent = null;

    // 订阅消息, 可以获取串口的数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerUartDataReceiver(MessageEvent event) {
        gevent = event;
    }

    private static Protocol packer = new Protocol();
    private static Protocol parser = new Protocol();

    private static RotateProto rotateProto = new RotateProto();


    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void commEventBus(UartEvent event) {
        try {
            byte[] data = event.data;
            parser.parse(data);
            //System.out.printf("## %d - %d - %d - %d\n", parser.getAmplitude(), parser.getHeight(), parser.getWeight(), parser.getWindSpeed());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float centerX = 0f;
    private static float centerY = 0f;

    // 定义处理串口数据的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RotateDateEventBus(RotateEvent event) {
        byte[] data = event.data;
        rotateProto.parse(data);
        centerX = event.centerX;
        centerY = event.centerY;
        //System.out.printf("## %d, x = %f, y = %f \n", rotateProto.getData(), event.centerX, event.centerY);

    }

    // 回转
    private CalibrationCell rotateStartX1 = new CalibrationCell("rotateStartX1", "rotateStartData", "rotateRate", R.id.etx1, R.id.tv_rotate_coord1, R.id.btn_rotate_coord1, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(rotateStartX1.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 串口数据
                    float start = rotateProto.getData();
                    TextView tvStart = (TextView) findViewById(rotateStartX1.uartDataTextViewId);
                    tvStart.setText(String.valueOf(start));
                    TextView tvEnd = (TextView) findViewById(rotateEndX2.uartDataTextViewId);
                    float end = Float.parseFloat(tvEnd.getText().toString());

                    // 用户设置的 首尾 两个 坐标点
                    EditText etStartX = (EditText) findViewById(rotateStartX1.dimValueEditTextId);
                    EditText etStartY = (EditText) findViewById(rotateStartY1.dimValueEditTextId);
                    float x1 = Float.parseFloat(etStartX.getText().toString());
                    float y1 = Float.parseFloat(etStartY.getText().toString());
                    EditText etEndX = (EditText) findViewById(rotateEndX2.dimValueEditTextId);
                    EditText etEndY = (EditText) findViewById(rotateEndY2.dimValueEditTextId);
                    float x2 = Float.parseFloat(etEndX.getText().toString());
                    float y2 = Float.parseFloat(etEndY.getText().toString());

                    double rotate = Angle.angleBetween(new org.locationtech.jts.geom.Coordinate(x1, y1),
                        new org.locationtech.jts.geom.Coordinate(centerX, centerY), new org.locationtech.jts.geom.Coordinate(x2, y2)); // [0 - pi]
                    System.out.println(Math.toDegrees(rotate));

                    float rate = (float)rotate / (end - start);

                    TextView tvRate = (TextView) findViewById(rotateStartX1.rateShowId);
                    tvRate.setText(String.valueOf(rate));

                    calibration.setRotateStartX1(x1);
                    calibration.setRotateStartY1(y1);
                    calibration.setRotateEndX2(x2);
                    calibration.setRotateEndY2(y2);
                    calibration.setRotateStartData(start);
                    calibration.setRotateEndData(end);
                    calibration.setRotateRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                }
            });
        }
    };

    private CalibrationCell rotateStartY1 = new CalibrationCell("rotateStartY1", "rotateStartData", "rotateRate", R.id.ety1, R.id.tv_rotate_coord1, R.id.btn_rotate_coord1, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {
        }
    };

    private CalibrationCell rotateEndX2 = new CalibrationCell("rotateEndX2", "rotateEndData", "rotateRate", R.id.etx2, R.id.tv_rotate_coord2, R.id.btn_rotate_coord2, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(rotateEndX2.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // 串口数据
                    float end = rotateProto.getData();
                    TextView tvStart = (TextView) findViewById(rotateStartX1.uartDataTextViewId);
                    float start = Float.parseFloat(tvStart.getText().toString());
                    System.out.printf("## %f- %f\n", start, end);
                    TextView tvEnd = (TextView) findViewById(rotateEndX2.uartDataTextViewId);
                    tvEnd.setText(String.valueOf(end));

                    // 用户设置的 首尾 两个 坐标点
                    EditText etStartX = (EditText) findViewById(rotateStartX1.dimValueEditTextId);
                    EditText etStartY = (EditText) findViewById(rotateStartY1.dimValueEditTextId);
                    float x1 = Float.parseFloat(etStartX.getText().toString());
                    float y1 = Float.parseFloat(etStartY.getText().toString());
                    EditText etEndX = (EditText) findViewById(rotateEndX2.dimValueEditTextId);
                    EditText etEndY = (EditText) findViewById(rotateEndY2.dimValueEditTextId);
                    float x2 = Float.parseFloat(etEndX.getText().toString());
                    float y2 = Float.parseFloat(etEndY.getText().toString());

                    double rotate = Angle.angleBetween(new org.locationtech.jts.geom.Coordinate(x1, y1),
                        new org.locationtech.jts.geom.Coordinate(centerX, centerY), new org.locationtech.jts.geom.Coordinate(x2, y2)); // [0 - pi]
                    System.out.println(Math.toDegrees(rotate));

                    float rate = (float)rotate / (end - start);

                    TextView tvRate = (TextView) findViewById(rotateStartX1.rateShowId);
                    tvRate.setText(String.valueOf(rate));

                    calibration.setRotateStartX1(x1);
                    calibration.setRotateStartY1(y1);
                    calibration.setRotateEndX2(x2);
                    calibration.setRotateEndY2(y2);
                    calibration.setRotateStartData(start);
                    calibration.setRotateEndData(end);
                    calibration.setRotateRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                }
            });
        }
    };

    private CalibrationCell rotateEndY2 = new CalibrationCell("rotateEndY2", "rotateEndData", "rotateRate", R.id.ety2, R.id.tv_rotate_coord2, R.id.btn_rotate_coord2, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // 档位标定
    private CalibrationCell GearRate1 = new CalibrationCell("GearRate1", -1, -1, R.id.btn_first_gear, R.id.tv_first_gear) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. 读取初始值
                    double start = 0;  // TODO 从串口读取值
                    System.out.println("## start value = " + start);
                    System.out.println(System.currentTimeMillis());
                    // 2. 延时 3 秒 再读值
                    Handler mHandler = new Handler();
                    Runnable readEnd = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("## end value = " + "??");
                            System.out.println(System.currentTimeMillis());
                            if (gevent != null) {
                                System.out.printf("### %s, %s \n", gevent.name, gevent.password);
                                TextView tv = (TextView) findViewById(GearRate1.rateShowId);
                                tv.setText("gear1");
                            }
                        }
                    };

                    mHandler.postDelayed(readEnd, 3000);
                }

            });
        }
    };

    private CalibrationCell GearRate2 = new CalibrationCell("GearRate2", -1, -1, R.id.btn_gear2, R.id.tv_gear2) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. 读取初始值
                    double start = 0;  // TODO 从串口读取值
                    System.out.println("## start value = " + start);
                    System.out.println(System.currentTimeMillis());
                    // 2. 延时 3 秒 再读值
                    Handler mHandler = new Handler();
                    Runnable readEnd = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("## end value = " + "??");
                            System.out.println(System.currentTimeMillis());
                            if (gevent != null) {
                                System.out.printf("### %s, %s \n", gevent.name, gevent.password);
                                TextView tv = (TextView) findViewById(GearRate2.rateShowId);
                                tv.setText("gear2");
                            }
                        }
                    };

                    mHandler.postDelayed(readEnd, 3000);
                }

            });
        }
    };

    private CalibrationCell GearRate3 = new CalibrationCell("GearRate3", -1, -1, R.id.btn_gear3, R.id.tv_gear3) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. 读取初始值
                    double start = 0;  // TODO 从串口读取值
                    System.out.println("## start value = " + start);
                    System.out.println(System.currentTimeMillis());
                    // 2. 延时 3 秒 再读值
                    Handler mHandler = new Handler();
                    Runnable readEnd = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("## end value = " + "??");
                            System.out.println(System.currentTimeMillis());
                            if (gevent != null) {
                                System.out.printf("### %s, %s \n", gevent.name, gevent.password);
                                TextView tv = (TextView) findViewById(GearRate3.rateShowId);
                                tv.setText("gear3");
                            }
                        }
                    };

                    mHandler.postDelayed(readEnd, 3000);
                }

            });
        }
    };

    private CalibrationCell GearRate4 = new CalibrationCell("GearRate4", -1, -1, R.id.btn_gear4, R.id.tv_gear4) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. 读取初始值
                    double start = 0;  // TODO 从串口读取值
                    System.out.println("## start value = " + start);
                    System.out.println(System.currentTimeMillis());
                    // 2. 延时 3 秒 再读值
                    Handler mHandler = new Handler();
                    Runnable readEnd = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("## end value = " + "??");
                            System.out.println(System.currentTimeMillis());
                            if (gevent != null) {
                                System.out.printf("### %s, %s \n", gevent.name, gevent.password);
                                TextView tv = (TextView) findViewById(GearRate4.rateShowId);
                                tv.setText("gear4");
                            }
                        }
                    };

                    mHandler.postDelayed(readEnd, 3000);
                }

            });
        }
    };

    private CalibrationCell GearRate5 = new CalibrationCell("GearRate5", -1, -1, R.id.btn_gear5, R.id.tv_gear5) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. 读取初始值
                    double start = 0;  // TODO 从串口读取值
                    System.out.println("## start value = " + start);
                    System.out.println(System.currentTimeMillis());
                    // 2. 延时 3 秒 再读值
                    Handler mHandler = new Handler();
                    Runnable readEnd = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("## end value = " + "??");
                            System.out.println(System.currentTimeMillis());
                            if (gevent != null) {
                                System.out.printf("### %s, %s \n", gevent.name, gevent.password);
                                TextView tv = (TextView) findViewById(GearRate5.rateShowId);
                                tv.setText("gear5");
                            }
                        }
                    };

                    mHandler.postDelayed(readEnd, 3000);
                }

            });
        }
    };

    // 倾角 ~ 幅度
    private CalibrationCell dipAngleStart = new CalibrationCell("dipAngleStart", "dipAngleStartData", "dipAngleRate", R.id.et_dip_angle_start, R.id.tv_dip_angle_start, R.id.btn_dip_angle_start, R.id.tv_dip_angle_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(dipAngleStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(dipAngleEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(dipAngleStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(dipAngleEnd.dimValueEditTextId);

                    double currUartData = parser.getAmplitude();

                    tvStart.setText(String.valueOf(currUartData));
                    float startUartData = Float.parseFloat(tvStart.getText().toString());
                    float endUartData = Float.parseFloat(tvEnd.getText().toString());

                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());

                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    float rate = deltaDimValue / deltaUartData;

                    calibration.setDipAngleStartData(startUartData);
                    calibration.setDipAngleEndData(endUartData);
                    calibration.setDipAngleStart(startDimValue);
                    calibration.setDipAngleEnd(endDimValue);
                    calibration.setDipAngleRate(rate);

                    TextView tvRateShow = (TextView) findViewById(dipAngleStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));

                    calibrationDao.update(calibration);

                    EventBus.getDefault().post(new CalibrationEvent(calibration));
                }
            });
        }
    };

    private CalibrationCell dipAngleEnd = new CalibrationCell("dipAngleEnd", "dipAngleEndData", "dipAngleRate", R.id.et_dip_angle_end, R.id.tv_dip_angle_end, R.id.btn_dip_angle_end, R.id.tv_dip_angle_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(dipAngleStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(dipAngleEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(dipAngleStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(dipAngleEnd.dimValueEditTextId);

                    double currUartData = parser.getAmplitude();

                    tvEnd.setText(String.valueOf(currUartData));

                    float startUartData = Float.parseFloat(tvStart.getText().toString()); // uart ???
                    float endUartData = Float.parseFloat(tvEnd.getText().toString()); // uart ???
                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());

                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    float rate = deltaDimValue / deltaUartData;

                    calibration.setDipAngleStartData(startUartData);
                    calibration.setDipAngleEndData(endUartData);
                    calibration.setDipAngleStart(startDimValue);
                    calibration.setDipAngleEnd(endDimValue);
                    calibration.setDipAngleRate(rate);

                    TextView tvRateShow = (TextView) findViewById(dipAngleStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));
                    calibrationDao.update(calibration);

                    EventBus.getDefault().post(new CalibrationEvent(calibration));
                }
            });
        }
    };

    // 重量
    private CalibrationCell weightStart = new CalibrationCell("weightStart", "weightStartData", "weightRate", R.id.et_weight_start, R.id.tv_weight_start, R.id.btn_weight_start, R.id.tv_weight_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(weightStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(weightEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(weightStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(weightEnd.dimValueEditTextId);

                    double currUartData = parser.getWeight();

                    tvStart.setText(String.valueOf(currUartData));

                    float startUartData = Float.parseFloat(tvStart.getText().toString());
                    float endUartData = Float.parseFloat(tvEnd.getText().toString());
                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());
                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    Float rate = deltaDimValue / deltaUartData;

                    calibration.setWeightStartData(startUartData);
                    calibration.setWeightEndData(endUartData);
                    calibration.setWeightStart(startDimValue);
                    calibration.setWeightEnd(endDimValue);
                    calibration.setWeightRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                    TextView tvRateShow = (TextView) findViewById(weightStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));

                }
            });
        }
    };

    private CalibrationCell weightEnd = new CalibrationCell("weightEnd", "weightEndData", "weightRate", R.id.et_weight_end, R.id.tv_weight_end, R.id.btn_weight_end, R.id.tv_weight_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(weightStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(weightEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(weightStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(weightEnd.dimValueEditTextId);

                    double currUartData = parser.getWeight();

                    tvEnd.setText(String.valueOf(currUartData));

                    float startUartData = Float.parseFloat(tvStart.getText().toString()); // uart ???
                    float endUartData = Float.parseFloat(tvEnd.getText().toString()); // uart ???
                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());
                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    float rate = deltaDimValue / deltaUartData;

                    calibration.setWeightStartData(startUartData);
                    calibration.setWeightEndData(endUartData);
                    calibration.setWeightStart(startDimValue);
                    calibration.setWeightEnd(endDimValue);
                    calibration.setWeightRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                    TextView tvRateShow = (TextView) findViewById(weightStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));

                }
            });
        }
    };

    // 长度
    private CalibrationCell lengthStart = new CalibrationCell("lengthStart", "lengthStartData", "lengthRate", R.id.et_arm_length_start, R.id.tv_arm_length_start, R.id.btn_arm_length_start, R.id.tv_arm_length_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(lengthStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(lengthEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(lengthStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(lengthEnd.dimValueEditTextId);

                    double currUartData = parser.getAmplitude();
                    tvStart.setText(String.valueOf(currUartData));

                    float startUartData = Float.parseFloat(tvStart.getText().toString()); // uart 读值
                    float endUartData = Float.parseFloat(tvEnd.getText().toString()); // uart 读值
                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());
                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    float rate = deltaDimValue / deltaUartData;

                    calibration.setLengthStartData(startUartData);
                    calibration.setLengthEndData(endUartData);
                    calibration.setLengthStart(startDimValue);
                    calibration.setLengthEnd(endDimValue);
                    calibration.setLengthRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                    TextView tvRateShow = (TextView) findViewById(lengthStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));

                }
            });
        }
    };

    private CalibrationCell lengthEnd = new CalibrationCell("lengthEnd", "lengthEndData", "lengthRate", R.id.et_arm_length_end, R.id.tv_arm_length_end, R.id.btn_arm_length_end, R.id.tv_arm_length_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(lengthStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(lengthEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(lengthStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(lengthEnd.dimValueEditTextId);

                    double currUartData = parser.getAmplitude();

                    tvEnd.setText(String.valueOf(currUartData));

                    float startUartData = Float.parseFloat(tvStart.getText().toString()); // uart 读值
                    float endUartData = Float.parseFloat(tvEnd.getText().toString()); // uart 读值
                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());
                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    float rate = deltaDimValue / deltaUartData;

                    calibration.setLengthStartData(startUartData);
                    calibration.setLengthEndData(endUartData);
                    calibration.setLengthStart(startDimValue);
                    calibration.setLengthEnd(endDimValue);
                    calibration.setLengthRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                    TextView tvRateShow = (TextView) findViewById(lengthStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));

                }
            });
        }
    };

    // 高度
    private CalibrationCell heightStart = new CalibrationCell("heightStart", "heightStartData", "heightRate", R.id.et_tower_height_start, R.id.tv_tower_height_start, R.id.btn_tower_height_start, R.id.tv_tower_height_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(heightStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(heightEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(heightStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(heightEnd.dimValueEditTextId);

                    double currUartData = parser.getHeight();
                    tvStart.setText(String.valueOf(currUartData));

                    float startUartData = Float.parseFloat(tvStart.getText().toString()); // uart 读值
                    float endUartData = Float.parseFloat(tvEnd.getText().toString()); // uart 读值
                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());
                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    float rate = deltaDimValue / deltaUartData;

                    calibration.setHeightStartData(startUartData);
                    calibration.setHeightEndData(endUartData);
                    calibration.setHeightStart(startDimValue);
                    calibration.setHeightEnd(endDimValue);
                    calibration.setHeightRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                    TextView tvRateShow = (TextView) findViewById(heightStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));

                }
            });
        }
    };

    private CalibrationCell heightEnd = new CalibrationCell("heightEnd", "heightEndData", "heightRate", R.id.et_tower_height_end, R.id.tv_tower_height_end, R.id.btn_tower_height_end, R.id.tv_tower_height_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tvStart = (TextView) findViewById(heightStart.uartDataTextViewId);
                    TextView tvEnd = (TextView) findViewById(heightEnd.uartDataTextViewId);

                    EditText etStart = (EditText) findViewById(heightStart.dimValueEditTextId);
                    EditText etEnd = (EditText) findViewById(heightEnd.dimValueEditTextId);

                    double currUartData = parser.getHeight();
                    tvEnd.setText(String.valueOf(currUartData));

                    float startUartData = Float.parseFloat(tvStart.getText().toString()); // uart 读值
                    float endUartData = Float.parseFloat(tvEnd.getText().toString()); // uart 读值
                    float startDimValue = Float.parseFloat(etStart.getText().toString());
                    float endDimValue = Float.parseFloat(etEnd.getText().toString());
                    float deltaUartData = endUartData - startUartData;
                    float deltaDimValue = endDimValue - startDimValue;
                    float rate = deltaDimValue / deltaUartData;

                    calibration.setHeightStartData(startUartData);
                    calibration.setHeightEndData(endUartData);
                    calibration.setHeightStart(startDimValue);
                    calibration.setHeightEnd(endDimValue);
                    calibration.setHeightRate(rate);
                    calibrationDao.update(calibration);
                    EventBus.getDefault().post(new CalibrationEvent(calibration));

                    TextView tvRateShow = (TextView) findViewById(heightStart.rateShowId);
                    tvRateShow.setText(String.valueOf(rate));

                }
            });
        }
    };


    private CalibrationCell[] ccs = new CalibrationCell[]{
        rotateStartX1, rotateStartY1, rotateEndX2, rotateEndY2, GearRate1, GearRate2, GearRate3, GearRate4, GearRate5, dipAngleStart, dipAngleEnd, weightStart, weightEnd, lengthStart, lengthEnd, heightStart, heightEnd
    };

    private Calibration calibration = new Calibration();
    private CalibrationDao calibrationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EventBus.getDefault().register(this);

        // start
        ((Button) findViewById(R.id.start_value)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SimulatorEvent(true, false, false, 3));
            }
        });
        // stop
        ((Button) findViewById(R.id.stop_value)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SimulatorEvent(false, true, false, 100));
            }
        });
        // running
        ((Button) findViewById(R.id.running)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SimulatorEvent(false, false, true, 3));
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        confLoad(getApplicationContext());
        setOnTouchListener();
    }

    private void confLoad(Context contex) {
        calibrationDao = new CalibrationDao(contex);
        DatabaseHelper.getInstance(contex).createTable(Calibration.class);
        List<Calibration> paras = calibrationDao.selectAll();
        if (paras == null || paras.size() == 0) {
            calibrationDao.insert(new Calibration(1.0f));
        }

        paras = calibrationDao.selectAll();
        if (paras.size() < 1) return;
        Calibration para = paras.get(0);
        calibration = para; // 从系统中导出配置

        for (int i = 0; i < ccs.length; i++) {
            CalibrationCell cell = ccs[i];
            cell.setOnClickListener();

            String dimName = cell.dimValueName;
            String rateName = cell.rateValueName;
            String dataName = cell.uartDataName;

            try {
                Field field = Calibration.class.getDeclaredField(dimName);
                field.setAccessible(true);
                Object value = field.get(para);

                EditText et = (EditText) findViewById(cell.dimValueEditTextId);
                if (et != null) {
                    et.setText(value.toString());
                }

                if (rateName != null) {
                    TextView tv = (TextView) findViewById(cell.rateShowId);
                    field = Calibration.class.getDeclaredField(rateName);
                    field.setAccessible(true);
                    value = field.get(para);
                    tv.setText(value.toString());
                }

                if (dataName != null) {
                    TextView tv = (TextView) findViewById(cell.uartDataTextViewId);
                    field = Calibration.class.getDeclaredField(dataName);
                    field.setAccessible(true);
                    value = field.get(para);
                    tv.setText(value.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void setOnTouchListener(View view) {
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

    private void setOnClickListener(View view) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.close_logo) {
                    finish();
                }

                if (view.getId() == R.id.save_logo) {
                    // TODO 保存数据
                    System.out.println("## save !!!!");
                }
            }
        };
        view.setOnClickListener(onClickListener);
    }

    private void setOnTouchListener() {
        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.close_logo));
            add((ImageView) findViewById(R.id.save_logo));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }

}
