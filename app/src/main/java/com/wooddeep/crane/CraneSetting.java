package com.wooddeep.crane;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.ProgressStyle;
import com.rmondjone.xrecyclerview.XRecyclerView;
import com.wooddeep.crane.persist.dao.CraneParaDao;
import com.wooddeep.crane.persist.dao.StudentDao;
import com.wooddeep.crane.persist.entity.CranePara;
import com.wooddeep.crane.persist.entity.Student;

import java.util.ArrayList;

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

    private void confLoad(Context contex) {
        CraneParaDao dao = new CraneParaDao(contex);
        //dao.insert(new CranePara(0,"zhangsan", 1, 1, 1, 1, 1, 1, 1, 1, 1, 1));
        dao.getAllCranePara();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crane_setting);

        Context context = getApplicationContext();
        confLoad(context);

        paraTable();
    }

    public void paraTable() {
        LinearLayout craneSettingContainer = (LinearLayout) findViewById(R.id.crane_setting_container);

        ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
        ArrayList<String> head = new ArrayList<String>() {{
            add("参数类型");
            add("01号塔基");
            add("02号塔基");
            add("03号塔基");
            add("04号塔基");
            add("05号塔基");
            add("06号塔基");
            add("07号塔基");
            add("08号塔基");
            add("09号塔基");
            add("10号塔基");
            add("11号塔基");

        }};
        table.add(head);

        ArrayList<String> row = new ArrayList<String>() {{
            add("X1(塔基X坐标)");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
        }};
        table.add(row);

        ArrayList<String> row1 = new ArrayList<String>() {{
            add("Y1(塔基Y坐标)");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
        }};
        table.add(row1);

        ArrayList<String> row2 = new ArrayList<String>() {{
            add("X2(塔基X偏移)");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
        }};
        table.add(row2);

        ArrayList<String> row3 = new ArrayList<String>() {{
            add("Y2(塔基Y偏移)");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
            add("a");
            add("b");
        }};
        table.add(row3);


        final LockTableView mLockTableView = new LockTableView(this, craneSettingContainer, table);
        Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
        mLockTableView.setLockFristColumn(true) //是否锁定第一列
        .setLockFristRow(true) //是否锁定第一行
        .setMaxColumnWidth(100) //列最大宽度
        .setMinColumnWidth(60) //列最小宽度
        .setColumnWidth(1, 60) //设置指定列文本宽度(从0开始计算,宽度单位dp)
        .setMinRowHeight(20)//行最小高度
        .setMaxRowHeight(60)//行最大高度
        .setTextViewSize(16) //单元格字体大小
        .setCellPadding(15)//设置单元格内边距(dp)
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
            //下拉刷新、上拉加载监听
            @Override
            public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                //Log.e("表格主视图", mXRecyclerView);
                //Log.e("表格所有数据", mTableDatas);
                //如需更新表格数据调用,部分刷新不会全部重绘
                mLockTableView.setTableDatas(mTableDatas);
                //停止刷新
                //mXRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                //Log.e("表格主视图", mXRecyclerView);
                //Log.e("表格所有数据", mTableDatas);
                //如需更新表格数据调用,部分刷新不会全部重绘
                mLockTableView.setTableDatas(mTableDatas);
                //停止刷新
                //mXRecyclerView.loadMoreComplete();
                //如果没有更多数据调用
                //mXRecyclerView.setNoMore(true);
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
        .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
        .show(); //显示表格,此方法必须调用
        //mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
        //mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
        //mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.SquareSpin);//属性值获取
        Log.e("每列最大宽度(dp)", mLockTableView.getColumnMaxWidths().toString());
        Log.e("每行最大高度(dp)", mLockTableView.getRowMaxHeights().toString());
        Log.e("表格所有的滚动视图", mLockTableView.getScrollViews().toString());
        Log.e("表格头部固定视图(锁列)", mLockTableView.getLockHeadView().toString());
        Log.e("表格头部固定视图(不锁列)", mLockTableView.getUnLockHeadView().toString());

    }

}
