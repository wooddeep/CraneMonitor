package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.SuperCircleView;

public class DrawTool {

    public static void DrawCycle(Activity activity, ViewGroup parent) {
        Context context = activity.getApplicationContext();
        SuperCircleView cycle = new SuperCircleView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams(105, 105);
        paras.leftMargin = 400;
        paras.topMargin = 100;
        cycle.setLayoutParams(paras);
        parent.addView(cycle);
        cycle.setDefMinRadio(50);
        cycle.setmRingNormalColor(Color.GREEN);
        cycle.setBackgroundColor(0x00000000);
        cycle.setValue(100);
    }

}
