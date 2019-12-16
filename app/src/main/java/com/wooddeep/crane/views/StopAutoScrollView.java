package com.wooddeep.crane.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by niuto on 2019/12/16.
 */

public class StopAutoScrollView extends HorizontalScrollView {


    public StopAutoScrollView(Context context) {
        super(context);
    }

    public StopAutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StopAutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StopAutoScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void requestChildFocus(View child, View focused) {
    }

}

