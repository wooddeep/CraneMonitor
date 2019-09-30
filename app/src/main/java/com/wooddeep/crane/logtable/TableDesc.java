package com.wooddeep.crane.logtable;

import com.wooddeep.crane.persist.dao.log.LogDao;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niuto on 2019/9/30.
 */

@SuppressWarnings("unused")
abstract public class TableDesc {

    private LogDao dao;
    private ArrayList<TableCell> colNames; // 列名
    private List<Integer> idList; // 列ID
    private List<Integer> widthList; // 列宽度
    private int pageSize = 10;
    private int globalIndex = 0;

    public TableDesc() {
    }

    public TableDesc(ArrayList<TableCell> names, List<Integer> ids, List<Integer> widths) {
        this.colNames = names;
        this.idList = ids;
        this.widthList = widths;
    }

    abstract public void showDataInfo(FixedTitleTable table);

    public LogDao getDao() {
        return dao;
    }

    abstract public ArrayList<TableCell> getColNames();

    abstract public List<Integer> getIdList();

    abstract public List<Integer> getWidthList();

    abstract public int getPageSize();

    abstract public int getGlobalIndex();

    abstract public void setGlobalIndex(int globalIndex);
}
