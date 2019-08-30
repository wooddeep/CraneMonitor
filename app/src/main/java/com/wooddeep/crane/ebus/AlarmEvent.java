package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/6.
 */

public class AlarmEvent {
    public boolean weightAlarm = false;
    public boolean momentAlarm = false;
    public boolean leftAlarm = false;
    public boolean rightAlarm = false;
    public boolean forwardAlarm = false;
    public boolean backendAlarm = false;

    public int weightAlarmLevel = 0;
    public int momentAlarmLevel = 0;
    public int leftAlarmLevel = 0;
    public int rightAlarmLevel = 0;
    public int forwardAlarmLevel = 0;
    public int backendAlarmLevel = 0;

    public int weightAlarmDispearLevel = 3; // 吊重告警消失等级
    public int momentAlarmDispearLevel = 3; // 力矩告警消失等级
}
