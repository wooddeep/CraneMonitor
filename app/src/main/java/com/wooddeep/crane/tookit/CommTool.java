package com.wooddeep.crane.tookit;

import android.content.Context;

import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.entity.Crane;

import java.util.List;

public class CommTool {

    public static Coordinate getMainCoordinate(Context context) {
        Coordinate out = new Coordinate(0, 0);
        CraneDao craneDao = new CraneDao(context);
        Crane crane = new Crane();
        crane.setIsMain(true);
        List<Crane> craneList = craneDao.queryForMatching(crane);
        if (craneList == null || craneList.size() == 0) {
            craneList = craneDao.selectAll();
            crane = craneList.get(0);
            out.setX(crane.getCoordX1());
            out.setY(crane.getCoordY1());
        } else {
            out.setX(craneList.get(0).getCoordX1());
            out.setY(craneList.get(0).getCoordY1());
        }

        return out;
    }


}
