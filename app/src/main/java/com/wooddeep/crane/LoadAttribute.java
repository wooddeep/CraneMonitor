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
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rmondjone.locktableview.DataCell;
import com.rmondjone.locktableview.DisplayUtil;
import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.XRecyclerView;
import com.wooddeep.crane.ebus.SysParaEvent;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.LoadDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.Load;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

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

public class LoadAttribute extends AppCompatActivity {
    private Context context;
    private FixedTitleTable table;
    private int screenWidth = 400; // dp

    private ArrayList<ArrayList<DataCell>> gTable = null;
    private ArrayList<Integer> gColId = null;

    private Activity activity = this;

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

    private List<Load> confLoad(Context contex) {
        DatabaseHelper.getInstance(contex).createTable(Load.class);
        DatabaseHelper.getInstance(contex).createTable(SysPara.class);
        LoadDao dao = new LoadDao(contex);
        List<Load> paras = dao.selectAll();
        if (paras == null || paras.size() <= 1) {
            dao.deleteAll();
            loadDefautAttr(contex); // 加载配置文件中的配置
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

    // 在SD卡目录下的文件，写入内容
    private void write(File file) throws Exception {
        FileWriter fw = new FileWriter(file);
        fw.write("我的sdcard内容.....");
        fw.close();
        Toast.makeText(LoadAttribute.this, "SD卡写入内容完成...", Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.load_attribute);
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

        confLoad(getApplicationContext());
        LoadDao loadDao = new LoadDao(getApplicationContext());
        SysParaDao paraDao = new SysParaDao(getApplicationContext());

        String savedCraneType = paraDao.queryValueByName("craneType");  // 塔基类型
        String savedAramLength = paraDao.queryValueByName("armLength"); // 臂长
        String savedPower = paraDao.queryValueByName("power"); // 倍率

        craneTypes = loadDao.getCraneTypes();
        String craneType = savedCraneType != null ? savedCraneType : craneTypes.get(0);
        armLengths = loadDao.getArmLengths(craneType);
        String armLength = savedAramLength != null ? savedAramLength : armLengths.get(0);
        cables = loadDao.getCables(craneType, armLength);
        String power = savedPower != null ? savedPower : cables.get(0);
        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.crane_type_option); // 塔基类型
        MaterialSpinner armLenSpinner = (MaterialSpinner) findViewById(R.id.arm_length_option); // 臂长
        MaterialSpinner cableSpiner = (MaterialSpinner) findViewById(R.id.rope_num_option); // 吊绳倍率

        spinner.setItems(craneTypes);
        spinner.setSelectedIndex(getIndex(craneTypes, craneType));
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String currCraneType) {
                armLengths = loadDao.getArmLengths(currCraneType);
                armLenSpinner.setItems(armLengths);
                armLenSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String currArmLen) {
                        cables = loadDao.getCables(currCraneType, currArmLen);
                        cableSpiner.setItems(cables);
                        cableSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                            @Override
                            public void onItemSelected(MaterialSpinner view, int position, long id, String currPower) {
                                // TODO
                            }
                        });
                    }
                });

                //List<Load> paras = confLoad(context);
                showLoadInfo(); // 渲染出表格
            }
        });

        armLenSpinner.setItems(armLengths);
        armLenSpinner.setSelectedIndex(getIndex(armLengths, armLength));
        armLenSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String currArmLen) {
                String currCraneType = spinner.getText().toString();//spinner
                cables = loadDao.getCables(currCraneType, currArmLen);
                cableSpiner.setItems(cables);
                cableSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String currPower) {
                        // TODO
                    }
                });
                //List<Load> paras = confLoad(context);
                showLoadInfo(); // 渲染出表格
            }
        });

        cableSpiner.setItems(cables);
        cableSpiner.setSelectedIndex(getIndex(cables, power));
        cableSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                //List<Load> paras = confLoad(context);
                showLoadInfo(); // 渲染出表格
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

                    AlertView alertView = new AlertView("保存负荷特性参数", "", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) {
                                MaterialSpinner craneTypeSpinner = (MaterialSpinner) findViewById(R.id.crane_type_option);
                                MaterialSpinner armLenSpinner = (MaterialSpinner) findViewById(R.id.arm_length_option);
                                MaterialSpinner cableNumSpinner = (MaterialSpinner) findViewById(R.id.rope_num_option);
                                String craneType = craneTypeSpinner.getText().toString();
                                String armLength = armLenSpinner.getText().toString();
                                String cableNum = cableNumSpinner.getText().toString();
                                SysParaDao sysParadao = new SysParaDao(getApplicationContext());

                                SysPara para = sysParadao.queryParaByName("craneType");
                                if (para == null) {
                                    para = new SysPara("craneType", craneType);
                                    sysParadao.insert(para);
                                } else {
                                    para.setParaValue(craneType);
                                    sysParadao.update(para);
                                }

                                para = sysParadao.queryParaByName("armLength");
                                if (para == null) {
                                    para = new SysPara("armLength", armLength);
                                    sysParadao.insert(para);
                                } else {
                                    para.setParaValue(armLength);
                                    sysParadao.update(para);
                                }

                                para = sysParadao.queryParaByName("power");
                                if (para == null) {
                                    para = new SysPara("power", cableNum);
                                    sysParadao.insert(para);
                                } else {
                                    para.setParaValue(cableNum);
                                    sysParadao.update(para);
                                }

                                EventBus.getDefault().post(new SysParaEvent(craneType, armLength, cableNum)); // 触发系统参数相关
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
            add((ImageView) findViewById(R.id.load_data));
            add((ImageView) findViewById(R.id.close_logo));
            add((ImageView) findViewById(R.id.save_data));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }


    public void showLoadInfo() {
        table.init(this);
        table.clearAll();

        // 头部信息
        ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
            add(new TableCell(0, "小车坐标"));
            add(new TableCell(0, "额定吊重"));
        }};

        List<Integer> idList = new ArrayList() {{
            add(-1);
            add(-1);
        }};

        table.setFirstRow(colNames, idList);

        // 数据信息
        List<Load> loads = queryLoadByCondition();
        for (Load load : loads) {
            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, load.getCoordinate()));
            row.add(new TableCell(0, load.getWeight()));
            table.addDataRow(row);
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

        //List<Load> paras = confLoad(context);
        try {
            showLoadInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnTouchListener();
    }
}
