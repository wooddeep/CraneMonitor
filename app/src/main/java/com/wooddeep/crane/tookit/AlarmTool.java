package com.wooddeep.crane.tookit;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.widget.ImageView;

import com.wooddeep.crane.R;

/**
 * Created by niuto on 2019/7/30.
 */

public class AlarmTool {

    // https://www.cnblogs.com/yongdaimi/p/7943226.html 控件动画
    public void weightAlarm(Activity activity) {

        ImageView weight = (ImageView) activity.findViewById(R.id.weight_logo);

        ObjectAnimator oa = ObjectAnimator.ofFloat(weight, "scaleX", 0.99f, 1.01f);
        oa.setDuration(1000);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(weight, "scaleY", 0.99f, 1.01f);
        oa2.setDuration(1000);

        weight.setImageDrawable(activity.getResources().getDrawable(R.mipmap.weight_alarm));

        oa.start();
        oa2.start();

        oa = ObjectAnimator.ofFloat(weight, "scaleX", 1.01f, 0.99f);
        oa.setDuration(1000);
        oa2 = ObjectAnimator.ofFloat(weight, "scaleY", 1.01f, 0.99f);
        oa2.setDuration(1000);

        oa.start();
        oa2.start();
    }

    public void leftAlarm(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.left_alarm);

        ObjectAnimator oa = ObjectAnimator.ofFloat(left, "scaleX", 0.99f, 1.01f);
        oa.setDuration(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(left, "scaleY", 0.99f, 1.01f);
        oa2.setDuration(500);

        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.left_rotation_alarm));

        oa.start();
        oa2.start();

        oa = ObjectAnimator.ofFloat(left, "scaleX", 1.01f, 0.99f);
        oa.setDuration(500);
        oa2 = ObjectAnimator.ofFloat(left, "scaleY", 1.01f, 0.99f);
        oa2.setDuration(500);

        oa.start();
        oa2.start();
    }

    public void leftAlarmClear(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.left_alarm);
        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.left_rotation));
    }


    public void rightAlarm(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.right_alarm);

        ObjectAnimator oa = ObjectAnimator.ofFloat(left, "scaleX", 0.99f, 1.01f);
        oa.setDuration(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(left, "scaleY", 0.99f, 1.01f);
        oa2.setDuration(500);

        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.right_rotation_alarm));

        oa.start();
        oa2.start();

        oa = ObjectAnimator.ofFloat(left, "scaleX", 1.01f, 0.99f);
        oa.setDuration(500);
        oa2 = ObjectAnimator.ofFloat(left, "scaleY", 1.01f, 0.99f);
        oa2.setDuration(500);

        oa.start();
        oa2.start();
    }

    public void rightAlarmClear(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.right_alarm);
        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.right_rotation));
    }

    public void fwdAlarm(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.forward_alarm);

        ObjectAnimator oa = ObjectAnimator.ofFloat(left, "scaleX", 0.99f, 1.01f);
        oa.setDuration(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(left, "scaleY", 0.99f, 1.01f);
        oa2.setDuration(500);

        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.forward_alarm));


        oa.start();
        oa2.start();

        oa = ObjectAnimator.ofFloat(left, "scaleX", 1.01f, 0.99f);
        oa.setDuration(500);
        oa2 = ObjectAnimator.ofFloat(left, "scaleY", 1.01f, 0.99f);
        oa2.setDuration(500);

        oa.start();
        oa2.start();
    }

    public void fwdAlarmClear(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.forward_alarm);
        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.forward));
    }

    public void backAlarm(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.back_alarm);

        ObjectAnimator oa = ObjectAnimator.ofFloat(left, "scaleX", 0.99f, 1.01f);
        oa.setDuration(500);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(left, "scaleY", 0.99f, 1.01f);
        oa2.setDuration(500);

        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.backoff_alarm));


        oa.start();
        oa2.start();

        oa = ObjectAnimator.ofFloat(left, "scaleX", 1.01f, 0.99f);
        oa.setDuration(500);
        oa2 = ObjectAnimator.ofFloat(left, "scaleY", 1.01f, 0.99f);
        oa2.setDuration(500);

        oa.start();
        oa2.start();
    }

    public void backAlarmClear(Activity activity) {
        ImageView left = (ImageView) activity.findViewById(R.id.back_alarm);
        left.setImageDrawable(activity.getResources().getDrawable(R.mipmap.backoff));
    }
}
