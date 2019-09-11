package com.wooddeep.crane.views;

import android.view.View;

import org.json.JSONObject;

/**
 * Created by niuto on 2019/9/10.
 */

public class TableCell {
    public int type = 0; // 0 ~ TextView, 1 ~ EditText, 2 ~ Button
    public String value = "0";
    public JSONObject privData = new JSONObject();

    public View.OnClickListener clickListener;

    public TableCell(int t, String v) {
        this.type = t;
        this.value = v;
    }

    public TableCell(int t, String v, View.OnClickListener cl) {
        this.type = t;
        this.value = v;
        this.clickListener = cl;
    }

    public TableCell(int t, String v, JSONObject p) {
        this.type = t;
        this.value = v;
        this.privData = p;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JSONObject getPrivData() {
        return privData;
    }

    public void setPrivData(JSONObject privData) {
        this.privData = privData;
    }
}
