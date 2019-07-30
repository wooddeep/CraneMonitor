package com.wooddeep.crane.ebus;

import com.wooddeep.crane.persist.entity.AlarmSet;

public class AlarmSetEvent {
    public AlarmSet alarmSet;
    public AlarmSetEvent(AlarmSet alarmSet) {
        this.alarmSet = alarmSet;
    }
}
