package com.wooddeep.crane.ebus;

public class ChannelEvent {
    int type = 0;
    int currChannel;
    int setChannel;

    public ChannelEvent(int currChannel, int setChannel) {
        this.currChannel = currChannel;
        this.setChannel = setChannel;
    }

    public ChannelEvent(int type, int currChannel, int setChannel) {
        this.type = type;
        this.currChannel = currChannel;
        this.setChannel = setChannel;
    }

    public int getCurrChannel() {
        return currChannel;
    }

    public void setCurrChannel(int currChannel) {
        this.currChannel = currChannel;
    }

    public int getSetChannel() {
        return setChannel;
    }

    public void setSetChannel(int setChannel) {
        this.setChannel = setChannel;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
