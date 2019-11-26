package com.wooddeep.crane.tookit;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wooddeep.crane.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

    public static void sysSetShow(Activity activity) {
        try {
            String command;
            command = "am start com.android.settings/com.android.settings.Settings";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
        } catch (Exception ex) {
            Toast.makeText(activity.getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public static void sysScriptInit(Context context) {
        try {

            String command;
            command = "if [ ! -f /system/bin/bash ]; then cp /system/bin/sh /system/bin/bash; fi";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();

            File monitor = new File("/sdcard/monitor.sh"); // 首付进程
            if (!monitor.exists()) {
                SysTool.copyFilesFromRaw(context, R.raw.monitor, "monitor.sh", "/sdcard");
            }

            File fileop = new File("/sdcard/fileops.sh"); // 文件拷贝工具
            if (!fileop.exists()) {
                SysTool.copyFilesFromRaw(context, R.raw.fileops, "fileops.sh", "/sdcard");
            }

            command = "exist=`ps | grep 'bash'`; if [ -z \"$exist\" ]; then bash /sdcard/monitor.sh; fi";
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();

            InputStream fis = proc.getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr = new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            //直到读完为止
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static boolean restartApp() {
        boolean isshow;
        try {
            String command;
            command = "sleep 2; am start -n com.wooddeep.crane/com.wooddeep.crane.MainActivity";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            isshow = true;
        } catch (Exception e) {
            isshow = false;
            e.printStackTrace();
        }
        return isshow;
    }

    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {// 判断目录是否存在
            System.out.println("创建目录失败，目标目录已存在！");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {// 创建目标目录
            System.out.println("创建目录成功！" + destDirName);
            return true;
        } else {
            System.out.println("创建目录失败！");
            return false;
        }
    }


    /**
     *      * 复制单个文件
     *      * @param oldPath String 原文件路径 如：data/video/xxx.mp4
     *      * @param newPath String 复制后路径 如：data/oss/xxx.mp4
     *      * @return boolean
     *      
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    private static final String SEPARATOR = File.separator;//路径分隔符

    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context     上下文
     * @param id          资源ID
     * @param fileName    文件名
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromRaw(Context context, int id, String fileName, String storagePath) {
        InputStream inputStream = context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.mkdirs();
        }
        readInputStream(storagePath + SEPARATOR + fileName, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyToUsbDisk(String filepath) {
        try {
            List<String> usbDir = new ArrayList<>();
            String command;
            command = "ls /mnt/media_rw";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            InputStream fis = proc.getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr = new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            //直到读完为止
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                usbDir.add("/mnt/media_rw/" + line.replaceAll("\\s+", ""));
            }

            for (String dir : usbDir) {
                command = String.format("cp -rf %s %s", filepath, dir);
                proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void usbDiskDetect() {
        try {
            String command = "ls /mnt/media_rw";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            InputStream fis = proc.getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr = new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            //直到读完为止
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                //usbDir.add("/mnt/media_rw/" + line.replaceAll("\\s+", ""));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static JSONObject copySysCfgToUsbDisk(String dstPath, String srcName) {
        JSONObject resp = new JSONObject();
        try {

            String out = executeScript("/sdcard/fileops.sh", "tousb", "/data/data/com.wooddeep.crane/databases", "crane.db");
            if (out.contains("ok")) {
                resp.put("msg", "操作成功(success)");
            } else {
                resp.put("msg", "操作失败(failure)");
            }
            Log.v("DEBUG", out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resp;
    }

    public static JSONObject copyToUsbDisk(String filepath, String fileName) {
        JSONObject out = new JSONObject();

        try {
            out.put("code", 0);

            List<String> usbDir = new ArrayList<>();
            String command;
            command = "ls /mnt/media_rw";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            InputStream fis = proc.getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr = new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            //直到读完为止
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                usbDir.add("/mnt/media_rw/" + line.replaceAll("\\s+", ""));
            }

            if (usbDir.size() == 0) {
                out.put("code", 1).put("msg", "U盘位置未知, 请在目标U盘创建一个文件名为tmec的空文件\n" +
                    "(Usb disk not found, please create a empty file named tmec \nin the target usb disk!)");
                return out;
            }

            for (String dir : usbDir) {
                command = String.format("cp -rf %s %s", filepath, dir);
                proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
                //copyCheck(filepath, String.format("%s/%s", dir, fileName));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    public static int copyFromUsbDisk(String dstPath, String srcName) {
        int ret = 0;
        try {
            List<String> usbDir = new ArrayList<>();
            String command;
            command = "ls /mnt/media_rw";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            InputStream fis = proc.getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr = new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            //直到读完为止
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                usbDir.add("/mnt/media_rw/" + line.replaceAll("\\s+", "")); // 遇到一个U盘退出
                break;
            }

            if (usbDir.size() == 0) {
                ret = -1;
            }

            for (String udir : usbDir) {
                command = String.format("cp -rf %s/%s %s", udir, srcName, dstPath);
                System.out.println(command);
                proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    // SysTool.copySysCfgFromUsbDisk("/data/data/com.wooddeep.crane/databases", "crane.db");
    public static int copyLoadAttrFromUsbDisk(String dstPath, String srcName) {
        int ret = -1;
        try {
            String out = executeScript("/sdcard/fileops.sh", "fromusb", "/data/data/com.wooddeep.crane/databases", "tc.db");
            if (out.contains("ok")) {
                ret = 0;
            }

            Log.v("DEBUG", out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    // SysTool.copySysCfgFromUsbDisk("/data/data/com.wooddeep.crane/databases", "crane.db");
    public static int copySysCfgFromUsbDisk(String dstPath, String srcName) {
        int ret = -1;
        try {
            String out = executeScript("/sdcard/fileops.sh", "fromusb", "/data/data/com.wooddeep.crane/databases", "crane.db");
            if (out.contains("ok")) {
                ret = 0;
            }

            Log.v("DEBUG", out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static String usbDiskRoot() {
        String ret = "none";
        try {
            ret = executeScript("/sdcard/fileops.sh", "usbdetect").replaceAll("\r?\n", "");
            Log.v("DEBUG", ret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    //settings get system screen_brightness
    public static void adjustBackgroudLight(int delta) {
        try {
            String command;
            command = "settings get system screen_brightness";
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            proc.waitFor();
            InputStream fis = proc.getInputStream();
            //用一个读输出流类去读
            InputStreamReader isr = new InputStreamReader(fis);
            //用缓冲器读行
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            //直到读完为止
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                break;
            }

            if (line != null) {
                int currbright = Integer.parseInt(line);
                if (currbright < 50 && delta < 0) return;
                int brightness = Integer.parseInt(line) + delta;
                command = String.format("settings put system screen_brightness %d", brightness);
                proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String executeScript(String fullPath, String... args) {
        StringBuffer out = new StringBuffer("");
        try {
            StringBuffer cmd = new StringBuffer(fullPath);

            for (String arg : args) {
                cmd.append(" ");
                cmd.append(arg);
            }

            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", String.format("sh %s", cmd.toString())});
            proc.waitFor();
            InputStream fis = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line);
                out.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toString();
    }


}
