package com.wooddeep.crane.persist.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.entity.SysPara;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SysParaDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<SysPara, Integer> dao;

    public SysParaDao(Context context) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDaoX(SysPara.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向user表中添加一条数据
    public void insert(SysPara data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(SysPara data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public boolean update(SysPara data) {
        boolean ret = false;
        try {
            dao.update(data);
            ret = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    // 查询user表中的所有数据
    public List<SysPara> selectAll() {
        List<SysPara> cranes = null;
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
    public SysPara queryById(int id) {
        SysPara crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    // 根据ID取出用户信息
    public String queryValueByName(String name) {
        String crane = null;
        try {
            List<SysPara> paras = dao.queryBuilder().where().eq("paraName", name).query();
            if (paras != null && paras.size() > 0) {
                crane = paras.get(0).getParaValue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public SysPara queryParaByName(String name) {
        SysPara crane = null;
        try {
            List<SysPara> paras = dao.queryBuilder().where().eq("paraName", name).query();
            if (paras != null && paras.size() > 0) {
                crane = paras.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public List<SysPara> queryForMatching(SysPara crane) {
        List<SysPara> cranes = null;
        try {
            cranes = dao.queryForMatching(crane);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

}

