package com.wooddeep.crane.persist.dao.log;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.LogDbHelper;
import com.wooddeep.crane.persist.entity.log.RealData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 工作记录, 记录一次吊重工作的最大值

public class RealDataDao extends LogDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<RealData, Integer> dao;

    public RealDataDao(Context context) {
        this.context = context;
        try {
            this.dao = LogDbHelper.getInstance(context).getDaoX(RealData.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向user表中添加一条数据
    public void insert(RealData data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(RealData data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public void update(RealData data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询user表中的所有数据
    public List<RealData> selectAll() {
        List<RealData> cranes = null;
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
    public RealData queryById(int id) {
        RealData crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public RealData queryLatestOne() {
        List<RealData> cranes;
        RealData out = null;
        try {
            cranes = dao.queryBuilder().offset((long) 0).limit((long) 1).orderBy("id", false).query();
            if (cranes != null && cranes.size() > 0) {
                out = cranes.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    public List<RealData> queryForMatching(RealData crane) {
        List<RealData> cranes = null;
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

    public List<RealData> queryPage(long offset, long limit) {
        List<RealData> cranes = null;
        try {
            cranes = dao.queryBuilder().offset(offset).limit(limit).orderBy("id", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

}

