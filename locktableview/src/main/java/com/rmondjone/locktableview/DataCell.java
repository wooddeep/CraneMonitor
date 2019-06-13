package com.rmondjone.locktableview;

import android.view.View;

import org.json.JSONObject;

public class DataCell {

    private int type;

    private String value;

    private JSONObject privData = null;

    private View.OnLongClickListener onLongClickListener  = null;


    public DataCell(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public DataCell(int type, String value, JSONObject pd) {
        this.type = type;
        this.value = value;
        this.privData = pd;
    }

    public DataCell(int type, String value, View.OnLongClickListener onLongClickListener) {
        this.type = type;
        this.value = value;
        this.onLongClickListener = onLongClickListener;
    }

    public DataCell(int type, String value, JSONObject privData, View.OnLongClickListener onLongClickListener) {
        this.type = type;
        this.value = value;
        this.privData = privData;
        this.onLongClickListener = onLongClickListener;
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

    public View.OnLongClickListener getOnLongClickListener() {
        return onLongClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

}
