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

        ObjectAnimator oa = ObjectAnimator.ofFloat(weight, "scaleX", 0.90f, 1.1f);
        oa.setDuration(1000);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(weight, "scaleY", 0.90f, 1.1f);
        oa2.setDuration(1000);

        weight.setImageDrawable(activity.getResources().getDrawable(R.mipmap.weight_alarm));
        //weight.setBackgroundDrawable(getResources().getDrawable(R.mipmap.weight_alarm));
        //ObjectAnimator oa3 = ObjectAnimator.ofInt(weight, "backgroundColor", Color.BLACK, Color.RED, Color.BLACK);
        //oa3.setDuration(1000);

        oa.start();
        oa2.start();
        //oa3.start();

        oa = ObjectAnimator.ofFloat(weight, "scaleX", 1.1f, 0.90f);
        oa.setDuration(1000);
        oa2 = ObjectAnimator.ofFloat(weight, "scaleY", 1.1f, 0.90f);
        oa2.setDuration(1000);

        oa.start();
        oa2.start();
    }
}
