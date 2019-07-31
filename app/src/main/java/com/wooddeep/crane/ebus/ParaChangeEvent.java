package com.wooddeep.crane.ebus;

import com.wooddeep.crane.persist.entity.AlarmSet;

public class ParaChangeEvent {
    public AlarmSet alarmSet;

    public ParaChangeEvent(AlarmSet alarmSet) {
        this.alarmSet = alarmSet;
    }
}
