package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.alertview.AlertView;
import com.wooddeep.crane.R;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.views.GridLineView;
import com.wooddeep.crane.views.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
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

    public static HashMap<String, Object> drawMenu(Activity activity, ViewGroup parent) {
        HashMap<String, Object> viewMap = new HashMap<>();
        Context context = activity.getApplicationContext();

        LinearLayout container = new LinearLayout(activity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 50);
        layoutParams.setMargins(5, 0, 0, 5);         //设置边距
        layoutParams.gravity = Gravity.BOTTOM; //  android:layout_gravity

        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setLayoutParams(layoutParams); //将以上的属性赋给LinearLayout
        container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.frame_border));

        // 菜单图片按钮
        ImageView menuOn = new ImageView(activity);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(50, 30);
        ivParams.gravity = Gravity.CENTER;
        menuOn.setLayoutParams(ivParams);
        //imageView.setBackgroundDrawable(activity.getResources().getDrawable(R.mipmap.menu_on)); // 设置背景
        menuOn.setImageResource(R.mipmap.menu_on);
        container.addView(menuOn);
        viewMap.put("R.id.menu", menuOn);

        // 菜单内部容器
        LinearLayout icontainer = new LinearLayout(activity);
        icontainer.setVisibility(View.GONE);
        LinearLayout.LayoutParams ilayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 50);
        ilayoutParams.gravity = Gravity.BOTTOM; //  android:layout_gravity
        icontainer.setOrientation(LinearLayout.HORIZONTAL);
        icontainer.setLayoutParams(ilayoutParams); //将以上的属性赋给LinearLayout
        viewMap.put("R.id.menu_expand", icontainer);

        // 菜单图片按钮
        // 塔基设置
        ImageView craneSetting = new ImageView(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 40);
        params.gravity = Gravity.CENTER;
        craneSetting.setLayoutParams(params);
        craneSetting.setImageResource(R.mipmap.crane_setting);
        icontainer.addView(craneSetting);
        viewMap.put("R.id.crane_setting", craneSetting);

        // 区域设置
        ImageView areaSetting = new ImageView(activity);
        params = new LinearLayout.LayoutParams(50, 45);
        params.setMargins(15, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        areaSetting.setLayoutParams(params);
        areaSetting.setImageResource(R.mipmap.area_setting);
        icontainer.addView(areaSetting);
        viewMap.put("R.id.area_setting", areaSetting);

        // 保护区域设置
        ImageView protectAreaSetting = new ImageView(activity);
        params = new LinearLayout.LayoutParams(45, 40);
        params.setMargins(15, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        protectAreaSetting.setLayoutParams(params);
        protectAreaSetting.setImageResource(R.mipmap.protect_area_log);
        icontainer.addView(protectAreaSetting);
        viewMap.put("R.id.protect_area_setting", protectAreaSetting);

        // 标定设置
        ImageView carlibrationSetting = new ImageView(activity);
        params = new LinearLayout.LayoutParams(40, 35);
        params.setMargins(15, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        carlibrationSetting.setLayoutParams(params);
        carlibrationSetting.setImageResource(R.mipmap.calibration_setting);
        icontainer.addView(carlibrationSetting);
        viewMap.put("R.id.calibration_setting", carlibrationSetting);

        // 告警设置
        ImageView alarmSetting = new ImageView(activity);
        params = new LinearLayout.LayoutParams(45, 40);
        params.setMargins(15, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        alarmSetting.setLayoutParams(params);
        alarmSetting.setImageResource(R.mipmap.alarm_setting);
        icontainer.addView(alarmSetting);
        viewMap.put("R.id.alarm_setting", alarmSetting);

        // 告警设置
        ImageView loadAttribute = new ImageView(activity);
        params = new LinearLayout.LayoutParams(40, 35);
        params.setMargins(15, 0, 5, 0);
        params.gravity = Gravity.CENTER;
        loadAttribute.setLayoutParams(params);
        loadAttribute.setImageResource(R.mipmap.load_attr);
        icontainer.addView(loadAttribute);
        viewMap.put("R.id.load_attribute", loadAttribute);

        container.addView(icontainer);
        parent.addView(container);

        System.out.println("### draw menu!!!");

        return viewMap;
    }

    public static void eraseMenu(Activity activity, ViewGroup parent, HashMap<String, Object> map) {
        LinearLayout icontainer = (LinearLayout) map.get("R.id.menu_expand");
        //ImageView craneSetting = (ImageView)map.get("R.id.crane_setting");
        //ImageView areaSetting = (ImageView)map.get("R.id.area_setting");
        //ImageView carlibrationSetting = (ImageView)map.get("R.id.calibration_setting");
        //ImageView alarmSetting = (ImageView)map.get("R.id.alarm_setting");
        icontainer.removeAllViews();
        parent.removeView(icontainer);
        ImageView menu = (ImageView) map.get("R.id.menu");
        parent.removeView(menu);
    }

    public static HashMap<String, Object> drawZoom(Activity activity, ViewGroup parent) {
        HashMap<String, Object> viewMap = new HashMap<>();
        Context context = activity.getApplicationContext();

        LinearLayout container = new LinearLayout(activity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(50, 100);
        layoutParams.setMargins(0, 0, 5, 0);         //设置边距
        layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT; //  android:layout_gravity
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(layoutParams); //将以上的属性赋给LinearLayout
        container.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.frame_border));

        // zoomin图片按钮
        ImageView zoomIn = new ImageView(activity);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(40, 50);
        ivParams.gravity = Gravity.CENTER;
        zoomIn.setLayoutParams(ivParams);
        //imageView.setBackgroundDrawable(activity.getResources().getDrawable(R.mipmap.menu_on)); // 设置背景
        zoomIn.setImageResource(R.mipmap.zoom_in);
        container.addView(zoomIn);
        viewMap.put("R.id.zoom_in", zoomIn);

        ImageView zoomOut = new ImageView(activity);
        zoomOut.setLayoutParams(ivParams);
        //imageView.setBackgroundDrawable(activity.getResources().getDrawable(R.mipmap.menu_on)); // 设置背景
        zoomOut.setImageResource(R.mipmap.zoom_out);
        container.addView(zoomOut);
        viewMap.put("R.id.zoom_out", zoomOut);

        parent.addView(container);
        System.out.println("### draw zoom!!!");

        viewMap.put("R.id.zoom_container", container);

        return viewMap;
    }

    public static void eraseZoom(Activity activity, ViewGroup parent, HashMap<String, Object> map) {
        LinearLayout icontainer = (LinearLayout) map.get("R.id.zoom_container");
        icontainer.removeAllViews();
        parent.removeView(icontainer);
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

    /**
     * 普通dialog
     */
    public static void showExportDialog(Activity activity) {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(activity);
        //alterDiaglog.setIcon(R.drawable.icon);//图标
        alterDiaglog.setTitle("日志导出成功(export log database success!)");//文字
        //alterDiaglog.setMessage("生存还是死亡");//提示消息

        //积极的选择
        alterDiaglog.setPositiveButton("确认(confirm)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(activity, "点击了生存", Toast.LENGTH_SHORT).show();
            }
        });
        //消极的选择
        alterDiaglog.setNegativeButton("取消(cancel)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(activity, "点击了死亡", Toast.LENGTH_SHORT).show();
            }
        });


        //显示
        alterDiaglog.show();
    }

    /**
     * 普通dialog
     */
    public static void showImportDialog(Activity activity, boolean ok) {
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(activity);
        //alterDiaglog.setIcon(R.drawable.icon);//图标
        if (ok) {
            alterDiaglog.setTitle("负荷特性导入成功(import load feature success!)");//文字
        } else {
            alterDiaglog.setTitle("负荷特性导入失败(import load feature fail!)");//文字
        }
        //alterDiaglog.setMessage("生存还是死亡");//提示消息

        //积极的选择
        alterDiaglog.setPositiveButton("确认(confirm)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(activity, "点击了生存", Toast.LENGTH_SHORT).show();
            }
        });
        //消极的选择
        alterDiaglog.setNegativeButton("取消(cancel)", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(activity, "点击了死亡", Toast.LENGTH_SHORT).show();
            }
        });


        //显示
        alterDiaglog.show();
    }

    public static void showPwDialog(Activity activity) {
        AlertView alertView = new AlertView("请输入密码(password)", "确定保存(save)?", null,
            new String[]{"确定(confirm)", "取消(cancel)"}, null, activity,
            AlertView.Style.Alert, (o, position) -> {
            if (position == 0) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@");
            }
        });
        alertView.show();
    }

}
