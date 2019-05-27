package com.wooddeep.crane.element;

import java.util.HashMap;

public class ElemMap {
    private HashMap<String, BaseElem> elemMap;

    public ElemMap() {
        elemMap = new HashMap<>();
    }

    public void addElem(String id, BaseElem elem) {
        elemMap.put(id, elem);
    }

    public BaseElem getElem(String id) {
        return elemMap.get(id);
    }
}
