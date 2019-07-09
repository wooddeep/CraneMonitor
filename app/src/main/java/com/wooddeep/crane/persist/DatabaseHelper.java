package com.wooddeep.crane.persist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wooddeep.crane.ProtectArea;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Area;
import com.wooddeep.crane.persist.entity.Calibration;
import com.wooddeep.crane.persist.entity.Crane;
import com.wooddeep.crane.persist.entity.Load;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库操作管理工具类
 * <p>
 * 我们需要自定义一个类继承自ORMlite给我们提供的OrmLiteSqliteOpenHelper，创建一个构造方法，重写两个方法onCreate()和onUpgrade()
 * 在onCreate()方法中使用TableUtils类中的createTable()方法初始化数据表
 * 在onUpgrade()方法中我们可以先删除所有表，然后调用onCreate()方法中的代码重新创建表
 * <p>
 * 我们需要对这个类进行单例，保证整个APP中只有一个SQLite Connection对象
 * <p>
 * 这个类通过一个Map集合来管理APP中所有的DAO，只有当第一次调用这个DAO类时才会创建这个对象（并存入Map集合中）
 * 其他时候都是直接根据实体类的路径从Map集合中取出DAO对象直接调用
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    // 数据库名称
    public static final String DATABASE_NAME = "crane.db";

    // 本类的单例实例
    private static DatabaseHelper instance;

    // DatabaseHelper 对象的 connSource
    private ConnectionSource connSource = null;

    private SQLiteDatabase db = null;

    // 存储APP中所有的DAO对象的Map集合
    private Map<String, Dao> daos = new HashMap<>();

    // 获取本类单例对象的方法
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    // 私有的构造方法
    private DatabaseHelper(Context context) {
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
            TableUtils.createTableIfNotExists(this.connSource, cls);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override // 创建数据库时调用的方法
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            this.connSource = connectionSource;
            this.db = database;
            TableUtils.createTableIfNotExists(connectionSource, Crane.class);  // 塔基
            TableUtils.createTableIfNotExists(connectionSource, Area.class);   // 区域
            //TableUtils.createTableIfNotExists(connectionSource, ProtectArea.class);   // 保护区域
            //TableUtils.createTableIfNotExists(connectionSource, Calibration.class); // 标定
            //TableUtils.createTableIfNotExists(connectionSource, Load.class);  // 负荷特性
            //TableUtils.createTableIfNotExists(connectionSource, AlarmSet.class); // 告警

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override // 数据库版本更新时调用的方法
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Crane.class, true);
            TableUtils.dropTable(connectionSource, Area.class, true);
            //TableUtils.dropTable(connectionSource, ProtectArea.class, true);
            //TableUtils.dropTable(connectionSource, Calibration.class, true);
            //TableUtils.dropTable(connectionSource, Load.class, true);
            //TableUtils.dropTable(connectionSource, AlarmSet.class, true);
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
    }
}