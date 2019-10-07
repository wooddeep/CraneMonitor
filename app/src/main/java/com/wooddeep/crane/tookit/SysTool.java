package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class SysTool {

    public static boolean hideNavigation(Activity activity) {
        boolean ishide;
        try {
            String command;
            command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib service call activity 42 s16 com.android.systemui";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            ishide = true;
        } catch (Exception ex) {
            Toast.makeText(activity.getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            ishide = false;
        }
        return ishide;
    }

    public static boolean showNavigation() {
        boolean isshow;
        try {
            String command;
            command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib am startservice -n com.android.systemui/.SystemUIService";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            isshow = true;
        } catch (Exception e) {
            isshow = false;
            e.printStackTrace();
        }
        return isshow;
    }

    public static boolean createTempFile(Context context) {
        boolean exist = false;
        try {
            File path = context.getCacheDir();
            System.out.println("### path = " + path.getPath() + "/tmp.txt");
            File file = new File(path.getPath() + "/tmp.txt");
            if (!file.exists()) {
                File tmpFile = File.createTempFile("tmp", "txt", path);
                tmpFile.deleteOnExit();
                exist = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exist;
    }
}
