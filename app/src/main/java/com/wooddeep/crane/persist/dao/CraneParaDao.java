package com.wooddeep.crane.persist.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wooddeep.crane.persist.MySqliteHelper;
import com.wooddeep.crane.persist.entity.CranePara;
import com.wooddeep.crane.persist.entity.Student;

import java.util.ArrayList;
import java.util.List;


public class CraneParaDao {
    private SQLiteOpenHelper helper;

    private Context context;

    public CraneParaDao(Context context) {
        this.context = context;
    }

    public void insert(CranePara cp) {
        String sql =
        "create table IF NOT EXISTS t_crane_para (" +
        "id integer primary key, " +
        "name varchar(20), " +
        "type int, " +
        "coordX1 float, " +
        "coordY1 float, " +
        "coordX2 float, " +
        "coordY2 float, " +
        "craneHeight float, " +
        "bigArmLength float, " +
        "balancArmLength float, " +
        "craneBodyRadius float, " +
        "bigArmWidth float, " +
        "balancArmWidth float" +
        ");";

        helper = new MySqliteHelper(context, "crane.db", null, 1, sql);
        Log.i("MYSQLITEHELPER", "before get db");
        SQLiteDatabase db = helper.getWritableDatabase();
        Log.i("MYSQLITEHELPER", "after get db");
        db.execSQL("insert into t_crane_para(name, type, coordX1, coordY1, coordX2, coordY2, " +
        "craneHeight, bigArmLength, balancArmLength, craneBodyRadius, bigArmWidth, balancArmWidth) " +
        "values(?,?,?,?,?,?,?,?,?,?,?,?)",
        new Object[]{cp.getName(), cp.getType(), cp.getCoordX1(), cp.getCoordY1(), cp.getCoordX2(), cp.getCoordY2(),
        cp.getCraneHeight(), cp.getBigArmLength(), cp.getBalancArmLength(), cp.getCraneBodyRadius(),
        cp.getBigArmWidth(), cp.getBalancArmWidth()});

        db.close();
    }

    public void updateById(int id, CranePara cp) {
        helper = new MySqliteHelper(context, "crane.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", cp.getType());
        values.put("coordX1", cp.getCoordX1());
        values.put("coordY1", cp.getCoordY1());
        values.put("coordX2", cp.getCoordX2());
        values.put("coordY2", cp.getCoordY2());
        values.put("craneHeight", cp.getCraneHeight());
        values.put("bigArmLength", cp.getBigArmLength());
        values.put("balancArmLength", cp.getBalancArmLength());
        values.put("craneBodyRadius", cp.getCraneBodyRadius());
        values.put("bigArmWidth", cp.getBigArmWidth());
        values.put("balancArmWidth", cp.getBalancArmWidth());
        db.update("t_crane_para", values, "id=?", new String[]{id+""});
    }

    public int getRows() {
        helper = new MySqliteHelper(context, "crane.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(1) from t_crane_para", null);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    public void deleteById(int id) {
        helper = new MySqliteHelper(context, "crane.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("t_crane_para", "id=?", new String[]{id + ""});
    }

    public void deleteLatest() {
        helper = new MySqliteHelper(context, "crane.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from t_crane_para where id = (SELECT max(id) FROM t_crane_para);");
    }

    public List<CranePara> getAllCranePara() {
        List<CranePara> list = new ArrayList<CranePara>();
        helper = new MySqliteHelper(context, "crane.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select id, name, type, coordX1, coordY1, coordX2, coordY2, " +
        "craneHeight, bigArmLength, balancArmLength, craneBodyRadius, bigArmWidth, balancArmWidth " +
        "from t_crane_para", null);

        if (cursor == null) {
            return null;
        }

        while (cursor.moveToNext()) {
            CranePara cp = new CranePara(
            cursor.getInt(0),
            cursor.getString(1),
            cursor.getInt(2),
            cursor.getFloat(3),
            cursor.getFloat(4),
            cursor.getFloat(5),
            cursor.getFloat(6),
            cursor.getFloat(7),
            cursor.getFloat(8),
            cursor.getFloat(9),
            cursor.getFloat(10),
            cursor.getFloat(11),
            cursor.getFloat(12)
            );

            Log.i("MYSQLITEHELPER", cp.toString());
            list.add(cp);
        }
        return list;
    }

    public Student getStudentById(int id) {
        Student stu = null;
        helper = new MySqliteHelper(context, "crane.db", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("t_student", new String[]{"id", "name", "gender", "age"},
        "id=?", new String[]{id + ""}, null, null, null);
        if (cursor == null) {
            return null;
        }

        if (cursor.moveToFirst()) {
            //stu = new Student(cursor.getInt(0), cursor.getString(1),
            //cursor.getString(2), cursor.getInt(3));
        }
        return stu;

    }
}
