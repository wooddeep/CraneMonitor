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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.ProtectDao;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.Protect;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

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

public class ProtectSetting extends AppCompatActivity {
    private Context context;
    private Activity activity = this;

    private static String[] craneParaNames = new String[]{
        "高度(米)/height(m)",
        "X1(m)",
        "Y1(m)",
        "X2(m)",
        "Y2(m)",
        "X3(m)",
        "Y3(m)",
        "X4(m)",
        "Y4(m)",
        "X5(m)",
        "Y5(m)",
        "X6(m)",
        "Y6(m)",
    };

    private List<Protect> confLoad(Context contex) {
        ProtectDao dao = new ProtectDao(contex);
        List<Protect> paras = dao.selectAll();
        return paras;
    }

    private FixedTitleTable table;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.protect_area);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setAutoCoordCalc();
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
                    ProtectDao dao = new ProtectDao(context);
                    dao.insert(Protect.getInitData());
                    List<Protect> paras = confLoad(context);
                    try {
                        showProtectInfo(paras);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (view.getId() == R.id.minus_logo) {
                    List<Protect> paras = confLoad(context);
                    if (paras.size() <= 1) {
                        Toast toast = Toast.makeText(ProtectSetting.this, "不能全删除(can't remove all)!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    ProtectDao dao = new ProtectDao(context);
                    dao.delete(paras.get(paras.size() - 1));
                    paras = confLoad(context);
                    try {
                        showProtectInfo(paras);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (view.getId() == R.id.save_logo) { // 保存数据

                    ProtectDao dao = new ProtectDao(context);
                    List<List<String>> gTable = table.getCurrData();

                    AlertView alertView = new AlertView("保存区域参数", "", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {

                            if (position == 0) { // 确认
                                for (int j = 0; j < gTable.get(j).size(); j++) {
                                    int id = Integer.parseInt(gTable.get(0).get(j));
                                    Protect cp = dao.queryById(id);
                                    cp.setId(id);
                                    cp.setHeight(Float.parseFloat(gTable.get(1).get(j)));
                                    cp.setX1(Float.parseFloat(gTable.get(2).get(j)));
                                    cp.setY1(Float.parseFloat(gTable.get(3).get(j)));
                                    cp.setX2(Float.parseFloat(gTable.get(4).get(j)));
                                    cp.setY2(Float.parseFloat(gTable.get(5).get(j)));
                                    cp.setX3(Float.parseFloat(gTable.get(6).get(j)));
                                    cp.setY3(Float.parseFloat(gTable.get(7).get(j)));
                                    cp.setX4(Float.parseFloat(gTable.get(8).get(j)));
                                    cp.setY4(Float.parseFloat(gTable.get(9).get(j)));
                                    cp.setX5(Float.parseFloat(gTable.get(10).get(j)));
                                    cp.setY5(Float.parseFloat(gTable.get(11).get(j)));
                                    cp.setX6(Float.parseFloat(gTable.get(12).get(j)));
                                    cp.setY6(Float.parseFloat(gTable.get(13).get(j)));
                                    dao.update(cp);
                                }
                            }
                            hideKeyboard();
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

    public void showProtectInfo(List<Protect> paras) throws Exception {
        table.init(this, null);
        table.clearAll();

        ArrayList<TableCell> head = new ArrayList<TableCell>() {{
            add(new TableCell(0, "参数类型/Parameter"));
        }};

        List<Integer> idList = new ArrayList() {{
            add(-1);
        }};

        for (int i = 0; i < paras.size(); i++) {
            idList.add(paras.get(i).getId());
            head.add(new TableCell(0, String.format("%02d号区域/No.%02d", i + 1, i + 1)));
        }

        table.setFirstRow(head, idList, null);

        for (int i = 0; i < craneParaNames.length; i++) {
            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, craneParaNames[i]));
            for (int j = 0; j < paras.size(); j++) {
                switch (i) {
                    case 0:
                        JSONObject privData = new JSONObject();
                        row.add(new TableCell(1, paras.get(j).getHeight() + "", privData));
                        break;
                    case 1:
                        row.add(new TableCell(1, paras.get(j).getX1() + ""));
                        break;
                    case 2:
                        row.add(new TableCell(1, paras.get(j).getY1() + ""));
                        break;
                    case 3:
                        row.add(new TableCell(1, paras.get(j).getX2() + ""));
                        break;
                    case 4:
                        row.add(new TableCell(1, paras.get(j).getY2() + ""));
                        break;
                    case 5:
                        row.add(new TableCell(1, paras.get(j).getX3() + ""));
                        break;
                    case 6:
                        row.add(new TableCell(1, paras.get(j).getY3() + ""));
                        break;
                    case 7:
                        row.add(new TableCell(1, paras.get(j).getX4() + ""));
                        break;
                    case 8:
                        row.add(new TableCell(1, paras.get(j).getY4() + ""));
                        break;
                    case 9:
                        row.add(new TableCell(1, paras.get(j).getX5() + ""));
                        break;
                    case 10:
                        row.add(new TableCell(1, paras.get(j).getY5() + ""));
                        break;
                    case 11:
                        row.add(new TableCell(1, paras.get(j).getX6() + ""));
                        break;
                    case 12:
                        row.add(new TableCell(1, paras.get(j).getY6() + ""));
                        break;
                }
            }
            table.addDataRow(row, null, true);
        }

    }

    public static JSONArray getProtectConfig(ProtectDao dao) {
        JSONArray out = new JSONArray();
        List<Protect> paras = dao.selectAll();

        try {
            for (Protect protect : paras) {
                JSONObject data = new JSONObject();
                data.put("id", protect.getId());
                data.put("height", protect.getHeight());
                data.put("x1", protect.getX1());
                data.put("y1", protect.getY1());
                data.put("x2", protect.getX2());
                data.put("y2", protect.getY2());
                data.put("x3", protect.getX3());
                data.put("y3", protect.getY3());
                data.put("x4", protect.getX4());
                data.put("y4", protect.getY4());
                data.put("x5", protect.getX5());
                data.put("y5", protect.getY5());
                data.put("x6", protect.getX6());
                data.put("y6", protect.getY6());
                out.put(data);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return out;
    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        table = new FixedTitleTable(dm.widthPixels); // 输入屏幕宽度

        List<Protect> paras = confLoad(context);
        try {
            showProtectInfo(paras);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnTouchListener();
    }


    private int[] coorBtnIdList = new int[]{
        R.id.btn_coord1,
        R.id.btn_coord2,
        R.id.btn_coord3,
        R.id.btn_coord4,
        R.id.btn_coord5,
        R.id.btn_coord6,
    };

    private void setAutoCoordCalc() {

        findViewById(R.id.btn_area_no_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int areaNo = Integer.parseInt(((Button) findViewById(R.id.btn_area_no_show)).getText().toString());
                if (areaNo >= table.getColNum()) return;
                ((Button) findViewById(R.id.btn_area_no_show)).setText(String.valueOf(areaNo + 1));
            }
        });

        findViewById(R.id.btn_area_no_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int areaNo = Integer.parseInt(((Button) findViewById(R.id.btn_area_no_show)).getText().toString());
                if (areaNo == 1) return;
                ((Button) findViewById(R.id.btn_area_no_show)).setText(String.valueOf(areaNo - 1));
            }
        });

        for (int i = 0; i < coorBtnIdList.length; i++) {
            View view = findViewById(coorBtnIdList[i]);
            System.out.println(view);
            view.setId(i); // 0, 1, 2, 3, 4, 5 ~ 坐标编号
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int areaNo = Integer.parseInt(((Button) findViewById(R.id.btn_area_no_show)).getText().toString()) - 1;
                    float cx = MainActivity.showData.getCoordX();
                    float cy = MainActivity.showData.getCoordY();
                    float angle = MainActivity.showData.getShowHAngle();
                    float range = MainActivity.showData.getShadowRange();

                    float x = cx + range * (float) Math.cos(Math.toRadians(angle));
                    float y = cy + range * (float) Math.sin(Math.toRadians(angle));

                    List<View> viewList = table.getColumn(areaNo);

                    // 0 -> x:1, y,2; 1 -> x:3, y,4
                    int id = v.getId();
                    int xIndex = id * 2 + 1;
                    int yIndex = id * 2 + 2;

                    EditText etX = (EditText) viewList.get(xIndex);
                    EditText etY = (EditText) viewList.get(yIndex);

                    etX.setText(String.valueOf(x));
                    etY.setText(String.valueOf(y));
                    /*
                    for (int i = 0; i < viewList.size(); i++) {
                        EditText et = (EditText) viewList.get(i);
                        System.out.println(et.getText().toString());
                    }
                    */
                }
            });
        }
    }
}
