package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    private ArrayList<Integer> gColId = null;

    private Activity activity = this;

    private FixedTitleTable table;

    private static String[] craneParaNames = new String[]{
        "自定义编号/custom no",
        "塔机类型/Crane Type",
        "X1坐标(米)/X1 Coordinate(m)",
        "Y1坐标(米)/Y1 Coordinate(m)",
        "X2偏移(米)/X2 Offset(m)",
        "Y2偏移(米)/X2 Offset(m)",
        "塔机高度(米)/Height(m)",
        "大臂长度(米)/Main Jib(m)",
        "平衡臂长度(米)/Counter Jib(m)",
        "塔身直径(米)/Crane Diameter(m)",
        "大臂宽度",
        "平衡臂宽度",
        "最大仰角(°)/Max Angle(°)",
        "最小仰角(°)/Min Angle(°)",
        "结构参数(米)/Arch Parameter(m)",
    };

    private static boolean[] craneParaVisible = new boolean[]{
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        true,
        false,
        false,
        true,
        true,
        true,
    };

    public void hideKeyboard() {
        try {
            View rootview = this.getWindow().getDecorView();
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(rootview.findFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    private List<Crane> confLoad(Context contex) {
        CraneDao dao = new CraneDao(contex);

        List<Crane> paras = dao.selectAll();
        return paras;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crane_setting);

        if (getSupportActionBar() != null) {
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

    private void saveCraneInfo() {
        CraneDao dao = new CraneDao(context);
        List<List<String>> gTable = table.getCurrData();
        AlertView alertView = new AlertView("保存塔机参数", "确定保存?", null,
            new String[]{"确定", "取消"}, null, activity,
            AlertView.Style.Alert, (o, position) -> {
            if (position == 0 && gTable != null) { // 确认
                for (int j = 0; j < gTable.get(0).size(); j++) {
                    int id = Integer.parseInt(gTable.get(0).get(j));
                    Crane cp = dao.queryById(id);
                    cp.setId(id);
                    cp.setName(String.format("%d号塔机", j + 1));
                    cp.setCustomNo(gTable.get(1).get(j).trim());
                    cp.setType(Integer.parseInt(gTable.get(2).get(j)));
                    cp.setCoordX1(Float.parseFloat(gTable.get(3).get(j)));
                    cp.setCoordY1(Float.parseFloat(gTable.get(4).get(j)));
                    cp.setCoordX2(Float.parseFloat(gTable.get(5).get(j)));
                    cp.setCoordY2(Float.parseFloat(gTable.get(6).get(j)));
                    cp.setCraneHeight(Float.parseFloat(gTable.get(7).get(j)));
                    cp.setBigArmLength(Float.parseFloat(gTable.get(8).get(j)));
                    cp.setBalancArmLength(Float.parseFloat(gTable.get(9).get(j)));
                    cp.setCraneBodyRadius(Float.parseFloat(gTable.get(10).get(j)));
                    cp.setBigArmWidth(Float.parseFloat(gTable.get(11).get(j)));
                    cp.setBalancArmWidth(Float.parseFloat(gTable.get(12).get(j)));
                    cp.setMaxAngle(Float.parseFloat(gTable.get(13).get(j)));
                    cp.setMinAngle(Float.parseFloat(gTable.get(14).get(j)));
                    cp.setArchPara(Float.parseFloat(gTable.get(15).get(j)));
                    dao.update(cp);
                    hideKeyboard();
                }
            }
        });
        alertView.show();
    }

    private void delCraneInfo() {
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
                showCranesInfo(paras);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addCraneInfo() {
        CraneDao dao = new CraneDao(context);
        int rowCnt = dao.selectAll().size();
        dao.insert(new Crane(
            false,
            0,
            String.format("%d号塔机/No.%02d", rowCnt + 1, rowCnt + 1),
            0,
            100,
            100,
            1,
            1,
            0,
            40,
            10,
            1,
            1,
            1)
        );
        List<Crane> paras = confLoad(context);
        try {
            showCranesInfo(paras);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListener(View view) {
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.add_logo) {
                    addCraneInfo();
                } else if (view.getId() == R.id.minus_logo) {
                    delCraneInfo();
                } else if (view.getId() == R.id.save_logo) { // 保存数据
                    saveCraneInfo();
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


    public void showCranesInfo(List<Crane> paras) throws Exception {
        table.init(this, null);
        table.clearAll();

        List<TableCell> colNames = new ArrayList() {{
            add(new TableCell(0, "参数类型/Parameter"));
        }};
        List<Integer> idList = new ArrayList() {{
            add(-1);
        }};

        for (int i = 0; i < paras.size(); i++) {
            colNames.add(new TableCell(0, String.format("%d号塔机/No.%d", i + 1, i + 1), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CraneDao dao = new CraneDao(context);
                    TextView textView = (TextView) view;

                    int id = textView.getId();
                    Crane selectedCrane = dao.queryById(id);

                    AlertView alertView = new AlertView("保存塔机参数", "确定" + selectedCrane.getName() + "绑定本终端?", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) { // 确认
                                List<Crane> craneList = dao.selectAll();
                                for (int j = 0; j < craneList.size(); j++) {
                                    Crane cp = craneList.get(j);
                                    if (id == cp.getId()) {
                                        cp.setIsMain(true);
                                    } else {
                                        cp.setIsMain(false);
                                    }
                                    dao.update(cp);
                                    table.setMainColumn(id);
                                }
                                hideKeyboard();
                            }
                        }
                    });
                    alertView.show();
                }
            }));
            idList.add(paras.get(i).getId());
        }

        table.setFirstRow(colNames, idList);

        for (int i = 0; i < paras.size(); i++) {
            if (paras.get(i).getIsMain() == true) {
                table.setMainColumn(paras.get(i).getId());
                System.out.printf("## I am main, my id : %d\n", i);
                break;
            }
        }

        for (int i = 0; i < craneParaNames.length; i++) { // 遍历每个参数, 每个参数对应一列
            List<TableCell> cells = new ArrayList();
            cells.add(new TableCell(0, craneParaNames[i]));
            for (int j = 0; j < paras.size(); j++) {
                switch (i) {
                    case 0:
                        cells.add(new TableCell(3, String.valueOf(paras.get(j).getCustomNo())));
                        break;

                    case 1:
                        JSONObject privData = new JSONObject();
                        JSONArray options = new JSONArray("[\"平臂式(flat-top)\", \"动臂式(luffing)\"]");
                        privData.put("options", options);
                        cells.add(new TableCell(2, String.valueOf(paras.get(j).getType()), privData));
                        break;
                    case 2:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getCoordX1())));
                        break;
                    case 3:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getCoordY1())));
                        break;
                    case 4:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getCoordX2())));
                        break;
                    case 5:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getCoordY2())));
                        break;
                    case 6:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getCraneHeight())));
                        break;
                    case 7:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getBigArmLength())));
                        break;
                    case 8:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getBalancArmLength())));
                        break;
                    case 9:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getCraneBodyRadius())));
                        break;
                    case 10:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getBigArmWidth())));
                        break;
                    case 11:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getBalancArmWidth())));
                        break;
                    case 12:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getMaxAngle())));
                        break;
                    case 13:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getMinAngle())));
                        break;
                    case 14:
                        cells.add(new TableCell(1, String.valueOf(paras.get(j).getArchPara())));
                        break;
                }
            }
            table.addDataRow(cells, craneParaVisible[i]);
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

        List<Crane> paras = confLoad(context);
        try {
            showCranesInfo(paras);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnTouchListener();
    }

//            "塔机类型/Crane Type",
//                "X1坐标(米)/X1 Coordinate(m)",
//                "Y1坐标(米)/Y1 Coordinate(m)",
//                "X2偏移(米)/X2 Offset(m)",
//                "Y2偏移(米)/X2 Offset(m)",
//                "塔机高度(米)/Height(m)",
//                "大臂长度(米)/Main Jib(m)",
//                "平衡臂长度(米)/Counter Jib(m)",
//                "塔身直径(米)/Crane Diameter(m)",
//                "大臂宽度",
//                "平衡臂宽度",
//                "最大仰角(°)/Max Angle(°)",
//                "最小仰角(°)/Min Angle(°)",
//                "结构参数(米)/Arch Parameter(m)",

    public static JSONObject getCraneConfig(CraneDao dao) {
        JSONObject data = new JSONObject();
        List<Crane> paras = dao.selectAll();

        try {
            for (Crane crane : paras) {
                if (crane.isMain()) {
                    data.put("no", crane.getId());
                    data.put("co", crane.getCustomNo());
                    data.put("ct", crane.getType());
                    data.put("xc", crane.getCoordX1());
                    data.put("yc", crane.getCoordY1());
                    data.put("xo", crane.getCoordX2());
                    data.put("yo", crane.getCoordX2());
                    data.put("ch", crane.getCraneHeight());
                    data.put("mjl", crane.getBigArmLength());
                    data.put("cjl", crane.getBalancArmLength());
                    data.put("cd", crane.getCraneBodyRadius());
                    data.put("mjw", crane.getBigArmWidth());
                    data.put("cjw", crane.getBalancArmWidth());
                    data.put("msa", crane.getMaxAngle());
                    data.put("mxa", crane.getMinAngle());
                    data.put("ap", crane.getArchPara());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;
    }
}
