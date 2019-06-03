package com.wooddeep.crane.tookit;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class AnimUtil {

    public static void alphaAnimation(View view) {
        Animation animation = new AlphaAnimation(1f, 0.1f);
        animation.setDuration(1000);
        view.setAnimation(animation);
        animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(1000);
        view.setAnimation(animation);
    }
}
