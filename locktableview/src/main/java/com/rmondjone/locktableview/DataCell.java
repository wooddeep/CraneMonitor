package com.rmondjone.locktableview;

import org.json.JSONObject;

public class DataCell {

    private int type;

    private String value;

    private JSONObject privData;

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

    public DataCell(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public DataCell(int type, String value, JSONObject pd) {
        this.type = type;
        this.value = value;
        this.privData = pd;
    }
}
