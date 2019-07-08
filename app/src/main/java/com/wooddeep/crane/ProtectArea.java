package com.wooddeep.crane;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class ProtectArea extends AppCompatActivity {
    private Context context;

    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.protect_setting);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        //LinearLayout craneSettingContainer = (LinearLayout) findViewById(R.id.area_setting_container);
        //int screenWidthPx = craneSettingContainer.getMeasuredWidth();
        //context = getApplicationContext();
        //screenWidth = DisplayUtil.px2dip(context, screenWidthPx); // 转换为dp
        //List<Area> paras = confLoad(context);
        //paraTableRender(paras);

        //setOnTouchListener();

    }
}
