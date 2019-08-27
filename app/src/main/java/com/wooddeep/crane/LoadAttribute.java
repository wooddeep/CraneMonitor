package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.LoadDao;
import com.wooddeep.crane.persist.entity.Load;

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

    private int screenWidth = 400; // dp

    private ArrayList<ArrayList<DataCell>> gTable = null;
    private ArrayList<Integer> gColId = null;

    private Activity activity = this;

    private List<String> craneTypes = new ArrayList<>();
    private List<String> armLengths =  new ArrayList<>();
    private List<String> cables =  new ArrayList<>();

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
        LoadDao dao = new LoadDao(contex);
        List<Load> paras = dao.selectAll();
        if (paras == null || paras.size() == 0) {
            Load load = new Load();
            load.setCraneType("X");
            load.setPower("-1");
            load.setArmLength("-1");
            load.setCoordinate("-1");
            load.setWeight("-1");
            dao.insert(load);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_attribute);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        try {
            createConfIfNotExist();
        } catch (Exception e) {
            e.printStackTrace();
        }

        confLoad(getApplicationContext());
        LoadDao dao = new LoadDao(getApplicationContext());
        Load savedLoad = dao.getSaveLoad();

        craneTypes = dao.getCraneTypes();
        String craneType = craneTypes.get(0);
        if (savedLoad != null) craneType = savedLoad.getCraneType();
        armLengths = dao.getArmLengths(craneType);
        String armLength = armLengths.get(0);
        if (savedLoad != null) armLength = savedLoad.getArmLength();
        cables = dao.getCables(craneTypes.get(0), armLength);

        MaterialSpinner spinner = (MaterialSpinner) findViewById(R.id.crane_type_option); // 塔基类型
        MaterialSpinner armLenSpinner = (MaterialSpinner) findViewById(R.id.arm_length_option); // 臂长
        MaterialSpinner cableSpiner = (MaterialSpinner) findViewById(R.id.rope_num_option); // 吊绳倍率

        spinner.setText(craneType);
        armLenSpinner.setText(armLength);
        if (savedLoad != null) cableSpiner.setText(savedLoad.getPower());

        spinner.setItems(craneTypes);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String currCraneType) {
                armLengths = dao.getArmLengths(currCraneType);
                armLenSpinner.setItems(armLengths);
                armLenSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String currArmLen) {
                        cables = dao.getCables(currCraneType, currArmLen);
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
                paraTableRender(); // 渲染出表格
            }
        });

        armLenSpinner.setItems(armLengths);
        armLenSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String currArmLen) {
                String currCraneType = spinner.getText().toString();//spinner
                cables = dao.getCables(currCraneType, currArmLen);
                cableSpiner.setItems(cables);
                cableSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String currPower) {
                        // TODO
                    }
                });
                List<Load> paras = confLoad(context);
                paraTableRender(); // 渲染出表格
            }
        });

        cableSpiner.setItems(cables);
        cableSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                List<Load> paras = confLoad(context);
                paraTableRender(); // 渲染出表格
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

                                System.out.printf("%s-%s-%s\n", craneType, armLength, cableNum);
                                LoadDao dao = new LoadDao(getApplicationContext());
                                Load saveLoad = new Load();
                                saveLoad.setCraneType(craneType);
                                saveLoad.setArmLength(armLength);
                                saveLoad.setPower(cableNum);
                                saveLoad.setId(0);
                                dao.update(saveLoad);
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


    public ArrayList<ArrayList<DataCell>> areaParaArrange() {

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
            //System.out.printf("@@@@@@@@@@@@@@@@@@@@ : %s\n", load.getCoordinate());
            table.add(row);

        }

        return table;
    }

    public void paraTableRender() {

        LinearLayout loadAttrContainer = (LinearLayout) findViewById(R.id.load_attri_container);
        ArrayList<ArrayList<DataCell>> table = areaParaArrange();

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
        paraTableRender(); // 渲染出表格

        setOnTouchListener();
    }
}
