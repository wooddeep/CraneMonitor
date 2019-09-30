package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.wooddeep.crane.logtable.CaliRecTable;
import com.wooddeep.crane.logtable.CtrlRecTable;
import com.wooddeep.crane.logtable.RealDataTable;
import com.wooddeep.crane.logtable.SwitchRecTable;
import com.wooddeep.crane.logtable.TableDesc;
import com.wooddeep.crane.logtable.WorkRecTable;
import com.wooddeep.crane.views.FixedTitleTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataRecord extends AppCompatActivity {

    private Context context;
    private FixedTitleTable table;
    private Activity activity = this;

    private TableDesc currTableDesc;
    private HashMap<Integer, TableDesc> recTableDescs = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_record);
        context = getApplicationContext();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    }

    private int[] backgroudIdList = new int[]{
        R.id.work_record,
        R.id.real_record,
        R.id.calibration_record,
        R.id.oper_record,
        R.id.switch_record,
    };

    private void setOnTouchListener(View view) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

                    for (int id : backgroudIdList) {
                        if (id == view.getId()) {
                            Drawable drawable = getResources().getDrawable(R.drawable.frame_border_selected);
                            findViewById(id).setBackground(drawable);
                        } else {
                            Drawable drawable = getResources().getDrawable(R.drawable.frame_border);
                            findViewById(id).setBackground(drawable);
                        }
                    }
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

                if (view.getId() == R.id.load_data) { // 导出数据库文件到 U盘
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
                    int count = (int) currTableDesc.getDao().queryCount();
                    currTableDesc.setGlobalIndex(count - currTableDesc.getPageSize());
                    showWorkRecInfo();
                } else if (view.getId() == R.id.next_page) { // 下一页
                    currTableDesc.setGlobalIndex(currTableDesc.getGlobalIndex() + currTableDesc.getPageSize());
                    showWorkRecInfo();
                } else if (view.getId() == R.id.first_page) { // 第一页
                    currTableDesc.setGlobalIndex(0);
                    showWorkRecInfo();
                } else if (view.getId() == R.id.prev_page) { // 上一页
                    currTableDesc.setGlobalIndex(currTableDesc.getGlobalIndex() - currTableDesc.getPageSize());
                    showWorkRecInfo();
                } else if (view.getId() == R.id.close_logo) { // 关闭
                    finish();
                } else if (view.getId() == R.id.work_record) { // 工作记录
                    currTableDesc = recTableDescs.get(R.id.work_record);
                    showWorkRecInfo();
                } else if (view.getId() == R.id.real_record) { // 实时记录
                    currTableDesc = recTableDescs.get(R.id.real_record);
                    showWorkRecInfo();
                } else if (view.getId() == R.id.calibration_record) { // 标定记录
                    currTableDesc = recTableDescs.get(R.id.calibration_record);
                    showWorkRecInfo();
                } else if (view.getId() == R.id.oper_record) { // 操作记录
                    currTableDesc = recTableDescs.get(R.id.oper_record);
                    showWorkRecInfo();
                } else if (view.getId() == R.id.switch_record) { // 开关机记录
                    currTableDesc = recTableDescs.get(R.id.switch_record);
                    showWorkRecInfo();
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

        List<LinearLayout> groups = new ArrayList<LinearLayout>() {{
            add((LinearLayout) findViewById(R.id.work_record));
            add((LinearLayout) findViewById(R.id.real_record));
            add((LinearLayout) findViewById(R.id.calibration_record));
            add((LinearLayout) findViewById(R.id.oper_record));
            add((LinearLayout) findViewById(R.id.switch_record));
        }};

        for (LinearLayout view : groups) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }

    public void showWorkRecInfo() {
        table.init(this, null);
        table.clearAll();
        if (currTableDesc.getWidthList() != null) {
            table.setFirstRow(currTableDesc.getColNames(), currTableDesc.getIdList(), currTableDesc.getWidthList());
        } else {
            table.setFirstRow(currTableDesc.getColNames(), currTableDesc.getIdList());
        }
        currTableDesc.showDataInfo(table);
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        table = new FixedTitleTable(dm.widthPixels); // 输入屏幕宽度

        recTableDescs.put(R.id.work_record, new WorkRecTable(context));
        recTableDescs.put(R.id.real_record, new RealDataTable(context));
        recTableDescs.put(R.id.calibration_record, new CaliRecTable(context));
        recTableDescs.put(R.id.oper_record, new CtrlRecTable(context));
        recTableDescs.put(R.id.switch_record, new SwitchRecTable(context, dm.widthPixels));
        currTableDesc = recTableDescs.get(R.id.work_record);

        try {
            showWorkRecInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnTouchListener();
    }
}
