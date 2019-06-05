package com.wooddeep.crane.persist.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wooddeep.crane.persist.MySqliteHelper;
import com.wooddeep.crane.persist.entity.Student;

public class StudentDao {

    private SQLiteOpenHelper helper;

    private Context context;

    public StudentDao(Context context){

        this.context = context;

    }

    public void insert(Student stu){

        helper = new MySqliteHelper(context,"students.db", null, 1);

        Log.i("MYSQLITEHELPER","before get db");

        SQLiteDatabase db = helper.getWritableDatabase();

        Log.i("MYSQLITEHELPER","after get db");

        db.execSQL("insert into t_student(name, gender, age) values(?,?,?)" , new Object[]{stu.getName(),stu.getGender(),stu.getAge()});

        db.close();

    }

}
