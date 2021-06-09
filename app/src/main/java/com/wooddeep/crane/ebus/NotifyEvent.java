package com.wooddeep.crane.ebus;

import org.json.JSONObject;

public class NotifyEvent {
    public Boolean state;
    public JSONObject data;
    public String mac;

    public NotifyEvent(Boolean state, JSONObject payload, String mac) {
        this.state = state;
        this.data = payload;
        this.mac = mac;
    }

    public Boolean isState() {
        return state;
    }

    public JSONObject getData() {
        return data;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public void setData(JSONObject payload) {
        this.data = payload;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
