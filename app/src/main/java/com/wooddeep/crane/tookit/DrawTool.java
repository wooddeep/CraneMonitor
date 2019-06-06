package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wooddeep.crane.views.GridLineView;
import com.wooddeep.crane.views.Polygon;
import com.wooddeep.crane.views.SuperCircleView;
import com.wooddeep.crane.views.Vertex;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class DrawTool {

    public static void drawGrid(Activity activity, ViewGroup parent) {
        Context context = activity.getApplicationContext();
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度
        GridLineView gridLineView = new GridLineView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams((int) width, (int) height);
        gridLineView.setLayoutParams(paras);
        gridLineView.setWidthSize(width);
        gridLineView.setHeighSize(height);
        parent.addView(gridLineView);
    }

}
