package com.wooddeep.crane.persist.dao.log;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.LogDbHelper;
import com.wooddeep.crane.persist.entity.log.WorkRecrod;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 工作记录, 记录一次吊重工作的最大值

public class WorkRecDao extends LogDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<WorkRecrod, Integer> dao;

    public WorkRecDao(Context context) {
        this.context = context;
        try {
            this.dao = LogDbHelper.getInstance(context).getDaoX(WorkRecrod.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向user表中添加一条数据
    public void insert(WorkRecrod data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(WorkRecrod data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public void update(WorkRecrod data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询user表中的所有数据
    public List<WorkRecrod> selectAll() {
        List<WorkRecrod> cranes = null;
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

    public long queryCount() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 根据ID取出用户信息
    public WorkRecrod queryById(int id) {
        WorkRecrod crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public List<WorkRecrod> queryForMatching(WorkRecrod crane) {
        List<WorkRecrod> cranes = null;
        try {
            cranes = dao.queryForMatching(crane);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

    public void deleteAll() {
        try {
            dao.executeRaw("delete from workrec;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<WorkRecrod> queryPage(long offset, long limit) {
        List<WorkRecrod> cranes = null;
        try {
            cranes = dao.queryBuilder().offset(offset).limit(limit).orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

}

