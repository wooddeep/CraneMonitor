package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.wooddeep.crane.persist.dao.WorkRecDao;
import com.wooddeep.crane.persist.entity.WorkRecrod;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import java.util.ArrayList;
import java.util.List;

//import android.support.design.widget.Snackbar;


// android开源控件
// https://www.cnblogs.com/porter/p/8135835.html
// https://github.com/opendigg/awesome-github-android-ui (*)

// 运行错误
// https://blog.csdn.net/littlexbear/article/details/81022581

// 表格展示
// https://github.com/z3896823/PanelList

// smartTable
// https://github.com/huangyanbin/smartTable
// https://juejin.im/post/5a5dce7651882573256bd043

// 对话框
// https://github.com/saiwu-bigkoo/Android-AlertView

// java 代码写下拉列表
// https://www.cnblogs.com/xiaodeyao/p/5049773.html

// greendao
// https://www.cnblogs.com/wjtaigwh/p/6394288.html

public class DataRecord extends AppCompatActivity {
    private Context context;
    private FixedTitleTable table;
    private Activity activity = this;
    private WorkRecDao workRecDao;

    private ImageView firstPage;
    private ImageView prevPage;
    private ImageView nextPage;
    private ImageView LatestPage;

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
        add(new TableCell(0, "回转/rotate"));
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
        add(400);
        add(200);
        add(200);
        add(200);
        add(200);
        add(200);
        add(200);
        add(200);
        add(200);
        add(200);
        add(200);
        add(200);
    }};


    private void viewInstance() {
        firstPage = (ImageView) findViewById(R.id.first_page);
        prevPage = (ImageView) findViewById(R.id.prev_page);
        nextPage = (ImageView) findViewById(R.id.next_page);
        LatestPage = (ImageView) findViewById(R.id.latest_page);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_record);
        context = getApplicationContext();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        workRecDao = new WorkRecDao(context);

    }

    private void setOnTouchListener(View view) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ObjectAnimator oa = ObjectAnimator.ofFloat(view,
                        "scaleX", 0.93f, 1f);
                    oa.setDuration(500);
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(view,
                        "scaleY", 0.93f, 1f);
                    oa2.setDuration(700);
                    oa.start();
                    oa2.start();
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ObjectAnimator oa = ObjectAnimator.ofFloat(view,
                        "scaleX", 1f, 0.93f);
                    oa.setDuration(500);
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(view,
                        "scaleY", 1f, 0.93f);
                    oa2.setDuration(700);
                    oa.start();
                    oa2.start();
                }
                return false;
            }
        };
        view.setOnTouchListener(onTouchListener);
    }

    private void setOnClickListener(View view) {

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.load_data) {
                    AlertView alertView = new AlertView("加载负荷特性参数", "", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) {
                            }
                        }
                    });
                    alertView.show();
                } else if (view.getId() == R.id.latest_page) { // 最后一页
                    int count = (int)workRecDao.queryCount();
                    globalIndex = count - pageSize;
                    showWorkRecInfo();
                } else if (view.getId() == R.id.next_page) { // 下一页
                    globalIndex = globalIndex + pageSize;
                    showWorkRecInfo();
                } else if (view.getId() == R.id.first_page) { // 第一页
                    globalIndex = 0;
                    showWorkRecInfo();
                } else if (view.getId() == R.id.prev_page) { // 上一页
                    globalIndex = globalIndex - pageSize;
                    showWorkRecInfo();
                } else if (view.getId() == R.id.close_logo) {
                    finish();
                }
            }
        };

        view.setOnClickListener(onClickListener);
    }

    private void setOnTouchListener() {
        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.first_page));
            add((ImageView) findViewById(R.id.next_page));
            add((ImageView) findViewById(R.id.prev_page));
            add((ImageView) findViewById(R.id.latest_page));
            add((ImageView) findViewById(R.id.close_logo));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }

    public void showWorkRecInfo() {
        table.init(this, null);
        table.clearAll();

        table.setFirstRow(colNames, idList, widthList);

        List<WorkRecrod> workRecrods = workRecDao.queryPage(globalIndex, pageSize);

        // 数据信息
        for (WorkRecrod recrod : workRecrods) {
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

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        table = new FixedTitleTable(dm.widthPixels); // 输入屏幕宽度

        try {
            showWorkRecInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnTouchListener();
    }
}
