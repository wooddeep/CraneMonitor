package com.wooddeep.crane.tookit;

import android.content.Intent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class DogTool {

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


}
