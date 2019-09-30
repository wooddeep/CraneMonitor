package com.wooddeep.crane.persist.dao.log;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.LogDbHelper;
import com.wooddeep.crane.persist.entity.log.LogEntity;
import com.wooddeep.crane.persist.entity.log.SwitchRec;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 工作记录, 记录一次吊重工作的最大值

public class SwitchRecDao  extends LogDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<SwitchRec, Integer> dao;

    public SwitchRecDao(Context context) {
        this.context = context;
        try {
            this.dao = LogDbHelper.getInstance(context).getDaoX(SwitchRec.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向user表中添加一条数据
    public void insert(SwitchRec data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(SwitchRec data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public void update(SwitchRec data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询user表中的所有数据
    public List<SwitchRec> selectAll() {
        List<SwitchRec> cranes = null;
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

    @Override
    public long queryCount() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 根据ID取出用户信息
    public SwitchRec queryById(int id) {
        SwitchRec crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public List<SwitchRec> queryForMatching(SwitchRec crane) {
        List<SwitchRec> cranes = null;
        try {
            cranes = dao.queryForMatching(crane);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

    public void deleteAll() {
        try {
            dao.executeRaw("delete from switchrec;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SwitchRec> queryPage(long offset, long limit) {
        List<LogEntity> out = new ArrayList();
        List<SwitchRec> cranes = new ArrayList();
        try {
            cranes = dao.queryBuilder().offset(offset).limit(limit).orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

}

