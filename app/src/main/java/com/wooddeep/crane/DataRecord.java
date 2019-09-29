package com.wooddeep.crane;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rmondjone.locktableview.DataCell;
import com.wooddeep.crane.ebus.SysParaEvent;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.LoadDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.dao.WorkRecDao;
import com.wooddeep.crane.persist.entity.Load;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.persist.entity.WorkRecrod;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private int screenWidth = 400; // dp

    private ArrayList<ArrayList<DataCell>> gTable = null;
    private ArrayList<Integer> gColId = null;

    private Activity activity = this;

    private List<String> craneTypes = new ArrayList<>();
    private List<String> armLengths = new ArrayList<>();
    private List<String> cables = new ArrayList<>();

    private WorkRecDao workRecDao;

    /**
     *  *
     * 检查应用程序是否允许写入存储设备
     * <p>
     *  *
     *  *
     * 如果应用程序不允许那么会提示用户授予权限
     * <p>
     *  *
     *  * @param activity
     *  
     */

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void loadDefautAttr(Context contex) {
        LoadDao dao = new LoadDao(contex);
        InputStream is = context.getResources().openRawResource(R.raw.load_attr); // 暂时放在这里
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        try {
            dao.deleteAll();
            String str;
            Load load = new Load();
            while ((str = bufferedReader.readLine()) != null) {
                //System.out.println(str);
                String[] cells = str.split(",");
                if (cells.length != 5) continue;
                // D5523, 4	,50	,0 ,10
                load.setCraneType(cells[0].trim());
                load.setPower(cells[1].trim());
                load.setArmLength(cells[2].trim());
                load.setCoordinate(cells[3].trim());
                load.setWeight(cells[4].trim());
                dao.insert(load);
            }
            craneTypes = dao.getCraneTypes();
            armLengths = dao.getArmLengths(craneTypes.get(0));
            cables = dao.getCables(craneTypes.get(0), armLengths.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<WorkRecrod> confWorkRecordLoad(Context contex) {
        DatabaseHelper.getInstance(contex).createTable(WorkRecrod.class);
        WorkRecDao dao = new WorkRecDao(contex);
        List<WorkRecrod> paras = dao.selectAll();
        if (paras == null || paras.size() <= 1) {
            dao.deleteAll();
        }
        paras = dao.selectAll();

        return paras;
    }

    // // 在SD卡目录下创建文件
    private void createConfIfNotExist() throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(), "load_conf.csv");
        Log.d("LoadAttribute", "file.exists():" + file.exists() + " file.getAbsolutePath():" + file.getAbsolutePath());
        if (!file.exists()) {
            //file.delete();
            file.createNewFile();
        }
        //Toast.makeText(LoadAttribute.this, "SD卡目录下创建文件成功...", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_record);
        context = getApplicationContext();
        verifyStoragePermissions(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        workRecDao = new WorkRecDao(context);

        //confWorkRecordLoad(getApplicationContext());
        //LoadDao loadDao = new LoadDao(getApplicationContext());
        //SysParaDao paraDao = new SysParaDao(getApplicationContext());
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
                    LoadDao dao = new LoadDao(context);
                    AlertView alertView = new AlertView("加载负荷特性参数", "", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) {
                                InputStream is = context.getResources().openRawResource(R.raw.load_attr); // 暂时放在这里
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                                try {
                                    dao.deleteAll();
                                    String str;
                                    Load load = new Load();
                                    while ((str = bufferedReader.readLine()) != null) {
                                        //System.out.println(str);
                                        String[] cells = str.split(",");
                                        if (cells.length != 5) continue;
                                        // D5523, 4	,50	,0 ,10
                                        load.setCraneType(cells[0].trim());
                                        load.setPower(cells[1].trim());
                                        load.setArmLength(cells[2].trim());
                                        load.setCoordinate(cells[3].trim());
                                        load.setWeight(cells[4].trim());
                                        dao.insert(load);
                                    }

                                    craneTypes = dao.getCraneTypes();
                                    armLengths = dao.getArmLengths(craneTypes.get(0));
                                    cables = dao.getCables(craneTypes.get(0), armLengths.get(0));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    alertView.show();
                } else if (view.getId() == R.id.save_data) { // 保持数据

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
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }


    // 头部信息  // ID, 时间，倍率，力矩，高度，幅度，额定重量，重量，回转，行走，仰角，风速，备注
    private ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
        add(new TableCell(0, "编号/ID"));
        add(new TableCell(0, "时间/Time"));
        add(new TableCell(0, "倍率/Time"));
        add(new TableCell(0, "力矩/Time"));
        add(new TableCell(0, "高度/Time"));
        add(new TableCell(0, "幅度/Time"));
        add(new TableCell(0, "额重/Time"));
        add(new TableCell(0, "时间/Time"));
        add(new TableCell(0, "回转/Time"));
        add(new TableCell(0, "行走/Time"));
        add(new TableCell(0, "仰角/Time"));
        add(new TableCell(0, "风速/Time"));
        add(new TableCell(0, "备注/Time"));
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

    public void showWorkRecInfo() {
        table.init(this);
        table.clearAll();

        table.setFirstRow(colNames, idList);

        List<WorkRecrod> workRecrods = workRecDao.queryPage(0, 10);

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

            table.addDataRow(row, true);
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
