package com.wooddeep.crane.logtable;

import android.content.Context;

import com.wooddeep.crane.persist.dao.log.CaliRecDao;
import com.wooddeep.crane.persist.dao.log.LogDao;
import com.wooddeep.crane.persist.entity.log.CaliRec;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niuto on 2019/9/30.
 */

@SuppressWarnings("unused")
public class CaliRecTable extends TableDesc {

    private Context context;

    private int pageSize = 10;
    private int globalIndex = 0;

    // 头部信息  // ID, 时间，倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
    private ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
        add(new TableCell(0, "编号/ID"));
        add(new TableCell(0, "时间/Time"));

        add(new TableCell(0, "类型/type"));

        add(new TableCell(0, "初始AD/start AD"));
        add(new TableCell(0, "终止AD/end AD"));

        add(new TableCell(0, "初始值/start value"));
        add(new TableCell(0, "终止值/end value"));

        add(new TableCell(0, "斜率/K value"));

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
    }};

    private List<Integer> widthList = new ArrayList() {{
        add(200); // ID
        add(300); // 时间
        add(200);
        add(250);
        add(200);
        add(250);
        add(250);
        add(200);
    }};

    private CaliRecDao dao;

    public CaliRecTable(Context context) {
        this.context = context;
        this.dao = new CaliRecDao(context);
    }

    @Override
    public void showDataInfo(FixedTitleTable table) {
        List<CaliRec> recrods = dao.queryPage(globalIndex, pageSize);

        // 数据信息
        for (CaliRec recrod : recrods) {
            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, String.valueOf(recrod.getId())));
            row.add(new TableCell(0, recrod.getTime()));

            row.add(new TableCell(0, recrod.getType()));
            row.add(new TableCell(0, String.valueOf(recrod.getStartAD())));
            row.add(new TableCell(0, String.valueOf(recrod.getEndAD())));
            row.add(new TableCell(0, recrod.getStartValue()));
            row.add(new TableCell(0, recrod.getEndValue()));
            row.add(new TableCell(0, String.valueOf(recrod.getKValue())));

            table.addDataRow(row, widthList, true);
        }
    }

    @Override
    public ArrayList<TableCell> getColNames() {
        return colNames;
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
