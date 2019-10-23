package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.wooddeep.crane.ebus.AlarmSetEvent;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.AlarmSetDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.SysPara;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@SuppressWarnings("unused")
public class AlarmSetting extends AppCompatActivity {

    private SysParaDao sysParaDao;

    class AlarmCell {
        public String propName;
        public int editTextId;
        public Object value;

        public AlarmCell(String pn, int eti, Object v) {
            this.propName = pn;
            this.editTextId = eti;
            this.value = v;
        }
    }

    private AlarmCell[] alarmCells = new AlarmCell[]{

        // 塔基与塔基告警
        new AlarmCell("t2tDistGear1", R.id.et_gear1, 0),
        new AlarmCell("t2tDistGear2", R.id.et_gear2, 0),
        new AlarmCell("t2tDistGear3", R.id.et_gear3, 0),
        new AlarmCell("t2tDistGear4", R.id.et_gear4, 0),
        new AlarmCell("t2tDistGear5", R.id.et_gear5, 0),

        // 塔基与区域告警
        new AlarmCell("t2cDistGear1", R.id.et_ta_gear1, 0),
        new AlarmCell("t2cDistGear2", R.id.et_ta_gear2, 0),
        new AlarmCell("t2cDistGear3", R.id.et_ta_gear3, 0),
        new AlarmCell("t2cDistGear4", R.id.et_ta_gear4, 0),
        new AlarmCell("t2cDistGear5", R.id.et_ta_gear5, 0),

        // 小车变幅减速距离, 停车距离, 力矩
        new AlarmCell("carSpeedDownDist", R.id.et_hook_dist1, 0),
        new AlarmCell("carStopDist", R.id.et_hook_dist2, 0),
        new AlarmCell("moment1", R.id.et_moment1, 0),
        new AlarmCell("moment2", R.id.et_moment2, 0),
        new AlarmCell("moment3", R.id.et_moment3, 0),

        // 风速 吊重百分比
        new AlarmCell("windSpeed1", R.id.et_wind_speed1, 0),
        new AlarmCell("windSpeed2", R.id.et_wind_speed2, 0),
        new AlarmCell("weight1", R.id.et_weight_gear1, 0),
        new AlarmCell("weight2", R.id.et_weight_gear2, 0),
        new AlarmCell("weight3", R.id.et_weight_gear3, 0),

        // 幅度，高度
        new AlarmCell("armLengthMin", R.id.et_arm_min, 0),
        new AlarmCell("armLengthMax", R.id.et_arm_max, 0),
        new AlarmCell("hookHeightMin", R.id.et_height_min, 0),
        new AlarmCell("hookHeightMax", R.id.et_height_max, 0),
    };

    private Activity activity;
    private Context context;

    public void hideKeyboard() {
        View rootview = this.getWindow().getDecorView();
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
            .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(rootview.findFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_settting);
        activity = this;
        context = getApplicationContext();
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


    private void setOnClickListener(View view) {
        View.OnClickListener onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.save_logo) { // 保存数据
                    AlertView alertView = new AlertView("保存告警参数", "", null,
                        new String[]{"确定", "取消"}, null, activity,
                        AlertView.Style.Alert, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            System.out.println("## yes! I will store the data!");
                            AlarmSetDao dao = new AlarmSetDao(context);
                            List<AlarmSet> alarmSets = dao.selectAll();
                            AlarmSet alarmSet = alarmSets.get(0);
                            for (AlarmCell cell : alarmCells) {
                                try {
                                    Field field = AlarmSet.class.getDeclaredField(cell.propName);
                                    EditText etShow = (EditText) findViewById(cell.editTextId);
                                    float value = Float.parseFloat(etShow.getText().toString());
                                    field.setAccessible(true);
                                    field.set(alarmSet, value);
                                } catch (Exception e) {
                                    // TODO
                                }
                            }
                            dao.update(alarmSet);

                            EventBus.getDefault().post(new AlarmSetEvent(alarmSet));
                        }
                    });
                    alertView.show();
                    hideKeyboard();
                } else if (view.getId() == R.id.close_logo) {
                    finish();
                }
            }
        };
        view.setOnClickListener(onClickListener);

        findViewById(R.id.rvc_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysPara rvc = sysParaDao.queryParaByName("rvc");
                boolean isRvcMode = Boolean.parseBoolean(rvc.getParaValue());
                if (!isRvcMode) { // 反向操作
                    ((Button) findViewById(R.id.rvc_mode)).setText("RCV");
                    findViewById(R.id.et_gear4).setEnabled(false);
                    findViewById(R.id.et_gear5).setEnabled(false);
                    findViewById(R.id.et_ta_gear4).setEnabled(false);
                    findViewById(R.id.et_ta_gear5).setEnabled(false);

                } else {
                    ((Button) findViewById(R.id.rvc_mode)).setText("FREQ");
                    findViewById(R.id.et_gear4).setEnabled(true);
                    findViewById(R.id.et_gear5).setEnabled(true);
                    findViewById(R.id.et_ta_gear4).setEnabled(true);
                    findViewById(R.id.et_ta_gear5).setEnabled(true);

                }

                rvc.setParaValue(String.valueOf(!isRvcMode));
                sysParaDao.update(rvc);
                MainActivity.isRvcMode.set(!isRvcMode);
            }
        });
    }

    private void setOnTouchListener() {
        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.close_logo));
            add((ImageView) findViewById(R.id.save_logo));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        confLoad(getApplicationContext());
        setOnTouchListener();
    }

    private void confLoad(Context contex) {
        DatabaseHelper.getInstance(contex).createTable(AlarmSet.class);
        AlarmSetDao dao = new AlarmSetDao(contex);
        sysParaDao = new SysParaDao(contex);

        SysPara rvc = sysParaDao.queryParaByName("rvc");
        if (rvc == null) {
            rvc = new SysPara("rvc", "false");
            sysParaDao.insert(rvc);
        }

        boolean isRvcMode = Boolean.parseBoolean(rvc.getParaValue());
        if (isRvcMode) {
            ((Button) findViewById(R.id.rvc_mode)).setText("RCV");
            findViewById(R.id.et_gear4).setEnabled(false);
            findViewById(R.id.et_gear5).setEnabled(false);
            findViewById(R.id.et_ta_gear4).setEnabled(false);
            findViewById(R.id.et_ta_gear5).setEnabled(false);

        } else {
            ((Button) findViewById(R.id.rvc_mode)).setText("FREQ");
            findViewById(R.id.et_gear4).setEnabled(true);
            findViewById(R.id.et_gear5).setEnabled(true);
            findViewById(R.id.et_ta_gear4).setEnabled(true);
            findViewById(R.id.et_ta_gear5).setEnabled(true);

        }

        List<AlarmSet> paras = dao.selectAll();
        if (paras == null || paras.size() == 0) {
            dao.insert(new AlarmSet());
        }

        paras = dao.selectAll();
        if (paras.size() < 1) return;
        AlarmSet para = paras.get(0);

        for (AlarmCell cell : alarmCells) {
            try {
                Field field = AlarmSet.class.getDeclaredField(cell.propName);
                field.setAccessible(true);
                float value = (float) field.get(para);
                EditText etShow = (EditText) findViewById(cell.editTextId);
                etShow.setText(String.valueOf(value));
            } catch (Exception e) {
                // TODO
            }
        }
    }
}
