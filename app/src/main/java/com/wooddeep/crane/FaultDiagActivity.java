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
import com.wooddeep.crane.ebus.SysParaEvent;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.LoadDbHelper;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.dao.TcParamDao;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.persist.entity.TcParam;
import com.wooddeep.crane.tookit.DrawTool;
import com.wooddeep.crane.tookit.EdbTool;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

//import com.wooddeep.crane.persist.dao.LoadDao;

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

public class FaultDiagActivity extends AppCompatActivity {
    private Context context;
    private FixedTitleTable table;
    //private int screenWidth = 400; // dp

    private Activity activity = this;

    private TcParamDao loadDao;
    private SysParaDao paraDao;

    private List<String> craneTypes = new ArrayList<>();
    private List<String> armLengths = new ArrayList<>();
    private List<String> cables = new ArrayList<>();

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

    private List<TcParam> confLoad(Context contex) {
        LoadDbHelper.getInstance(contex).createTable(TcParam.class);
        DatabaseHelper.getInstance(contex).createTable(SysPara.class);
        //loadDao = new TcParamDao(contex);
        List<TcParam> paras = loadDao.selectAll();

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

    // 在SD卡目录下的文件，写入内容
    private void write(File file) throws Exception {
        FileWriter fw = new FileWriter(file);
        fw.write("我的sdcard内容.....");
        fw.close();
        Toast.makeText(FaultDiagActivity.this, "SD卡写入内容完成...", Toast.LENGTH_LONG).show();
        Log.d("LoadAttribute", "SD卡写入内容完成...");
    }

    // 读取SD卡文件里面的内容
    private void read() throws Exception {
        FileReader fr = new FileReader("/mnt/sdcard/mysdcard.txt");
        BufferedReader r = new BufferedReader(fr);
        String result = r.readLine();
        Log.d("LoadAttribute", "SD卡文件里面的内容:" + result);
    }

    private int getIndex(List<String> list, String value) {
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fault_diag);
        context = getApplicationContext();
        verifyStoragePermissions(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        try {
            createConfIfNotExist();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadDao = new TcParamDao(getApplicationContext());
        paraDao = new SysParaDao(getApplicationContext());
        confLoad(getApplicationContext());
    }

    public List<TcParam> queryLoadByCondition() {

        MaterialSpinner craneTypeSpinner = (MaterialSpinner) findViewById(R.id.crane_type_option);
        MaterialSpinner armLenSpinner = (MaterialSpinner) findViewById(R.id.arm_length_option);
        MaterialSpinner cableNumSpinner = (MaterialSpinner) findViewById(R.id.rope_num_option);

        String craneType = craneTypeSpinner.getText().toString();
        String armLength = armLenSpinner.getText().toString();
        String cableNum = cableNumSpinner.getText().toString();

        System.out.printf("%s-%s-%s\n", craneType, armLength, cableNum);

        //loadDao = new TcParamDao(getApplicationContext());
        return loadDao.getLoads(craneType, armLength, cableNum);

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
        //view.setOnTouchListener(onTouchListener);
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
            add(new TableCell(0, "故障编号"));
            add(new TableCell(0, "故障编码"));
            add(new TableCell(0, "故障描述"));
        }};

        List<Integer> idList = new ArrayList() {{
            add(-1);
            add(-1);
            add(-1);
        }};


         List<Integer> widthList = new ArrayList() {{
            add(200); // ID
            add(200); // 时间
            add(width - 400);
        }};


        table.setFirstRow(colNames, idList, widthList);
        /*
        ArrayList<TableCell> row = new ArrayList<TableCell>();
        row.add(new TableCell(0, "1"));
        row.add(new TableCell(0, "0001"));
        row.add(new TableCell(0, "故障A0/故障A1"));
        table.addDataRow(row, true);
        */
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
