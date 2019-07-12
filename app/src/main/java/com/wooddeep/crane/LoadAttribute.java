package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rmondjone.locktableview.DataCell;
import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.XRecyclerView;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.LoadDao;
import com.wooddeep.crane.persist.entity.Load;

import org.json.JSONObject;

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

public class LoadAttribute extends AppCompatActivity {
    private Context context;

    private int screenWidth = 400; // dp

    private ArrayList<ArrayList<DataCell>> gTable = null;
    private ArrayList<Integer> gColId = null;

    private Activity activity = this;

    private List<Load> confLoad(Context contex) {
        DatabaseHelper.getInstance(contex).createTable(Load.class);
        LoadDao dao = new LoadDao(contex);
        List<Load> paras = dao.selectAll();
        if (paras == null || paras.size() == 0) {
            Load load = new Load();
            load.setCraneType("JL186/12");
            load.setPower("2");
            load.setArmLength("50");
            load.setCoordinate("0");
            load.setWeight("6");
            dao.insert(load);

            load = new Load();
            load.setCraneType("JL186/12");
            load.setPower("2");
            load.setArmLength("50");
            load.setCoordinate("34");
            load.setWeight("6");
            dao.insert(load);

            load = new Load();
            load.setCraneType("JL186/12");
            load.setPower("2");
            load.setArmLength("50");
            load.setCoordinate("35");
            load.setWeight("5.6");
            dao.insert(load);
        }
        paras = dao.selectAll();

        System.out.println(paras.size());
        return paras;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_attribute);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        confLoad(getApplicationContext());

        LoadDao dao = new LoadDao(getApplicationContext());
        List<String> craneTypes = dao.getCraneTypes();
        List<String> armLengths = dao.getArmLengths(craneTypes.get(0));
        List<String> cables = dao.getCables(craneTypes.get(0), armLengths.get(0));

        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.crane_type_option);
        spinner.setItems(craneTypes);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                MaterialSpinner armLenSpinner = (MaterialSpinner) findViewById(R.id.arm_length_option);
                List<String> armLens = dao.getArmLengths(item);
                armLenSpinner.setItems(armLens);
                armLenSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        // TODO
                    }
                });
            }
        });

        MaterialSpinner armLenSpinner = (MaterialSpinner) findViewById(R.id.arm_length_option);
        armLenSpinner.setItems(armLengths);
        armLenSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        MaterialSpinner cableSpiner = (MaterialSpinner) findViewById(R.id.rope_num_option);
        cableSpiner.setItems(cables);
        cableSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public List<Load> queryLoadByCondition() {
        MaterialSpinner craneTypeSpinner = (MaterialSpinner) findViewById(R.id.crane_type_option);
        MaterialSpinner armLenSpinner = (MaterialSpinner) findViewById(R.id.arm_length_option);
        MaterialSpinner cableNumSpinner = (MaterialSpinner) findViewById(R.id.rope_num_option);

        String craneType = craneTypeSpinner.getText().toString();
        String armLength = armLenSpinner.getText().toString();
        String cableNum = cableNumSpinner.getText().toString();

        System.out.printf("%s-%s-%s\n", craneType, armLength, cableNum);

        LoadDao dao = new LoadDao(getApplicationContext());
        return dao.getLoads(craneType, armLength, cableNum);
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
        /*
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.add_logo) {
                    AreaDao dao = new AreaDao(context);
                    int rowCnt = dao.selectAll().size();
                    dao.insert(new Area(
                        100,
                        0,
                        0,
                        50,
                        50,
                        100,
                        100,
                        150,
                        150,
                        200,
                        200,
                        250,
                        250)
                    );
                    List<Area> paras = confLoad(context);
                    paraTableRender(paras);
                } else if (view.getId() == R.id.minus_logo) {
                    List<Area> paras = confLoad(context);
                    if (paras.size() <= 1) {
                        Toast toast = Toast.makeText(LoadAttribute.this, "不能全删除!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    AreaDao dao = new AreaDao(context);
                    dao.delete(paras.get(paras.size() - 1));
                    paras = confLoad(context);
                    paraTableRender(paras);

                } else if (view.getId() == R.id.save_logo) { // 保存数据
                    AlertView alertView = new AlertView("保存塔基参数", "", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0 && gTable != null) { // 确认
                                for (int j = 1; j < gTable.get(j).size(); j++) {
                                    Area cp = new Area();
                                    int id = gTable.get(0).get(j).getPrivData().optInt("id");
                                    cp.setId(id);
                                    cp.setHeight(Float.parseFloat(gTable.get(1).get(j).getValue()));
                                    cp.setX1(Float.parseFloat(gTable.get(2).get(j).getValue()));
                                    cp.setY1(Float.parseFloat(gTable.get(3).get(j).getValue()));
                                    cp.setX2(Float.parseFloat(gTable.get(4).get(j).getValue()));
                                    cp.setY2(Float.parseFloat(gTable.get(5).get(j).getValue()));
                                    cp.setX3(Float.parseFloat(gTable.get(6).get(j).getValue()));
                                    cp.setY3(Float.parseFloat(gTable.get(7).get(j).getValue()));
                                    cp.setX4(Float.parseFloat(gTable.get(8).get(j).getValue()));
                                    cp.setY4(Float.parseFloat(gTable.get(9).get(j).getValue()));
                                    cp.setX5(Float.parseFloat(gTable.get(10).get(j).getValue()));
                                    cp.setY5(Float.parseFloat(gTable.get(11).get(j).getValue()));
                                    cp.setX6(Float.parseFloat(gTable.get(12).get(j).getValue()));
                                    cp.setY6(Float.parseFloat(gTable.get(13).get(j).getValue()));
                                    AreaDao dao = new AreaDao(context);
                                    dao.update(cp);
                                }
                            }
                        }
                    });
                    alertView.show();

                } else if (view.getId() == R.id.close_logo) {
                    finish();
                }
            }
        };
        view.setOnClickListener(onClickListener);
        */
    }

    private void setOnTouchListener() {
        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.close_logo));

        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }


    public ArrayList<ArrayList<DataCell>> areaParaArrange(List<Load> paras) {

        ArrayList<ArrayList<DataCell>> table = new ArrayList<ArrayList<DataCell>>();

        // 头部信息
        ArrayList<DataCell> head = new ArrayList<DataCell>() {{
            add(new DataCell(0, "小车坐标"));
            add(new DataCell(0, "额定吊重"));
        }};
        table.add(head);

        // 数据信息
        List<Load> loads = queryLoadByCondition();
        for (Load load : loads) {
            ArrayList<DataCell> row = new ArrayList<DataCell>();
            row.add(new DataCell(0, load.getCoordinate()));
            row.add(new DataCell(0, load.getWeight()));
            table.add(row);

        }

        return table;
    }

    public void paraTableRender(List<Load> paras) {

        LinearLayout loadAttrContainer = (LinearLayout) findViewById(R.id.load_attri_container);
        ArrayList<ArrayList<DataCell>> table = areaParaArrange(paras);

        gTable = table;
        final LockTableView mLockTableView = new LockTableView(this, loadAttrContainer, table);
        int firstColumnWidth = 100;
        Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
        mLockTableView.setLockFristColumn(true) //是否锁定第一列
            .setLockFristRow(true) //是否锁定第一行
            .setMaxColumnWidth(firstColumnWidth) //列最大宽度
            .setMinColumnWidth(60) //列最小宽度
            .setColumnWidth(0, 60)
            .setMinRowHeight(20)//行最小高度
            .setMaxRowHeight(60)//行最大高度
            .setTextViewSize(16) //单元格字体大小
            .setCellPadding(5)//设置单元格内边距(dp)
            .setFristRowBackGroudColor(R.color.table_head)//表头背景色
            .setTableHeadTextColor(R.color.beijin)//表头字体颜色
            .setTableContentTextColor(R.color.border_color)//单元格字体颜色
            .setNullableString("N/A") //空值替换值
            .setTableViewListener(new LockTableView.OnTableViewListener() {
                //设置横向滚动监听
                @Override
                public void onTableViewScrollChange(int x, int y) {
                    Log.e("滚动值", "[" + x + "]" + "[" + y + "]");
                }
            })
            .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                //设置横向滚动边界监听
                @Override
                public void onLeft(HorizontalScrollView view) {
                    Log.e("滚动边界", "滚动到最左边");
                }

                @Override
                public void onRight(HorizontalScrollView view) {
                    Log.e("滚动边界", "滚动到最右边");
                }
            })
            .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                @Override
                public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<DataCell>> mTableDatas) {
                    mLockTableView.setTableDatas(mTableDatas);
                    //停止刷新
                }

                @Override
                public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<DataCell>> mTableDatas) {
                    mLockTableView.setTableDatas(mTableDatas);
                }
            })
            .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                @Override
                public void onItemClick(View item, int position) {
                    Log.e("点击事件", position + "");
                }
            })
            .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                @Override
                public void onItemLongClick(View item, int position) {
                    Log.e("长按事件", position + "");
                }
            })
            .setOnItemSeletor(R.color.dashline_color);//设置Item被选中颜色
        
        mLockTableView.setColumnWidth(1, screenWidth - firstColumnWidth); //设置指定列文本宽度(从0开始计算,宽度单位dp)
        mLockTableView.show(); //显示表格,此方法必须调用

    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        LinearLayout craneSettingContainer = (LinearLayout) findViewById(R.id.load_attri_container);
        int screenWidthPx = craneSettingContainer.getMeasuredWidth();
        context = getApplicationContext();
        screenWidth = DisplayUtil.px2dip(context, screenWidthPx); // 转换为dp
        List<Load> paras = confLoad(context);
        paraTableRender(paras); // 渲染出表格

        //setOnTouchListener();

    }
}
