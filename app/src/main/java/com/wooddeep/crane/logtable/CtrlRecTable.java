package com.wooddeep.crane.logtable;

import android.content.Context;

import com.wooddeep.crane.MainActivity;
import com.wooddeep.crane.persist.dao.log.CtrlRecDao;
import com.wooddeep.crane.persist.dao.log.LogDao;
import com.wooddeep.crane.persist.entity.log.CtrlRec;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niuto on 2019/9/30.
 */

@SuppressWarnings("unused")
public class CtrlRecTable extends TableDesc {

    private Context context;

    private int pageSize = 10;
    private int globalIndex = 0;


    // 头部信息  // ID, 时间，倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
    private ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
        add(new TableCell(0, "编号/ID"));
        add(new TableCell(0, "时间/Time"));
        add(new TableCell(0, "小车出2/car out 2"));
        add(new TableCell(0, "小车出1/car out 1"));
        add(new TableCell(0, "回转5/gear 5"));
        add(new TableCell(0, "回转4/gear 4"));
        add(new TableCell(0, "回转3/gear 2"));
        add(new TableCell(0, "回转2/gear 2"));
        add(new TableCell(0, "左回转/left"));
        add(new TableCell(0, "右回转/right"));
        add(new TableCell(0, "力矩3/moment 3"));
        add(new TableCell(0, "力矩2/moment 2"));
        add(new TableCell(0, "力矩1/moment 1"));
        add(new TableCell(0, "吊重/weight"));
        add(new TableCell(0, "小车回2/car back 2"));
        add(new TableCell(0, "小车回1/car back 1"));
    }};

    private ArrayList<TableCell> colRvcNames = new ArrayList<TableCell>() {{
        add(new TableCell(0, "编号/ID"));
        add(new TableCell(0, "时间/Time"));
        add(new TableCell(0, "小车出2/car out 2"));
        add(new TableCell(0, "小车出1/car out 1"));
        add(new TableCell(0, "右回转3/right 3"));
        add(new TableCell(0, "左回转3/left 3"));
        add(new TableCell(0, "右回转2/rignt 2"));
        add(new TableCell(0, "左回转2/left 2"));
        add(new TableCell(0, "左回转1/left 1"));
        add(new TableCell(0, "右回转1/right 1"));
        add(new TableCell(0, "力矩3/moment 3"));
        add(new TableCell(0, "力矩2/moment 2"));
        add(new TableCell(0, "力矩1/moment 1"));
        add(new TableCell(0, "吊重/weight"));
        add(new TableCell(0, "小车回2/car back 2"));
        add(new TableCell(0, "小车回1/car back 1"));
    }};

    private List<Integer> idList = new ArrayList() {{
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
        add(-1);
    }};

    private List<Integer> widthList = new ArrayList() {{
        add(200);
        add(300); // 时间
        add(300);
        add(300);
        add(300);
        add(300);
        add(300);
        add(300);
        add(300);
        add(300);
        add(200);
        add(200);
        add(200);
        add(200);
        add(300);
        add(300);
    }};

    private CtrlRecDao dao;

    public CtrlRecTable(Context context) {
        this.context = context;
        this.dao = new CtrlRecDao(context);
    }

    @Override
    public void showDataInfo(FixedTitleTable table) {
        List<CtrlRec> recrods = dao.queryPage(globalIndex, pageSize);

        // 数据信息
        for (CtrlRec recrod : recrods) {
            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, String.valueOf(recrod.getId())));
            row.add(new TableCell(0, recrod.getTime()));

            row.add(new TableCell(0, String.valueOf(recrod.isCarOut2())));
            row.add(new TableCell(0, String.valueOf(recrod.isCarOut1())));
            row.add(new TableCell(0, String.valueOf(recrod.isRotate5())));
            row.add(new TableCell(0, String.valueOf(recrod.isRotate4())));
            row.add(new TableCell(0, String.valueOf(recrod.isRotate3())));
            row.add(new TableCell(0, String.valueOf(recrod.isRotate2())));
            row.add(new TableCell(0, String.valueOf(recrod.isLeftRote())));
            row.add(new TableCell(0, String.valueOf(recrod.isRightRote())));
            row.add(new TableCell(0, String.valueOf(recrod.isMoment3())));
            row.add(new TableCell(0, String.valueOf(recrod.isMoment2())));
            row.add(new TableCell(0, String.valueOf(recrod.isMoment1())));
            row.add(new TableCell(0, String.valueOf(recrod.isWeight1())));
            row.add(new TableCell(0, String.valueOf(recrod.isCarBack2())));
            row.add(new TableCell(0, String.valueOf(recrod.isCarBack1())));
            table.addDataRow(row, widthList, true);
        }
    }

    @Override
    public ArrayList<TableCell> getColNames() {
        if (MainActivity.isRvcMode.get()) {
            return colRvcNames;
        } else {
            return colNames;
        }
    }

    @Override
    public List<Integer> getIdList() {
        return idList;
    }

    @Override
    public List<Integer> getWidthList() {
        return widthList;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public int getGlobalIndex() {
        return globalIndex;
    }

    @Override
    public void setGlobalIndex(int globalIndex) {
        this.globalIndex = globalIndex;
    }

    @Override
    public LogDao getDao() {
        return dao;
    }
}
