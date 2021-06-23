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
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.LoadDbHelper;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.dao.TcParamDao;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.persist.entity.TcParam;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import java.util.ArrayList;
import java.util.List;


public class CameraActivity extends AppCompatActivity {
    private Context context;
    private FixedTitleTable table;
    //private int screenWidth = 400; // dp

    private Activity activity = this;

    private TcParamDao loadDao;
    private SysParaDao paraDao;



    private List<TcParam> confLoad(Context contex) {
        LoadDbHelper.getInstance(contex).createTable(TcParam.class);
        DatabaseHelper.getInstance(contex).createTable(SysPara.class);
        //loadDao = new TcParamDao(contex);
        List<TcParam> paras = loadDao.selectAll();

        return paras;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_conf);
        context = getApplicationContext();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loadDao = new TcParamDao(getApplicationContext());
        paraDao = new SysParaDao(getApplicationContext());
        confLoad(getApplicationContext());
    }

    private void setOnTouchListener(View view) {
        View.OnTouchListener onTouchListener = (v, event) -> {
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
        };
        view.setOnTouchListener(onTouchListener);
    }

    AlertView gAlertView = null;

    private void setOnClickListener(View view) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.close_logo) {
                    finish();
                }
            }
        };

        view.setOnClickListener(onClickListener);
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

    public void showDiagnosisInfo(int width) {
        table.init(this, null);
        table.clearAll();

        // 头部信息
        ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
            add(new TableCell(0, "编号"));
            add(new TableCell(0, "摄像头IP"));
            add(new TableCell(0, "摄像头端口"));
            add(new TableCell(0, "测试"));
        }};

        List<Integer> idList = new ArrayList() {{
            add(-1);
            add(-1);
            add(-1);
            add(-1);
        }};

        table.setFirstRow(colNames, idList);

        ArrayList<TableCell> row = new ArrayList<TableCell>();
        row.add(new TableCell(0, "1"));
        row.add(new TableCell(1, "127.0.0.1"));
        row.add(new TableCell(1, "8445"));
        row.add(new TableCell(5, "打开"));
        table.addDataRow(row, true);

        /*
        // 数据信息
        List<TcParam> loads = queryLoadByCondition();
        for (TcParam load : loads) {
            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, load.getCoordinate()));
            row.add(new TableCell(0, load.getWeight()));
            table.addDataRow(row, true);
        }
        */

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
            showDiagnosisInfo(dm.widthPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnTouchListener();
    }
}
