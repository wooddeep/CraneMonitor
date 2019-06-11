package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

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

    /**
     * 普通dialog
     */
    public static void showAlterDialog(Activity activity) {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(activity);
        //alterDiaglog.setIcon(R.drawable.icon);//图标
        alterDiaglog.setTitle("塔基配置保存");//文字
        //alterDiaglog.setMessage("生存还是死亡");//提示消息

        //积极的选择
        alterDiaglog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(activity, "点击了生存", Toast.LENGTH_SHORT).show();
            }
        });
        //消极的选择
        alterDiaglog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(activity, "点击了死亡", Toast.LENGTH_SHORT).show();
            }
        });


        //显示
        alterDiaglog.show();
    }


}
