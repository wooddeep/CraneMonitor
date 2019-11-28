package com.wooddeep.crane;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.wooddeep.crane.R;
import com.wooddeep.crane.ebus.AlarmSetEvent;
import com.wooddeep.crane.ebus.SysParaEvent;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.EdbHelper;
import com.wooddeep.crane.persist.LoadDbHelper;
import com.wooddeep.crane.persist.dao.AlarmSetDao;
import com.wooddeep.crane.persist.dao.AreaDao;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.dao.CraneDao;
import com.wooddeep.crane.persist.dao.ProtectAreaDao;
import com.wooddeep.crane.persist.dao.ProtectDao;
import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.dao.TcParamDao;
import com.wooddeep.crane.persist.edao.EAlarmSetDao;
import com.wooddeep.crane.persist.edao.EAreaDao;
import com.wooddeep.crane.persist.edao.ECalibrationDao;
import com.wooddeep.crane.persist.edao.ECraneDao;
import com.wooddeep.crane.persist.edao.EProtectAreaDao;
import com.wooddeep.crane.persist.edao.EProtectDao;
import com.wooddeep.crane.persist.edao.ESysParaDao;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.Protect;
import com.wooddeep.crane.persist.entity.SysPara;
import com.wooddeep.crane.tookit.DrawTool;
import com.wooddeep.crane.tookit.SysTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static android.media.AudioManager.ADJUST_LOWER;
import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.view.View.GONE;

// 音量调节
// https://www.jianshu.com/p/a5f013e0dc3e

@SuppressWarnings("unused")
public class SuperAdmin extends AppCompatActivity {

    private Activity activity;
    private Context context;
    private SysParaDao paraDao;

    public static LinkedBlockingQueue mq = new LinkedBlockingQueue();

    private Activity[] activities = new Activity[]{

    };

    public void changeAppBrightness(Activity[] activities, float delta) {
        for (Activity activity : activities) {

            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            float brightness = lp.screenBrightness;
            //lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;

            brightness = brightness + delta;

            if (brightness > 1) brightness = 1f;
            if (brightness < 0.1) brightness = 0.1f;

            lp.screenBrightness = brightness;

            window.setAttributes(lp);
        }
    }

    private void reqireAuth() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.super_admin);
        Intent intent = getIntent();
        boolean superSuper = intent.getBooleanExtra("superSuper", false);
        if (!superSuper) {
            TableRow row = (TableRow) findViewById(R.id.row_pass_set);
            row.setVisibility(GONE);
            TableRow rowSysSet = (TableRow) findViewById(R.id.row_sys_set);
            rowSysSet.setVisibility(GONE);
        }
        activity = this;
        reqireAuth();
        context = getApplicationContext();
        paraDao = new SysParaDao(context);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setOnTouchListener();

        findViewById(R.id.btn_sysset_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysTool.sysSetShow(activity);
            }
        });

        findViewById(R.id.btn_pass_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passOne = ((EditText) findViewById(R.id.et_pass_set)).getText().toString();
                String passTwo = ((EditText) findViewById(R.id.et_pass_cfm)).getText().toString();
                if (!passOne.equals(passOne) || passOne.length() == 0) {
                    Toast toast = Toast.makeText(SuperAdmin.this, "密码数据错误(password data error!)", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    SysPara superpwd = paraDao.queryParaByName("superpwd");
                    superpwd.setParaValue(passOne);
                    boolean ret = paraDao.update(superpwd);
                    if (ret) {
                        Toast toast = Toast.makeText(SuperAdmin.this, "密码修改成功(password update ok!)", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(SuperAdmin.this, "密码修改失败(password update fail!)", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        });

        findViewById(R.id.btn_light_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SysTool.adjustBackgroudLight(50);
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_light_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SysTool.adjustBackgroudLight(-50);
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_sound_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.adjustVolume(ADJUST_RAISE, FLAG_PLAY_SOUND);

            }
        });

        findViewById(R.id.btn_sound_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mAudioManager.adjustVolume(ADJUST_LOWER, FLAG_PLAY_SOUND);
            }
        });

        findViewById(R.id.btn_export_sys_cfg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject out = SysTool.copySysCfgToUsbDisk("/data/data/com.wooddeep.crane/databases/crane.db", "crane.db");
                DrawTool.showExportSysCfgDialog(activity, out);
            }
        });

        findViewById(R.id.btn_import_sys_cfg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String extenalRoot = SysTool.usbDiskRoot();
                if (extenalRoot.equals("none")) {
                    DrawTool.showImportSysCfgDialog(activity, false, 1);
                }

                final String usbRoot = "/sdcard/crane";
                SysTool.executeScript("/sdcard/fileops.sh", "fromusb", "/sdcard/crane", "crane.db");

                EAlarmSetDao eAlarmSetDao = new EAlarmSetDao(context, usbRoot);
                EAreaDao eAreaDao = new EAreaDao(context, usbRoot);
                ECalibrationDao eCalibrationDao = new ECalibrationDao(context, usbRoot);
                ECraneDao eCraneDao = new ECraneDao(context, usbRoot);
                EProtectDao eProtectDao = new EProtectDao(context, usbRoot);
                ESysParaDao eSysParaDao = new ESysParaDao(context, usbRoot);

                AlarmSetDao alarmSetDao = new AlarmSetDao(context);
                AreaDao areaDao = new AreaDao(context);
                CalibrationDao calibrationDao = new CalibrationDao(context);
                CraneDao craneDao = new CraneDao(context);
                ProtectDao protectDao = new ProtectDao(context);
                SysParaDao sysParaDao = new SysParaDao(context);

                List<Crane> cranes = eCraneDao.selectAll();
                if (cranes == null || cranes.size() == 0) {
                    DrawTool.showImportSysCfgDialog(activity, false, 2);
                    return;
                }
                craneDao.deleteAll();
                for (Crane crane: cranes) {
                    craneDao.insert(crane);
                }

                List<AlarmSet> alarmSets = eAlarmSetDao.selectAll();
                if (alarmSets == null) return;
                alarmSetDao.deleteAll();
                for (AlarmSet alarmSet: alarmSets) {
                    alarmSetDao.insert(alarmSet);
                }

                areaDao.deleteAll();
                List<Area> areas = eAreaDao.selectAll();
                for (Area area: areas) {
                    eAreaDao.insert(area);
                }

                calibrationDao.deleteAll();
                List<Calibration> calibrations = eCalibrationDao.selectAll();
                for (Calibration calibration: calibrations) {
                    calibrationDao.insert(calibration);
                }

                protectDao.deleteAll();
                List<Protect> protects = eProtectDao.selectAll();
                for (Protect protect: protects) {
                    protectDao.insert(protect);
                }

                sysParaDao.deleteAll();
                List<SysPara> sysParas = eSysParaDao.selectAll();
                for (SysPara sysPara: sysParas) {
                    sysParaDao.insert(sysPara);
                }

                EdbHelper.getInstance().close();

                DrawTool.showImportSysCfgDialog(activity, true, 0);
            }
        });


        String remoteAddr = "192.168.140.94";
        SysPara ra = paraDao.queryParaByName("remoteAddr");
        if (ra == null) {
            ra = new SysPara("remoteAddr", remoteAddr);
            paraDao.insert(ra);
        } else {
            remoteAddr = ra.getParaValue();
        }

        int remotePort = 1733;
        SysPara rp = paraDao.queryParaByName("remotePort");
        if (rp == null) {
            rp = new SysPara("remotePort", String.valueOf(remotePort));
            paraDao.insert(rp);
        } else {
            remotePort = Integer.parseInt(rp.getParaValue());
        }


        ((EditText) findViewById(R.id.et_remote_addr_set)).setText(remoteAddr);
        ((EditText) findViewById(R.id.et_remote_port_set)).setText(String.valueOf(remotePort));


        ((Button) findViewById(R.id.btn_remote_set)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addr = ((EditText) findViewById(R.id.et_remote_addr_set)).getText().toString();
                String port = ((EditText) findViewById(R.id.et_remote_port_set)).getText().toString();

                if (!addr.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")) {
                    DrawTool.showDialog(activity, "地址格式错误!(address format error!)");
                } else if (!port.matches("[0-9]+")) {
                    DrawTool.showDialog(activity, "端口格式错误!(port format error!)");
                } else {
                    SysPara ra = paraDao.queryParaByName("remoteAddr");
                    ra.setParaValue(addr);
                    boolean ret = paraDao.update(ra);
                    if (!ret) {
                        DrawTool.showDialog(activity, "存地址失败!(save address fail!)");
                        return;
                    }

                    SysPara rp = paraDao.queryParaByName("remotePort");
                    rp.setParaValue(port);
                    ret = paraDao.update(rp);
                    if (!ret) {
                        DrawTool.showDialog(activity, "存端口失败!(save port fail!)");
                        return;
                    }

                    mq.offer(addr + ":" + port);
                    DrawTool.showDialog(activity, "成功!(success!)");
                }
            }
        });


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
            add((ImageView) findViewById(R.id.save_logo));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }

}
