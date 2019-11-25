package com.wooddeep.crane.tookit;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class EdbTool {
    private static Context mContext;

    public static String DB_PATH = "";
    private static int BUFFER_SIZE = 40000;


    public static JSONArray getExtTcParam(Context context, String dbname, String tbname) {
        JSONArray out = new JSONArray();

        mContext = context;
        String usbRoot = SysTool.usbDiskRoot();
        if (usbRoot.equals("none")) return out;
        DB_PATH = usbRoot;

        try {
            File file = new File(DB_PATH);
            // 如果内存中不存在，则开始导入
            if (!file.exists()) {
                file.mkdirs();
                AssetManager manager = mContext.getAssets();
                InputStream is = manager.open(dbname);
                FileOutputStream fos = new FileOutputStream(DB_PATH + "/" + dbname);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + dbname, null);
            Cursor cursor = db.query(tbname, new String[]{"Rate", "Length", "Distance", "Type", "Weight"}, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    JSONObject line = new JSONObject();
                    line.put("Rate", cursor.getString(cursor.getColumnIndex("Rate")));
                    line.put("Length", cursor.getString(cursor.getColumnIndex("Length")));
                    line.put("Distance", cursor.getString(cursor.getColumnIndex("Distance")));
                    line.put("Type", cursor.getString(cursor.getColumnIndex("Type")));
                    line.put("Weight", cursor.getString(cursor.getColumnIndex("Weight")));
                    out.put(line);
                    //Log.d("Type : ", cursor.getString(cursor.getColumnIndex("Type")));
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static JSONArray syncExtCranedb(Context context, String dbname, String tbname) {
        JSONArray out = new JSONArray();

        mContext = context;
        String usbRoot = SysTool.usbDiskRoot();
        if (usbRoot.equals("none")) return out;
        DB_PATH = usbRoot;

        try {
            File file = new File(DB_PATH);
            // 如果内存中不存在，则开始导入
            if (!file.exists()) {
                file.mkdirs();
                AssetManager manager = mContext.getAssets();
                InputStream is = manager.open(dbname);
                FileOutputStream fos = new FileOutputStream(DB_PATH + "/" + dbname);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + dbname, null);
            Cursor cursor = db.query(tbname, new String[]{"Rate", "Length", "Distance", "Type", "Weight"}, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    JSONObject line = new JSONObject();
                    line.put("Rate", cursor.getString(cursor.getColumnIndex("Rate")));
                    line.put("Length", cursor.getString(cursor.getColumnIndex("Length")));
                    line.put("Distance", cursor.getString(cursor.getColumnIndex("Distance")));
                    line.put("Type", cursor.getString(cursor.getColumnIndex("Type")));
                    line.put("Weight", cursor.getString(cursor.getColumnIndex("Weight")));
                    out.put(line);
                    //Log.d("Type : ", cursor.getString(cursor.getColumnIndex("Type")));
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }
}
