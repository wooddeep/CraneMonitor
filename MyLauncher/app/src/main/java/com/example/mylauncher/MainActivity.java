package com.example.mylauncher;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

// adb shell am start com.android.settings/com.android.settings.Settings

public class MainActivity extends AppCompatActivity {

    private PackageManager mPackageManager;
    private SharedPreferences sharedPreferences;
    private List<PackageInfo> packages;
    private List<String> nameList;
    private List<String> packageList;
    private boolean exit = false;

    public static AtomicBoolean restart = new AtomicBoolean(false);
    public static AtomicLong prevMsec = new AtomicLong(System.currentTimeMillis());

    private MyReceiver recevier;
    private IntentFilter intentFilter;

    public static final String ACTION_WATCHDOG_KICK = "android.intent.action.WATCHDOG_KICK";
    public static final String ACTION_WATCHDOG_INIT = "android.intent.action.WATCHDOG_INIT";
    public static final String ACTION_WATCHDOG_STOP = "android.intent.action.WATCHDOG_STOP";
    public static final String ACTION_MOBILE_RESET = "android.intent.action.RESET_MOBILE";
    public final static String ACTION_MOBILE_SWITCH = "android.intent.action.ACTION_MOBILE_SWITCH_CHANGED";

    public final static String ACTION_WATCHDOG_SETTIMEOUT = "android.intent.action.WATCHDOG_SET_TIMEOUT";
    private Intent feedIntent = new Intent(ACTION_WATCHDOG_KICK);
    private Intent intent = null;

    public static void changePermission() {
        execRootCmdSilent("chmod 777 /dev/watchdog");
        execRootCmdSilent("kill `(ps |grep rk_wtd_test | busybox awk '{print $2}')`");
        File file = new File("/dev/watchdog");
        if (!file.canRead() || !file.canWrite()) {

            try {
                execRootCmdSilent("chmod 666 /dev/watchdog");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void initWatchDog() {
        changePermission();
        intent = new Intent(ACTION_WATCHDOG_INIT);
        sendBroadcast(intent);
    }

    private void feedWatchDog() {
        runOnUiThread(
            new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(feedIntent);
                }
            });
    }

    private void setWatchDogTimeOut() {
        intent = new Intent(ACTION_WATCHDOG_SETTIMEOUT);
        intent.putExtra("timeout", 10);
        sendBroadcast(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        System.out.println("## onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(recevier);
        exit = true;

    }

    public void startTimerThread(MainActivity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true && !exit) {
                    try {

                        if (count % 10 == 0) { // 5秒钟喂狗1次
                            runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        feedWatchDog();
                                    }
                                }); // 喂狗

                        }

                        long currMsec = System.currentTimeMillis();
                        System.out.println(currMsec - prevMsec.longValue());
                        if (currMsec - prevMsec.longValue() > 1000) {
                            runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        launchPackage("com.wooddeep.crane", 1);
                                        //System.out.println("## no message from crane!");
                                        Log.e("LUNCHER", "## no message from crane!");
                                    }
                                }); // 启动
                        } else {
                            //System.out.println("## crane is alive");
                            Log.e("LUNCHER", "## crane is alive");
                        }


                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recevier = new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.wooddeep.crane.HEARTBEAT");
        moveTaskToBack(true);

        //registerReceiver(recevier, intentFilter);


        mPackageManager = getPackageManager();
        sharedPreferences = getSharedPreferences("sharedPre", MODE_PRIVATE);
        nameList = new ArrayList<>();
        nameList.add("不使用");
        packageList = new ArrayList<>();
        packageList.add("nonon");
        getAppList();

        launchPackage("com.wooddeep.crane", 1);

        // 触发判断本机是否为主机
        startTimerThread(this);

        initWatchDog();
        setWatchDogTimeOut();
    }

    private void getAppList() {
        packages = mPackageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                nameList.add(packageInfo.applicationInfo.loadLabel(mPackageManager).toString());
                packageList.add(packageInfo.packageName);
            }
        }
    }

    private void launchPackage(String packageName, int id) {
        if (packageName != null && !packageName.equals("nonon")) {
            //Log.d("Main", "packageName0" + id + " = " + packageName);

            try {
                mPackageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                Intent intent = mPackageManager.getLaunchIntentForPackage(packageName);
                startActivity(intent);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(MainActivity.this, "应用" + id + "不存在", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(MainActivity.this, "应用" + id + "无法启动", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
