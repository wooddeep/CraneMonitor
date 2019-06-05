package com.wooddeep.crane.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// 数据库操作
// https://www.cnblogs.com/dqrcsc/p/4636162.html

public class MySqliteHelper extends SQLiteOpenHelper {

    public static final String TAG = "MYSQLITEHELPER";

    public static final String CREATE_STUDENT = "" +
    "create table t_student (" +
    "id integer primary key, " +
    "name varchar(20), " +
    "gender varchar(10), " +
    "age integer" +
    ")";

    private String createSql = "";

    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.createSql = createSql;
    }

    public MySqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, String createSql) {
        super(context, name, factory, version);
        this.createSql = createSql;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG,"open db");
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"create db");
        Log.i(TAG,"before excSql");
        db.execSQL(this.createSql);
        Log.i(TAG,"after excSql");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
