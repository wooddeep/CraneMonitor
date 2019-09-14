package com.wooddeep.crane.main;

import com.wooddeep.crane.R;

import java.util.HashMap;

public class Constant {
    public static HashMap<Integer, Integer> rotateAlarmMap = new HashMap() {{
        put(1, R.mipmap.forward5);
        put(2, R.mipmap.forward4);
        put(3, R.mipmap.forward3);
        put(4, R.mipmap.forward2);
        put(5, R.mipmap.forward1);
    }};

    public static HashMap<Integer, Integer> carRangeAlarmMap = new HashMap() {{
        put(1, R.mipmap.forward5);
        put(2, R.mipmap.forward2);
    }};

    public static HashMap<Integer, Integer> weightAlarmMap = new HashMap() {{
        put(3, R.mipmap.weight3);
        put(2, R.mipmap.weight2);
        put(1, R.mipmap.weight1);
    }};

    public static HashMap<Integer, Integer> momentAlarmMap = new HashMap() {{
        put(3, R.mipmap.moment3);
        put(2, R.mipmap.moment2);
        put(1, R.mipmap.moment1);
    }};

    public static HashMap<Integer, String> levelMap = new HashMap() {{
        put(0, "〇");
        put(1, "①");
        put(2, "②");
        put(3, "③");
        put(4, "④");
        put(5, "⑤");
    }};
}
