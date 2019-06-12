package com.wooddeep.crane.views;

/**
 * Created by niuto on 2019/6/11.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wooddeep.crane.R;

// https://trickyandroid.com/protip-inflating-layout-for-your-custom-view/

public class SimDialog extends RelativeLayout {
    private TextView header;
    private TextView description;
    private ImageView thumbnail;
    private ImageView icon;

    public SimDialog(Context context) {
        super(context);
        init();
    }

    public SimDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.sim_dialog, this);
        //this.header = (TextView)findViewById(R.id.header);
        this.description = (TextView)findViewById(R.id.description);
        this.thumbnail = (ImageView)findViewById(R.id.thumbnail);
        this.icon = (ImageView)findViewById(R.id.icon);
    }
}
