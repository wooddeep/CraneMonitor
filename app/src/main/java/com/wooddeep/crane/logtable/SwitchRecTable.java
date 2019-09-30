package com.wooddeep.crane.logtable;

import android.content.Context;

import com.wooddeep.crane.persist.dao.log.SwitchRecDao;
import com.wooddeep.crane.persist.entity.log.SwitchRec;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niuto on 2019/9/30.
 */

@SuppressWarnings("unused")
public class SwitchRecTable extends TableDesc {

    private Context context;

    private int pageSize = 10;
    private int globalIndex = 0;

    // ID, 时间，倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
    private ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
        add(new TableCell(0, "编号/ID"));
        add(new TableCell(0, "时间/Time"));
        add(new TableCell(0, "行为/action"));
    }};

    private List<Integer> idList = new ArrayList() {{
        add(-1);
        add(-1);
        add(-1);
    }};

    private List<Integer> widthList = null;

    private SwitchRecDao dao;

    public SwitchRecTable(Context context) {
        this.context = context;
        this.dao = new SwitchRecDao(context);
    }

    public SwitchRecTable(Context context, int width) {
        this.context = context;
        this.dao = new SwitchRecDao(context);
        this.widthList = new ArrayList() {{
            add(200);
            add(300);
            add(width - 500);
        }};
    }

    @Override
    public void showDataInfo(FixedTitleTable table) {
        List<SwitchRec> records = dao.queryPage(globalIndex, pageSize);
        // 数据信息
        for (SwitchRec recrod : records) {
            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, String.valueOf(recrod.getId())));
            row.add(new TableCell(0, recrod.getTime()));
            row.add(new TableCell(0, recrod.getAction()));
            table.addDataRow(row, true, widthList);
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
}
