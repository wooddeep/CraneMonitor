package com.wooddeep.crane.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.Protect;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by niuto on 2019/11/25.
 */

public class EdbHelper extends OrmLiteSqliteOpenHelper {

    public static String DATABASE_PATH = ""; //"/sdcard/test/tc/crane.db";

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null,
            SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return SQLiteDatabase.openDatabase(DATABASE_PATH, null,
            SQLiteDatabase.OPEN_READONLY);
    }

    // 数据库名称
    public static final String DATABASE_NAME = "crane.db";

    // 本类的单例实例
    private static EdbHelper instance;


    // 存储APP中所有的DAO对象的Map集合
    private Map<String, Dao> daos = new HashMap<>();

    // 获取本类单例对象的方法
    public static synchronized EdbHelper getInstance(Context context, String fullpath) {
        DATABASE_PATH = fullpath + "/crane.db";
        if (instance == null) {
            synchronized (EdbHelper.class) {
                if (instance == null) {
                    instance = new EdbHelper(context);
                }
            }
        }
        return instance;
    }

    public static synchronized EdbHelper getInstance() {
        return instance;
    }

    // 私有的构造方法

    private EdbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    // 根据传入的DAO的路径获取到这个DAO的单例对象（要么从daos这个Map中获取，要么新创建一个并存入daos）
    public synchronized Dao getDaoX(Class<?> clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    @SuppressWarnings("unused")
    public void createTable(Class cls) {
        try {
            Iterator<String> it = daos.keySet().iterator();
            while (it.hasNext()) {
                String name = it.next();
                TableUtils.createTableIfNotExists(daos.get(name).getConnectionSource(), cls);
                break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override // 创建数据库时调用的方法
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTableIfNotExists(connectionSource, Crane.class);  // 塔基
            TableUtils.createTableIfNotExists(connectionSource, Area.class);   // 区域
            TableUtils.createTableIfNotExists(connectionSource, Protect.class);   // 区域
            TableUtils.createTableIfNotExists(connectionSource, Crane.class);   // 区域
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override // 数据库版本更新时调用的方法
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Crane.class, true);
            TableUtils.dropTable(connectionSource, Area.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 释放资源
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
        instance = null;
    }
}
