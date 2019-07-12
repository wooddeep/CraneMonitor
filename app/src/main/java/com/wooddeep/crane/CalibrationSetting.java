package com.wooddeep.crane;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wooddeep.crane.ebus.MessageEvent;
import com.wooddeep.crane.ebus.UserEvent;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.entity.Calibration;
//import com.wooddeep.crane.tookit.CommTool;
//import com.wooddeep.crane.tookit.Coordinate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;

import java.lang.reflect.Field;
import java.util.List;


// https://locationtech.github.io/jts/javadoc/org/locationtech/jts/algorithm/Angle.html


//double degrees = 45.0;
//double radians = Math.toRadians(degrees);
//System.out.format("pi ��ֵΪ %.4f%n", Math.PI);
//System.out.format("%.4f �ķ�����ֵΪ %.4f �� %n", Math.sin(radians), Math.toDegrees(Math.asin(Math.sin(radians))));
//Math.asin();

@SuppressWarnings("unused")
public class CalibrationSetting extends AppCompatActivity {

    abstract class CalibrationCell {
        public String dimValueName = null; // ��Ӧ�� ά����ʼֵ ����
        public String uartDataName = null; // ��Ӧ�� uart��ʼֵ ����

        public String rateValueName = null; // ��Ӧ��б��ֵ����

        public int dimValueEditTextId = -1;  // ���ڱ༭γ��ֵ�����
        public int uartDataTextViewId = -1;  // ������ʾ����ֵ�ı���
        public int buttonId = -1;            // ������ȡ����ֵ�İ�ť

        public int rateShowId = -1;  // ������ʾб��ֵ��TextView��ID

        public float dimValue; // ����ֵ����߶ȣ�����, ����ֵ��
        public int uartData; // read from uart

        public float rateValue; // ���ռ����б��ֵ

        public boolean calcRate = false; // �����ť���Ƿ����б��

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
    // ������Ϣ, ���Ի�ȡ���ڵ�����
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerUartDataReceiver(MessageEvent event) {
        gevent = event;
    }

    // ��ת
    private CalibrationCell rotateStartX1 = new CalibrationCell("rotateStartX1", "rotateStartData", "rotateRate", R.id.etx1, R.id.tv_rotate_coord1, R.id.btn_rotate_coord1, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(rotateStartX1.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float start = 100; // TODO �滻Ϊ�Ӵ��ڶ�ȡ����
                    TextView tvStart = (TextView) findViewById(rotateStartX1.uartDataTextViewId);
                    if (tvStart != null) {
                        tvStart.setText(String.valueOf(start));
                    }

                    TextView tvEnd = (TextView) findViewById(rotateEndX2.uartDataTextViewId);
                    float end = Float.parseFloat(tvEnd.getText().toString());

                    float dataDelta = end - start; // ��ֵ��deltaֵ
                    System.out.println("## data delta = " + dataDelta);

                    EditText etStartX = (EditText) findViewById(rotateStartX1.dimValueEditTextId);
                    EditText etStartY = (EditText) findViewById(rotateStartY1.dimValueEditTextId);
                    double x1 = Double.parseDouble(etStartX.getText().toString());
                    double y1 = Double.parseDouble(etStartY.getText().toString());
                    EditText etEndX = (EditText) findViewById(rotateEndX2.dimValueEditTextId);
                    EditText etEndY = (EditText) findViewById(rotateEndY2.dimValueEditTextId);

                    // Բ��
                    double x = 100;
                    double y = 100;

                    double x2 = Double.parseDouble(etEndX.getText().toString());
                    double y2 = Double.parseDouble(etEndY.getText().toString());

                    double rotate = Angle.angleBetween(new Coordinate(x1, y1), new Coordinate(x, y), new Coordinate(x2, y2)); // [0 - pi]
                    System.out.println(Math.toDegrees(rotate));
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
                    float start = 100; // TODO �滻Ϊ�Ӵ��ڶ�ȡ����
                    TextView tvStart = (TextView) findViewById(rotateStartX1.uartDataTextViewId);
                    if (tvStart != null) {
                        tvStart.setText(String.valueOf(start));
                    }

                    TextView tvEnd = (TextView) findViewById(rotateEndX2.uartDataTextViewId);
                    float end = Float.parseFloat(tvEnd.getText().toString());

                    float dataDelta = end - start; // ��ֵ��deltaֵ
                    System.out.println("## data delta = " + dataDelta);

                    EditText etStartX = (EditText) findViewById(rotateStartX1.dimValueEditTextId);
                    EditText etStartY = (EditText) findViewById(rotateStartY1.dimValueEditTextId);
                    double x1 = Double.parseDouble(etStartX.getText().toString());
                    double y1 = Double.parseDouble(etStartY.getText().toString());
                    EditText etEndX = (EditText) findViewById(rotateEndX2.dimValueEditTextId);
                    EditText etEndY = (EditText) findViewById(rotateEndY2.dimValueEditTextId);

                    // Բ��
                    double x = 100;
                    double y = 100;

                    double x2 = Double.parseDouble(etEndX.getText().toString());
                    double y2 = Double.parseDouble(etEndY.getText().toString());

                    double rotate = Angle.angleBetween(new Coordinate(x1, y1), new Coordinate(x, y), new Coordinate(x2, y2)); // [0 - pi]
                    System.out.println(Math.toDegrees(rotate));
                }
            });
        }
    };

    private CalibrationCell rotateEndY2 = new CalibrationCell("rotateEndY2", "rotateEndData", "rotateRate", R.id.ety2, R.id.tv_rotate_coord2, R.id.btn_rotate_coord2, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // ��λ�궨
    private CalibrationCell GearRate1 = new CalibrationCell("GearRate1", -1, -1, R.id.btn_first_gear, R.id.tv_first_gear) {
        @Override
        public void setOnClickListener() {
            Button btn = (Button) findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. ��ȡ��ʼֵ
                    double start = 0;  // TODO �Ӵ��ڶ�ȡֵ
                    System.out.println("## start value = " + start);
                    System.out.println(System.currentTimeMillis());
                    // 2. ��ʱ 3 �� �ٶ�ֵ
                    Handler mHandler = new Handler();
                    Runnable readEnd = new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("## end value = " + "??");
                            System.out.println(System.currentTimeMillis());
                            if (gevent != null) {
                                System.out.printf("### %s, %s \n", gevent.name, gevent.password);
                                TextView tv = (TextView)findViewById(GearRate1.rateShowId);
                                tv.setText(".......");
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

        }
    };

    private CalibrationCell GearRate3 = new CalibrationCell("GearRate3", -1, -1, R.id.btn_gear3, R.id.tv_gear3) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell GearRate4 = new CalibrationCell("GearRate4", -1, -1, R.id.btn_gear4, R.id.tv_gear4) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell GearRate5 = new CalibrationCell("GearRate5", -1, -1, R.id.btn_gear5, R.id.tv_gear5) {
        @Override
        public void setOnClickListener() {

        }
    };

    // ���
    private CalibrationCell dipAngleStart = new CalibrationCell("dipAngleStart", "dipAngleStartData", "dipAngleRate", R.id.et_dip_angle_start, R.id.tv_dip_angle_start, R.id.btn_dip_angle_start, R.id.tv_dip_angle_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell dipAngleEnd = new CalibrationCell("dipAngleEnd", "dipAngleEndData", "dipAngleRate", R.id.et_dip_angle_end, R.id.tv_dip_angle_end, R.id.btn_dip_angle_end, R.id.tv_dip_angle_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // ����
    private CalibrationCell weightStart = new CalibrationCell("weightStart", "weightStartData", "weightRate", R.id.et_weight_start, R.id.tv_weight_start, R.id.btn_weight_start, R.id.tv_weight_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell weightEnd = new CalibrationCell("weightEnd", "weightEndData", "weightRate", R.id.et_weight_end, R.id.tv_weight_end, R.id.btn_weight_end, R.id.tv_weight_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // ����
    private CalibrationCell lengthStart = new CalibrationCell("lengthStart", "lengthStartData", "lengthRate", R.id.et_arm_length_start, R.id.tv_arm_length_start, R.id.btn_arm_length_start, R.id.tv_arm_length_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell lengthEnd = new CalibrationCell("lengthEnd", "lengthEndData", "lengthRate", R.id.et_arm_length_end, R.id.tv_arm_length_end, R.id.btn_arm_length_end, R.id.tv_arm_length_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // �߶�
    private CalibrationCell heightStart = new CalibrationCell("heightStart", "heightStartData", "heightRate", R.id.et_tower_height_start, R.id.tv_tower_height_start, R.id.btn_tower_height_start, R.id.tv_tower_height_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell heightEnd = new CalibrationCell("heightEnd", "heightEndData", "heightRate", R.id.et_tower_height_end, R.id.tv_tower_height_end, R.id.btn_tower_height_end, R.id.tv_tower_height_rate) {
        @Override
        public void setOnClickListener() {

        }
    };


    private CalibrationCell[] ccs = new CalibrationCell[]{
        rotateStartX1, rotateStartY1, rotateEndX2, rotateEndY2, GearRate1, GearRate2, GearRate3, GearRate4, GearRate5, dipAngleStart, dipAngleEnd, weightStart, weightEnd, lengthStart, lengthEnd, heightStart, heightEnd
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EventBus.getDefault().register(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView closeBtn = (ImageView) findViewById(R.id.clock_calibration);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        confLoad(getApplicationContext());
    }

    private void confLoad(Context contex) {
        CalibrationDao dao = new CalibrationDao(contex);
        DatabaseHelper.getInstance(contex).createTable(Calibration.class);
        List<Calibration> paras = dao.selectAll();
        if (paras == null || paras.size() == 0) {
            dao.insert(new Calibration(1.0f));
        }

        paras = dao.selectAll();
        if (paras.size() < 1) return;
        Calibration para = paras.get(0);


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

}
