package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.XRecyclerView;
import com.wooddeep.crane.persist.dao.CraneParaDao;
import com.wooddeep.crane.persist.entity.CranePara;

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


public class CraneSetting extends AppCompatActivity {
    private Context context;

    private int screenWidth = 400; // dp

    private List<CranePara> confLoad(Context contex) {
        CraneParaDao dao = new CraneParaDao(contex);
        //dao.insert(new CranePara(0,"zhangsan", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        //dao.insert(new CranePara(1,"lisi", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));

        List<CranePara> paras = dao.getAllCranePara();
        return paras;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crane_setting);

        //context = getApplicationContext();
        //List<CranePara> paras = confLoad(context);
        //paraTableRender(paras);
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
                    CraneParaDao dao = new CraneParaDao(context);
                    dao.insert(new CranePara(0, "zhangsan", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
                    List<CranePara> paras = confLoad(context);
                    paraTableRender(paras);
                } else if (view.getId() == R.id.minus_logo) {

                } else if (view.getId() == R.id.close_logo) {

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

    public static void _main(String[] args) {
        for (int i = 0; i < craneParaNames.length; i++) {
            System.out.println(StringLength(craneParaNames[i]));
        }
    }

    private static String[] craneParaNames = new String[]{
    "X1(塔基X坐标)",
    "Y1(塔基Y坐标)",
    "X2(塔基X偏移)",
    "Y2(塔基Y偏移)",
    "         塔机高度", // SHIT!!! 必须保持字符串宽度一致！
    "         大臂长度",
    "       平衡臂长度",
    "          塔身直径",
    "          大臂宽度",
    "        平衡臂宽度",
    };


    public ArrayList<ArrayList<String>> craneParaArrange(List<CranePara> paras) {
        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();

        ArrayList<String> head = new ArrayList<String>() {{
            add("参数类型");
        }};

        for (int i = 0; i < paras.size(); i++) {
            head.add(String.format("%02d号塔基", i));
        }
        table.add(head);

        for (int i = 0; i < craneParaNames.length; i++) {
            ArrayList<String> row = new ArrayList<String>();
            row.add(craneParaNames[i]);
            for (int j = 0; j < paras.size(); j++) {
                switch (i) {
                    case 0:
                        row.add(paras.get(j).getCoordX1().toString());
                        break;
                    case 1:
                        row.add(paras.get(j).getCoordY1().toString());
                        break;
                    case 2:
                        row.add(paras.get(j).getCoordX2().toString());
                        break;
                    case 3:
                        row.add(paras.get(j).getCoordY2().toString());
                        break;
                    case 4:
                        row.add(paras.get(j).getCraneHeight().toString());
                        break;
                    case 5:
                        row.add(paras.get(j).getBigArmLength().toString());
                        break;
                    case 6:
                        row.add(paras.get(j).getBalancArmLength().toString());
                        break;
                    case 7:
                        row.add(paras.get(j).getCraneBodyRadius().toString());
                        break;
                    case 8:
                        row.add(paras.get(j).getBigArmWidth().toString());
                        break;
                    case 9:
                        row.add(paras.get(j).getBalancArmWidth().toString());
                        break;
                }
            }

            table.add(row);
        }

        return table;
    }

    public void paraTableRender(List<CranePara> paras) {
        LinearLayout craneSettingContainer = (LinearLayout) findViewById(R.id.crane_setting_container);
        ArrayList<ArrayList<String>> table = craneParaArrange(paras);

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
            public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                mLockTableView.setTableDatas(mTableDatas);
                //停止刷新
            }

            @Override
            public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
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
            int columnWidth = (screenWidth - firstColumnWidth - 50 * paras.size()) / paras.size();
            if (columnWidth < 100) columnWidth = 100;
            mLockTableView.setColumnWidth(i, columnWidth); //设置指定列文本宽度(从0开始计算,宽度单位dp)
        }

        mLockTableView.show(); //显示表格,此方法必须调用
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        LinearLayout craneSettingContainer = (LinearLayout) findViewById(R.id.crane_setting_container);
        int screenWidthPx = craneSettingContainer.getMeasuredWidth();
        context = getApplicationContext();
        screenWidth =  DisplayUtil.px2dip(context, screenWidthPx); // 转换为dp
        List<CranePara> paras = confLoad(context);
        paraTableRender(paras);

        setOnTouchListener();
    }
}
