package com.wooddeep.crane;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CalibrationSetting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calibration_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
