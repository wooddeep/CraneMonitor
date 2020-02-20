package com.wooddeep.crane.ebus;

/**
 * Created by niuto on 2019/8/14.
 */

public class RestartEvent {
    private int type;

    private int setChannelNo;

    public RestartEvent() {
    }

    public RestartEvent(int t) {
        this.type = t;
    }

    public RestartEvent(int type, int setChannelNo) {
        this.type = type;
        this.setChannelNo = setChannelNo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getSetChannelNo() {
        return setChannelNo;
    }

    public void setSetChannelNo(int setChannelNo) {
        this.setChannelNo = setChannelNo;
    }
}
