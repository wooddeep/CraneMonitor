package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.rmondjone.locktableview.DataCell;
import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.XRecyclerView;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.entity.Crane;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

public class CraneSetting extends AppCompatActivity {
    private Context context;

    private int screenWidth = 400; // dp

    private ArrayList<ArrayList<DataCell>> gTable = null;
    private ArrayList<Integer> gColId = null;

    private Activity activity = this;

    private List<Crane> confLoad(Context contex) {
        CraneDao dao = new CraneDao(contex);

        List<Crane> paras = dao.selectAll();
        if (paras.size() == 0) {

            dao.insert(new Crane(
                false,
                1,
                "1号塔基",
                0,
                100,
                100,
                1,
                1,
                1,
                40,
                1,
                1,
                1,
                1)
            );
        }

        paras = dao.selectAll();
        return paras;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crane_setting);
        if (getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
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
                if (view.getId() == R.id.add_logo) {
                    CraneDao dao = new CraneDao(context);
                    int rowCnt = dao.selectAll().size();
                    dao.insert(new Crane(
                        false,
                        0,
                        String.format("%d号塔基", rowCnt + 1),
                        0,
                        100,
                        100,
                        1,
                        1,
                        1,
                        40,
                        10,
                        1,
                        1,
                        1)
                    );
                    List<Crane> paras = confLoad(context);
                    try {
                        paraTableRender(paras);
                    } catch (Exception e) {
                    }
                } else if (view.getId() == R.id.minus_logo) {
                    List<Crane> paras = confLoad(context);
                    if (paras.size() <= 1) {
                        Toast toast = Toast.makeText(CraneSetting.this, "不能全删除!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        CraneDao dao = new CraneDao(context);
                        dao.delete(paras.get(paras.size() - 1));
                        paras = confLoad(context);
                        try {
                            paraTableRender(paras);
                        } catch (Exception e) {
                        }
                    }
                } else if (view.getId() == R.id.save_logo) { // 保存数据
                    AlertView alertView = new AlertView("保存塔基参数", "确定保存?", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0 && gTable != null) { // 确认
                                for (int j = 1; j < gTable.get(j).size(); j++) {
                                    Crane cp = new Crane();
                                    int id = gTable.get(0).get(j).getPrivData().optInt("id");
                                    cp.setId(id);
                                    cp.setName(gTable.get(0).get(j).getValue());
                                    cp.setType(Integer.parseInt(gTable.get(1).get(j).getValue()));
                                    cp.setCoordX1(Float.parseFloat(gTable.get(2).get(j).getValue()));
                                    cp.setCoordY1(Float.parseFloat(gTable.get(3).get(j).getValue()));
                                    cp.setCoordX2(Float.parseFloat(gTable.get(4).get(j).getValue()));
                                    cp.setCoordY2(Float.parseFloat(gTable.get(5).get(j).getValue()));
                                    cp.setCraneHeight(Float.parseFloat(gTable.get(6).get(j).getValue()));
                                    cp.setBigArmLength(Float.parseFloat(gTable.get(7).get(j).getValue()));
                                    cp.setBalancArmLength(Float.parseFloat(gTable.get(8).get(j).getValue()));
                                    cp.setCraneBodyRadius(Float.parseFloat(gTable.get(9).get(j).getValue()));
                                    cp.setBigArmWidth(Float.parseFloat(gTable.get(10).get(j).getValue()));
                                    cp.setBalancArmWidth(Float.parseFloat(gTable.get(11).get(j).getValue()));
                                    CraneDao dao = new CraneDao(context);
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
    }

    private void setOnTouchListener() {
        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.close_logo));
            add((ImageView) findViewById(R.id.add_logo));
            add((ImageView) findViewById(R.id.minus_logo));
            add((ImageView) findViewById(R.id.save_logo));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }

    public static int StringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }


    private static String[] craneParaNames = new String[]{
        "塔基类型",
        "X1(塔基X坐标)",
        "Y1(塔基Y坐标)",
        "X2(塔基X偏移)",
        "Y2(塔基Y偏移)",
        "塔机高度",
        "大臂长度",
        "平衡臂长度",
        "塔身直径",
        "大臂宽度",
        "平衡臂宽度",
    };


    public ArrayList<ArrayList<DataCell>> craneParaArrange(List<Crane> paras) throws Exception {
        ArrayList<ArrayList<DataCell>> table = new ArrayList<ArrayList<DataCell>>();

        ArrayList<DataCell> head = new ArrayList<DataCell>() {{
            add(new DataCell(0, "参数类型"));
        }};

        gColId = new ArrayList<Integer>();
        for (int i = 0; i < paras.size(); i++) {
            head.add(new DataCell(0, String.format("%02d号塔基", i + 1),
                new JSONObject().put("craneNo", i + 1).put("id", paras.get(i).getId()),
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        TextView textView = (TextView) view;
                        JSONObject privData = (JSONObject) textView.getTag();
                        int craneNo = privData.optInt("craneNo");
                        AlertView alertView = new AlertView("保存塔基参数", "确定" + craneNo + "号塔基绑定本终端?", null,
                            new String[]{"确定", "取消"}, null, activity,
                            AlertView.Style.Alert, new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0 && gTable != null) { // 确认
                                    for (int j = 1; j < gTable.get(j).size(); j++) {
                                        Crane cp = new Crane();
                                        int id = gTable.get(0).get(j).getPrivData().optInt("id");
                                        cp.setId(id);
                                        TextView tv = (TextView) gTable.get(0).get(j).getView();
                                        if (craneNo == j) {
                                            TextPaint tp = tv.getPaint();
                                            tp.setFakeBoldText(true);
                                            tv.setTextColor(Color.BLACK);
                                            cp.setIsMain(true);
                                        } else {
                                            TextPaint tp = tv.getPaint();
                                            tp.setFakeBoldText(false);
                                            tv.setTextColor(0xff2c8fd3); // LockTableView.mTableHeadTextColor
                                            cp.setIsMain(false);
                                        }

                                        cp.setType(Integer.parseInt(gTable.get(1).get(j).getValue()));
                                        cp.setCoordX1(Float.parseFloat(gTable.get(2).get(j).getValue()));
                                        cp.setCoordY1(Float.parseFloat(gTable.get(3).get(j).getValue()));
                                        cp.setCoordX2(Float.parseFloat(gTable.get(4).get(j).getValue()));
                                        cp.setCoordY2(Float.parseFloat(gTable.get(5).get(j).getValue()));
                                        cp.setCraneHeight(Float.parseFloat(gTable.get(6).get(j).getValue()));
                                        cp.setBigArmLength(Float.parseFloat(gTable.get(7).get(j).getValue()));
                                        cp.setBalancArmLength(Float.parseFloat(gTable.get(8).get(j).getValue()));
                                        cp.setCraneBodyRadius(Float.parseFloat(gTable.get(9).get(j).getValue()));
                                        cp.setBigArmWidth(Float.parseFloat(gTable.get(10).get(j).getValue()));
                                        cp.setBalancArmWidth(Float.parseFloat(gTable.get(11).get(j).getValue()));
                                        CraneDao dao = new CraneDao(context);
                                        dao.update(cp);
                                    }
                                }
                            }
                        });
                        alertView.show();
                        return false;
                    }
                }));
            gColId.add(paras.get(i).getId());
        }
        table.add(head);

        for (int i = 0; i < craneParaNames.length; i++) {
            ArrayList<DataCell> row = new ArrayList<DataCell>();
            row.add(new DataCell(0, craneParaNames[i]));
            for (int j = 0; j < paras.size(); j++) {
                switch (i) {
                    case 0:
                        JSONObject privData = new JSONObject();
                        try {
                            JSONArray options = new JSONArray("[\"A类型\", \"B类型\"]");
                            privData.put("options", options);
                        } catch (Exception e) {
                        }
                        row.add(new DataCell(1, String.valueOf(paras.get(j).getType()), privData));
                        break;
                    case 1:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getCoordX1())));
                        break;
                    case 2:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getCoordY1())));
                        break;
                    case 3:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getCoordX2())));
                        break;
                    case 4:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getCoordY2())));
                        break;
                    case 5:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getCraneHeight())));
                        break;
                    case 6:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getBigArmLength())));
                        break;
                    case 7:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getBalancArmLength())));
                        break;
                    case 8:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getCraneBodyRadius())));
                        break;
                    case 9:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getBigArmWidth())));
                        break;
                    case 10:
                        row.add(new DataCell(0, String.valueOf(paras.get(j).getBalancArmWidth())));
                        break;
                }
            }

            table.add(row);
        }

        return table;
    }

    public void paraTableRender(List<Crane> paras) throws Exception {
        LinearLayout craneSettingContainer = (LinearLayout) findViewById(R.id.crane_setting_container);
        ArrayList<ArrayList<DataCell>> table = craneParaArrange(paras);
        gTable = table;
        final LockTableView mLockTableView = new LockTableView(this, craneSettingContainer, table);
        int firstColumnWidth = 100;
        Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
        mLockTableView.setLockFristColumn(true) //是否锁定第一列
            .setLockFristRow(true) //是否锁定第一行
            .setMaxColumnWidth(firstColumnWidth) //列最大宽度
            .setMinColumnWidth(60) //列最小宽度
            .setColumnWidth(0, 100)
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

        for (int i = 1; i <= paras.size(); i++) {
            int columnWidth = (screenWidth - firstColumnWidth - 20 * paras.size()) / paras.size();
            if (columnWidth < 100) columnWidth = 100;
            mLockTableView.setColumnWidth(i, columnWidth); //设置指定列文本宽度(从0开始计算,宽度单位dp)
        }

        mLockTableView.show(); //显示表格,此方法必须调用

        if (gTable != null) { // 确认
            for (int j = 1; j < gTable.get(j).size(); j++) { // 从第二列开始, 第一列为标题栏
                int id = gTable.get(0).get(j).getPrivData().optInt("id");
                CraneDao craneDao = new CraneDao(context);
                Crane crane = craneDao.queryById(id);
                TextView tv = (TextView) gTable.get(0).get(j).getView();
                if (crane.getIsMain() == true) {
                    TextPaint tp = tv.getPaint();
                    tp.setFakeBoldText(true);
                    tv.setTextColor(Color.BLACK);
                } else {
                    TextPaint tp = tv.getPaint();
                    tp.setFakeBoldText(false);
                    tv.setTextColor(0xff2c8fd3); // LockTableView.mTableHeadTextColor
                }
            }
        }
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        LinearLayout craneSettingContainer = (LinearLayout) findViewById(R.id.crane_setting_container);
        int screenWidthPx = craneSettingContainer.getMeasuredWidth();
        context = getApplicationContext();
        screenWidth = DisplayUtil.px2dip(context, screenWidthPx); // 转换为dp
        List<Crane> paras = confLoad(context);
        try {
            paraTableRender(paras);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setOnTouchListener();

    }
}
