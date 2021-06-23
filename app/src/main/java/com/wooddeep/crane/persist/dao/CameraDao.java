package com.wooddeep.crane.persist.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.entity.Camera;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CameraDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<Camera, Integer> dao;

    public CameraDao(Context context) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDaoX(Camera.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向user表中添加一条数据
    public void insert(Camera data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(Camera data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            dao.executeRaw("delete from camera;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public void update(Camera data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询user表中的所有数据
    public List<Camera> selectAll() {
        List<Camera> cranes = null;
        try {
            cranes = dao.queryForAll();
            if (cranes == null) {
                cranes = new ArrayList<>();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cranes;
    }

    // 根据ID取出用户信息
    public Camera queryById(int id) {
        Camera crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public List<Camera> queryForMatching(Camera camera) {
        List<Camera> cameras = null;
        try {
            cameras = dao.queryForMatching(camera);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cameras;
    }

}

