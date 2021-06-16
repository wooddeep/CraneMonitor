package com.example.yangjianlin.hkmonitor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//import com.ghyf.mplay.value.ConstantValue;



import android.app.Activity;
import android.graphics.Bitmap;

import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * FrameBuffer中获取Android屏幕截图
 * @author cuiran
 * @version 1.0.0
 */
public class ScreenShotFb {

    private static final String TAG = "ScreenShotFb";

    final static String FB0FILE1 = "/dev/graphics/fb0";

    static File fbFile;

    //程序入口
    public static void shoot() {
        try {
            /************ 创建锁对象 ************/
            final Object lock = new Object();

            synchronized (lock) {
                long start = System.currentTimeMillis();
                Bitmap bitmap = getScreenShotBitmap();
                long end = System.currentTimeMillis();
                //LogUtil.i(TAG, "getScreenShotBitmap time is :" + (end - start) + " ms");
                String filePath = "/sdcard/lihan.png" ;//ConstantValue.ROOT_SDCARD_DIR + "/s.png";
//    	        	String filePath= ConstantValue.ROOT_SDCARD_DIR+"/screens/"+System.currentTimeMillis()+".png";
                ScreenShotFb.savePic(bitmap, filePath);
            }
        } catch (Exception e) {
            //LogUtil.e(TAG, "Exception error", e);
            e.printStackTrace();
        }


    }

    //保存到sdcard
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 50, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            //LogUtil.e(TAG, "FileNotFoundException error", e);
        } catch (IOException e) {
            //LogUtil.e(TAG, "IOException error", e);
        }

        //LogUtil.i(TAG, "savePic success");
    }

    public static void init(Activity activity) {

        try {

            DisplayMetrics dm = new DisplayMetrics();
            Display display = activity.getWindowManager().getDefaultDisplay();
            display.getMetrics(dm);
            screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
            screenHeight = dm.heightPixels; // 屏幕高（像素，如：800p）
            int pixelformat = display.getPixelFormat();
            PixelFormat localPixelFormat1 = new PixelFormat();
            PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
            int deepth = localPixelFormat1.bytesPerPixel;// 位深
            //LogUtil.i(TAG, "deepth=" + deepth);
            piex = new byte[screenHeight * screenWidth * deepth];// 像素
            colors = new int[screenHeight * screenWidth];
        } catch (Exception e) {
            //LogUtil.e(TAG, "Exception error", e);
        }
    }

    static DataInputStream dStream = null;
    static byte[] piex = null;
    static int[] colors = null;
    static int screenWidth;
    static int screenHeight;

    public static synchronized Bitmap getScreenShotBitmap() {
        FileInputStream buf = null;
        try {
            fbFile = new File(FB0FILE1);
            buf = new FileInputStream(fbFile);// 读取文件内容
            dStream = new DataInputStream(buf);
            dStream.readFully(piex);
            dStream.close();
            // 将rgb转为色值
            for (int i = 0; i < piex.length; i += 2) {
                colors[i / 2] = (int) 0xff000000 +
                    (int) (((piex[i + 1]) << (16)) & 0x00f80000) +
                    (int) (((piex[i + 1]) << 13) & 0x0000e000) +
                    (int) (((piex[i]) << 5) & 0x00001A00) +
                    (int) (((piex[i]) << 3) & 0x000000f8);
            }

            // 得到屏幕bitmap
            return Bitmap.createBitmap(colors, screenWidth, screenHeight,
                Bitmap.Config.RGB_565);

        } catch (FileNotFoundException e) {
            //LogUtil.e(TAG, "FileNotFoundException error", e);
        } catch (IOException e) {
            //LogUtil.e(TAG, "IOException error", e);
        } catch (Exception e) {
            //LogUtil.e(TAG, "Exception error", e);
        } finally {
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
