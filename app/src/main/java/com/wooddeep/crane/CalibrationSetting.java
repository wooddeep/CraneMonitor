package com.wooddeep.crane;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.AreaDao;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Calibration;

import java.lang.reflect.Field;
import java.util.List;

import static org.joor.Reflect.on;

public class CalibrationSetting extends AppCompatActivity {

    class CalibrationCell {
        public String dimValueName; // 对应的熟悉值
        public String uartDataName;
        public int dimValueEditTextId;  // 用于编辑纬度值输入框
        public int uartDataTextViewId;  // 用于显示串口值文本框
        public int buttonId;            // 触发获取串口值的按钮
        public int rateShowId;  // 用于显示斜率值的TextView的ID
        public float dimValue; // 物理值，如高度，长度, 坐标值等
        public int uartData; // read from uart

        public CalibrationCell(String name, int dveti, int udtvi, int bi, int rsi) {
            this.dimValueName = name;
            this.dimValueEditTextId = dveti;
            this.uartDataTextViewId = udtvi;
            this.buttonId = bi;
            this.rateShowId = rsi;
        }
    }

    private CalibrationCell[] ccs = new CalibrationCell[]{

        // 回转标定
        new CalibrationCell("rotateStartX1", R.id.etx1, R.id.tv_rotate_coord1, R.id.btn_rotate_coord1, R.id.tv_rotate_rate),
        new CalibrationCell("rotateStartY1", R.id.ety1, R.id.tv_rotate_coord1, R.id.btn_rotate_coord1, R.id.tv_rotate_rate),
        new CalibrationCell("rotateEndX2", R.id.etx2, R.id.tv_rotate_coord2, R.id.btn_rotate_coord2, R.id.tv_rotate_rate),
        new CalibrationCell("rotateEndY2", R.id.ety2, R.id.tv_rotate_coord2, R.id.btn_rotate_coord2, R.id.tv_rotate_rate),

        // 档位标定
        new CalibrationCell("GearRate1", -1, -1, R.id.btn_first_gear, R.id.tv_first_gear),
        new CalibrationCell("GearRate2", -1, -1, R.id.btn_gear2, R.id.tv_gear2),
        new CalibrationCell("GearRate3", -1, -1, R.id.btn_gear3, R.id.tv_gear3),
        new CalibrationCell("GearRate4", -1, -1, R.id.btn_gear4, R.id.tv_gear4),
        new CalibrationCell("GearRate5", -1, -1, R.id.btn_gear5, R.id.tv_gear5),

        // 倾角
        new CalibrationCell("dipAngleStart", R.id.et_dip_angle_start, R.id.tv_dip_angle_start, R.id.btn_dip_angle_start, R.id.tv_dip_angle_rate),
        new CalibrationCell("dipAngleEnd", R.id.et_dip_angle_end, R.id.tv_dip_angle_end, R.id.btn_dip_angle_end, R.id.tv_dip_angle_rate),

        // 重量
        new CalibrationCell("weightStart", R.id.et_weight_start, R.id.tv_weight_start, R.id.btn_weight_start, R.id.tv_weight_rate),
        new CalibrationCell("weightEnd", R.id.et_weight_end, R.id.tv_weight_end, R.id.btn_weight_end, R.id.tv_weight_rate),

        // 长度
        new CalibrationCell("lengthStart", R.id.et_arm_length_start, R.id.tv_arm_length_start, R.id.btn_arm_length_start, R.id.tv_arm_length_rate),
        new CalibrationCell("lengthEnd", R.id.et_arm_length_end, R.id.tv_arm_length_end, R.id.btn_arm_length_end, R.id.tv_arm_length_rate),

        // 高度
        new CalibrationCell("heightStart", R.id.et_tower_height_start, R.id.tv_tower_height_start, R.id.btn_tower_height_start, R.id.tv_tower_height_rate),
        new CalibrationCell("heightEnd", R.id.et_tower_height_end, R.id.tv_tower_height_end, R.id.btn_tower_height_end, R.id.tv_tower_height_rate),
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
            String dimName = cell.dimValueName;
            String fisrtChar = dimName.substring(0, 1); // 头一个字符
            String leftString = dimName.substring(1, dimName.length()); // 剩下的字符串
            String methodName = "get" + fisrtChar.toUpperCase() + leftString;
            //Object value = on(para).call(methodName).get();
            //System.out.printf("## %s: %s\n", dimName, value);
            //Field[] fields = CalibrationCell.class.getDeclaredFields();
            try {
                //Field field = CalibrationCell.class.getField(dimName);
                Field field = Calibration.class.getDeclaredField(dimName);
                field.setAccessible(true);
                Object value = field.get(para);
                System.out.printf("## %s: %s\n", dimName, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
