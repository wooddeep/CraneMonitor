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

        SysTool.executeScript("/sdcard/fileops.sh", "fromusb", "/sdcard/crane", "tc.db");

        DB_PATH = "/sdcard/crane";

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

    public static JSONArray extTableQuery(String dbname, String tbname, String sql) {
        JSONArray out = new JSONArray();

        DB_PATH = "/sdcard/crane";

        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + dbname, null);
            Cursor cursor = db.rawQuery(sql, new String[]{});
            if (cursor.moveToFirst()) {
                do {
                    JSONObject line = new JSONObject();

                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String colName = cursor.getColumnName(i);
                        line.put(colName,  cursor.getString(i));
                    }
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


    public static JSONArray extTableExec(String dbname, String tbname, String sql) {
        JSONArray out = new JSONArray();

        DB_PATH = "/sdcard/crane";

        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + dbname, null);
            db.execSQL(sql);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static String extKvGet(String dbname, String key) {
        String out = null;
        DB_PATH = "/sdcard/crane";
        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + dbname, null);
            Cursor cursor = db.rawQuery(String.format("select paraValue from syspara where paraName='%s'", key), new String[]{});
            if (cursor.moveToFirst()) {
                do {
                    out = cursor.getString(0);
                    break;
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static JSONArray extKvSet(String dbname, String key, String value) {
        JSONArray out = new JSONArray();
        DB_PATH = "/sdcard/crane";
        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + "/" + dbname, null);
            String saved = extKvGet(dbname, key);
            if (saved == null) {
                db.execSQL(String.format("insert into syspara (paraName, paraValue) values ('%s', '%s')", key, value));
            } else {
                db.execSQL(String.format("update syspara set paraValue='%s' where paraName='%s'", value, key));
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

}
