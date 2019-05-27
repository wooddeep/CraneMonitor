package com.wooddeep.crane.element;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Function;

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

    public void forEach(Function<String, Boolean> callback) {
        Set<String> idSet = elemMap.keySet();
        for (String id: idSet) {

        }
    }

    public void alarmJudge(Function<String, Boolean> callback) {
        Set<String> idSet = elemMap.keySet();
        for (String id: idSet) {

        }
    }
}
