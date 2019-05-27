package com.wooddeep.crane.element;

import java.util.UUID;

public class BaseElem {

    private String uuid;

    public BaseElem() {
        this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
