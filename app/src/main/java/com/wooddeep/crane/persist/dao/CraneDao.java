package com.wooddeep.crane.persist.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.entity.Crane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CraneDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<Crane, Integer> dao;

    public CraneDao(Context context) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDaoX(Crane.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向user表中添加一条数据
    public void insert(Crane data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(Crane data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            dao.executeRaw("delete from crane;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public void update(Crane data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询user表中的所有数据
    public List<Crane> selectAll() {
        List<Crane> cranes = null;
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
    public Crane queryById(int id) {
        Crane crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    // 根据dn取出用户信息
    public Crane queryByDn(String dn) {
        List<Crane> cranes = null;
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("devid", dn);
            cranes = dao.queryForFieldValues(map);
            if (cranes == null || cranes.size() == 0) return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes.get(0);
    }

    public List<Crane> queryForMatching(Crane crane) {
        List<Crane> cranes = null;
        try {
            cranes = dao.queryForMatching(crane);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

}
