package com.wooddeep.crane.logtable;

import android.content.Context;

import com.wooddeep.crane.persist.dao.log.LogDao;
import com.wooddeep.crane.persist.dao.log.RealDataDao;
import com.wooddeep.crane.persist.entity.log.RealData;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niuto on 2019/9/30.
 */

@SuppressWarnings("unused")
public class RealDataTable extends TableDesc {

    private Context context;

    private int pageSize = 10;
    private int globalIndex = 0;

    // 头部信息  // ID, 时间，倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
    private ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
        add(new TableCell(0, "编号/ID"));
        add(new TableCell(0, "时间/Time"));
        add(new TableCell(0, "倍率/power"));
        add(new TableCell(0, "力矩/moment"));
        add(new TableCell(0, "高度/height"));
        add(new TableCell(0, "幅度/range"));
        add(new TableCell(0, "额重/rated weight"));
        add(new TableCell(0, "重量/weight"));
        add(new TableCell(0, "回转/slewing"));
        add(new TableCell(0, "行走/walk"));
        add(new TableCell(0, "仰角/dip angle"));
        add(new TableCell(0, "风速/wind speed"));
        add(new TableCell(0, "备注/remark"));
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
    }};

    private List<Integer> widthList = new ArrayList() {{
        add(200);
        add(300); // 时间
        add(150); // 倍率
        add(200); // 力矩
        add(200); // 高度
        add(200); // 幅度
        add(250); // 额重
        add(150); // 重量
        add(200); // 回转
        add(150); // 行走
        add(200); // 仰角
        add(250); // 风速
        add(200);
    }};

    private RealDataDao dao;

    public RealDataTable(Context context) {
        this.context = context;
        this.dao = new RealDataDao(context);
    }

    @Override
    public void showDataInfo(FixedTitleTable table) {
        List<RealData> workRecrods = dao.queryPage(globalIndex, pageSize);

        // 数据信息
        for (RealData recrod : workRecrods) {
            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, String.valueOf(recrod.getId())));
            row.add(new TableCell(0, recrod.getTime()));

            // ID, 时间，倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
            row.add(new TableCell(0, String.valueOf(recrod.getRopenum())));
            row.add(new TableCell(0, String.valueOf(recrod.getMoment())));
            row.add(new TableCell(0, String.valueOf(recrod.getHeigth())));
            row.add(new TableCell(0, String.valueOf(recrod.getRange())));
            row.add(new TableCell(0, String.valueOf(recrod.getRatedweight())));
            row.add(new TableCell(0, String.valueOf(recrod.getWeight())));
            row.add(new TableCell(0, String.valueOf(recrod.getRotate())));
            row.add(new TableCell(0, String.valueOf(recrod.getWalk())));
            row.add(new TableCell(0, String.valueOf(recrod.getDipange())));
            row.add(new TableCell(0, String.valueOf(recrod.getWindspeed())));
            row.add(new TableCell(0, String.valueOf(recrod.getRemark())));

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

    @Override
    public LogDao getDao() {
        return dao;
    }
}
