package com.wooddeep.crane.tookit;

import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.Load;
import com.wooddeep.crane.persist.entity.TcParam;

import java.util.List;

public class MathTool {

    public static MomentOut momentOut = new MomentOut(1, 1);

    public static float radiansToAngle(float radians) {
        double angle = Math.toDegrees(radians);
        if (angle < 0) angle = 360 - (Math.abs(angle) % 360);
        return (float) angle;
    }

    public static MomentOut momentCalc(List<TcParam> loads, float curWeight, float cc) {
        momentOut.moment = 100.0f;
        momentOut.ratedWeight = 10;

        for (int i = 0; i < loads.size() - 1; i++) {
            float sc = Float.parseFloat(loads.get(i).getCoordinate());
            float ec = Float.parseFloat(loads.get(i + 1).getCoordinate());
            if (sc <= cc && ec >= cc) { // 处于告警判断位置
                float sw = Float.parseFloat(loads.get(i).getWeight());
                float ew = Float.parseFloat(loads.get(i + 1).getWeight());
                float ww = 1; // 标准重量
                if (ec - cc == 0) {
                    ww = ew;
                } else {
                    float rate = (cc - sc) / (ec - cc);
                    ww = (rate * ew + sw) / (1 + rate);
                }
                momentOut.moment = Math.round(1000 * curWeight / ww) / 10.0f;
                momentOut.ratedWeight = Math.round(ww * 10) / 10.0f;
                return momentOut;
            }
        }

        float ww = Float.parseFloat(loads.get(loads.size() - 1).getWeight());
        momentOut.moment = Math.round(1000 * curWeight / ww) / 10.0f;
        momentOut.ratedWeight = Math.round(ww * 10) / 10.0f;
        return momentOut;
    }


    public static double calcVAngle(float armLen, float armShadowLen, float archPara) {
        double cos =  (armShadowLen + archPara) / armLen;
        double rands = Math.acos(cos);
        return Math.toDegrees(rands);
    }

    public static double calcShadow(float armLen, float vangle, float archPara) {
        return Math.cos(Math.toRadians(vangle)) * (armLen + archPara);
    }

    public static float shadowToArm(Crane crane) {
        float bigArmLength = crane.getBigArmLength();
        if (crane.getType() == 1) { // 动臂式模式下(初始值bigArmLength 是 大臂投影长度)，需要转换得到 半径长度
            bigArmLength = (bigArmLength + crane.getArchPara()) / (float) Math.cos(Math.toRadians(crane.getMinAngle()));
        }
        return bigArmLength;
    }

}
