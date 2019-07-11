package com.wooddeep.crane;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.entity.Calibration;

import java.lang.reflect.Field;
import java.util.List;


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
        /*{
            CalibrationCell cell = this;
            Button btn = (Button)findViewById(this.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tv = (TextView) findViewById(cell.uartDataTextViewId);
                    if (tv != null) {
                        //tv.setText("xxx"); // TODO 替换为从串口读取数据
                    }
                }
            });
        }*/

    }

    // 回转
    private CalibrationCell  rotateStartX1 =  new CalibrationCell("rotateStartX1", "rotateStartData", "rotateRate", R.id.etx1, R.id.tv_rotate_coord1, R.id.btn_rotate_coord1, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {
            //CalibrationCell cell = this;
            Button btn = (Button)findViewById(rotateStartX1.buttonId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float start = 100; // TODO 替换为从串口读取数据
                    TextView tvStart = (TextView) findViewById(rotateStartX1.uartDataTextViewId);
                    if (tvStart != null) {
                        tvStart.setText(String.valueOf(start));
                    }

                    TextView tvEnd = (TextView) findViewById(rotateEndX2.uartDataTextViewId);
                    float end = Float.parseFloat(tvEnd.getText().toString());

                    float dataDelta = end - start;

                    System.out.println("#### detaDelata = " + dataDelta);
                }
            });
        }
    };

    private CalibrationCell  rotateStartY1 =  new CalibrationCell("rotateStartY1", "rotateStartData", "rotateRate", R.id.ety1, R.id.tv_rotate_coord1, R.id.btn_rotate_coord1, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  rotateEndX2 =  new CalibrationCell("rotateEndX2", "rotateEndData", "rotateRate", R.id.etx2, R.id.tv_rotate_coord2, R.id.btn_rotate_coord2, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  rotateEndY2 =  new CalibrationCell("rotateEndY2", "rotateEndData", "rotateRate", R.id.ety2, R.id.tv_rotate_coord2, R.id.btn_rotate_coord2, R.id.tv_rotate_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // 档位标定
    private CalibrationCell  GearRate1 =  new CalibrationCell("GearRate1", -1, -1, R.id.btn_first_gear, R.id.tv_first_gear) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  GearRate2 =  new CalibrationCell("GearRate2", -1, -1, R.id.btn_gear2, R.id.tv_gear2) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  GearRate3 =  new CalibrationCell("GearRate3", -1, -1, R.id.btn_gear3, R.id.tv_gear3) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  GearRate4 =  new CalibrationCell("GearRate4", -1, -1, R.id.btn_gear4, R.id.tv_gear4) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  GearRate5 =  new CalibrationCell("GearRate5", -1, -1, R.id.btn_gear5, R.id.tv_gear5) {
        @Override
        public void setOnClickListener() {

        }
    };

    // 倾角
    private CalibrationCell  dipAngleStart =  new CalibrationCell("dipAngleStart", "dipAngleStartData", "dipAngleRate", R.id.et_dip_angle_start, R.id.tv_dip_angle_start, R.id.btn_dip_angle_start, R.id.tv_dip_angle_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  dipAngleEnd =  new CalibrationCell("dipAngleEnd", "dipAngleEndData", "dipAngleRate", R.id.et_dip_angle_end, R.id.tv_dip_angle_end, R.id.btn_dip_angle_end, R.id.tv_dip_angle_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // 重量
    private CalibrationCell  weightStart =  new CalibrationCell("weightStart", "weightStartData", "weightRate", R.id.et_weight_start, R.id.tv_weight_start, R.id.btn_weight_start, R.id.tv_weight_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  weightEnd =  new CalibrationCell("weightEnd", "weightEndData", "weightRate", R.id.et_weight_end, R.id.tv_weight_end, R.id.btn_weight_end, R.id.tv_weight_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // 长度
    private CalibrationCell  lengthStart =  new CalibrationCell("lengthStart", "lengthStartData", "lengthRate", R.id.et_arm_length_start, R.id.tv_arm_length_start, R.id.btn_arm_length_start, R.id.tv_arm_length_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  lengthEnd =  new CalibrationCell("lengthEnd", "lengthEndData", "lengthRate", R.id.et_arm_length_end, R.id.tv_arm_length_end, R.id.btn_arm_length_end, R.id.tv_arm_length_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    // 高度
    private CalibrationCell  heightStart =  new CalibrationCell("heightStart", "heightStartData", "heightRate", R.id.et_tower_height_start, R.id.tv_tower_height_start, R.id.btn_tower_height_start, R.id.tv_tower_height_rate) {
        @Override
        public void setOnClickListener() {

        }
    };

    private CalibrationCell  heightEnd =  new CalibrationCell("heightEnd", "heightEndData", "heightRate", R.id.et_tower_height_end, R.id.tv_tower_height_end, R.id.btn_tower_height_end, R.id.tv_tower_height_rate) {
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
