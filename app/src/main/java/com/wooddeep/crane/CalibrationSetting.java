package com.wooddeep.crane;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class CalibrationSetting extends AppCompatActivity {

    // 回转标定
    private float rotateStartX1 = 0.0f;
    private float rotateStartY1 = 0.0f;
    private float rotateStartData = 0.0f; //根据485传输的电压数据

    private float rotateEndX2 = 0.0f;
    private float rotateEndY2 = 0.0f;
    private float rotateEndData = 0.0f;   // 根据485传输的电压数据

    private float rotateRate = 0.1f;      // 角度差值 / (rotateEndData - rotateStartData)

    // 档位标定
    private float Gear1 = 0.1f;  // 一档

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

        ImageView closeBtn = (ImageView)findViewById(R.id.clock_calibration);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



}
