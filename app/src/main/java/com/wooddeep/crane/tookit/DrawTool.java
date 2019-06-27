package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wooddeep.crane.R;
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

    public static void drawMenu(Activity activity, ViewGroup parent) {
        Context context = activity.getApplicationContext();

        LinearLayout container = new LinearLayout(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 50);
        layoutParams.setMargins(5, 0, 0, 60);         //设置边距
        layoutParams.gravity = Gravity.BOTTOM; //  android:layout_gravity

        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(layoutParams); //将以上的属性赋给LinearLayout

        /*
        <ImageView
        android:id="@+id/menu"
        android:layout_width="50dp"
        android:layout_height="30dp"
        android:layout_gravity="center"
        android:gravity="center"
        android:src="@mipmap/menu_on" />
        */

        ImageView imageView = new ImageView(activity);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(50, 30);
        ivParams.gravity = Gravity.CENTER;
        imageView.setLayoutParams(ivParams);

        imageView.setBackgroundDrawable(activity.getResources().getDrawable(R.mipmap.menu_on));

        container.addView(imageView);
        parent.addView(container);


//        //实例化一个TextView
//        TextView tv = new TextView(this);
//        //设置宽高以及权重
//        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//        //设置textview垂直居中
//        tvParams.gravity = Gravity.CENTER_VERTICAL;
//        tv.setLayoutParams(tvParams);
//        tv.setTextSize(14);
//        tv.setTextColor(getResources().getColor(R.color.rbtn_tet));
//        tv.setText(initMissionList().get(i).toString().trim());
//
//        RadioGroup radioGroup = new RadioGroup(this);
//        radioGroup.setLayoutParams(new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 120));
//        radioGroup.setOrientation(0);
//
//        RadioGroup.LayoutParams rbtnParams = new RadioGroup.LayoutParams(72, 72);
//        rbtnParams.gravity=Gravity.CENTER_VERTICAL;
//
//        RadioGroup.LayoutParams rbtnParamsf = new RadioGroup.LayoutParams(72, 72);
//        rbtnParamsf.gravity=Gravity.CENTER_VERTICAL;
//        rbtnParamsf.leftMargin=84;
//        rbtnParamsf.rightMargin=90;
//
//        final RadioButton radioButtonF = new RadioButton(this);
//        radioButtonF.setLayoutParams(rbtnParamsf);
//        radioButtonF.setButtonDrawable(android.R.color.transparent);
//        radioButtonF.setBackground(getResources().getDrawable(R.drawable.selector_fmhp_radiobutton_x_style));
//
//        final RadioButton radioButtonT = new RadioButton(this);
//        radioButtonT.setLayoutParams(rbtnParams);
//        radioButtonT.setButtonDrawable(android.R.color.transparent);
//        radioButtonT.setBackground(getResources().getDrawable(R.drawable.selector_fmhp_radiobutton_hook_style));
//
//        radioGroup.addView(radioButtonF);
//        radioGroup.addView(radioButtonT);
//
//        linearLayout.addView(tv);
//        linearLayout.addView(radioGroup);
//
//        llFmhpMissionList.addView(linearLayout);


        /*
        int width = parent.getMeasuredWidth(); // 获取组件宽度
        int height = parent.getMeasuredHeight(); // 获取组件高度
        GridLineView gridLineView = new GridLineView(context);
        FrameLayout.LayoutParams paras = new FrameLayout.LayoutParams((int) width, (int) height);

        gridLineView.setLayoutParams(paras);
        gridLineView.setWidthSize(width);
        gridLineView.setHeighSize(height);
        parent.addView(gridLineView);
        */
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
