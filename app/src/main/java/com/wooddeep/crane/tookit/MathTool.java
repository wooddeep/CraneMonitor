package com.wooddeep.crane.tookit;

public class MathTool {

    public static float radiansToAngle(float radians) {
        double angle = Math.toDegrees(radians);
        if (angle < 0) angle = 360 - (Math.abs(angle) % 360);
        return (float)angle;
    }

}
