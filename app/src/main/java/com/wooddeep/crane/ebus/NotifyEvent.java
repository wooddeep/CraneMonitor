package com.wooddeep.crane.ebus;

import org.json.JSONObject;

public class NotifyEvent {
    public boolean state;
    public JSONObject payload;

    public NotifyEvent(boolean state, JSONObject payload) {
        this.state = state;
        this.payload = payload;
    }

    public boolean isState() {
        return state;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }
}
