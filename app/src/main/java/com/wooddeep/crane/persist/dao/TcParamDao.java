package com.wooddeep.crane.persist.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.LoadDbHelper;
import com.wooddeep.crane.persist.entity.TcParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ormlite的使用
// https://blog.csdn.net/industriously/article/details/50790624

public class TcParamDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<TcParam, Integer> dao;

    public TcParamDao(Context context) {
        this.context = context;
        try {
            this.dao = LoadDbHelper.getInstance(context).getDaoX(TcParam.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<String> getCraneTypes() {
        List<String> out = new ArrayList<>();
        try {
            List<TcParam> loads = dao.queryBuilder()
                .selectColumns("Type").distinct().query();
            for (TcParam load : loads) {
                out.add(load.getCraneType());
            }
        } catch (Exception e) {

        }
        return out;
    }

    public List<String> getArmLengths(String craneType) {
        List<String> out = new ArrayList<>();

        try {
            List<TcParam> loads = dao.queryBuilder().selectColumns("Length").distinct()
                .where().eq("Type", craneType).query();
            for (TcParam load : loads) {
                out.add(load.getArmLength());
            }
        } catch (Exception e) {
            // NOTHING
        }
        return out;
    }

    public List<String> getCables(String craneType, String armLength) {
        List<String> out = new ArrayList<>();

        try {
            List<TcParam> loads = dao.queryBuilder().selectColumns("Rate").distinct().where()
                .eq("Type", craneType).and().eq("Length", armLength).query();
            for (TcParam load : loads) {
                out.add(load.getPower());
            }
        } catch (Exception e) {
            // NOTHING
        }
        return out;
    }


    public List<TcParam> getLoads(String craneType, String armLength, String power) {
        List<TcParam> out = new ArrayList<>();

        try {
            List<TcParam> loads = dao.queryBuilder().where().eq("Type", craneType)
                .and().eq("Length", armLength).and().eq("Rate", power).query();
            out = loads;
        } catch (Exception e) {
            // NOTHING
        }
        return out;
    }

    public TcParam getSaveLoad() {
        List<TcParam> out = new ArrayList<>();

        try {
            List<TcParam> loads = dao.queryBuilder().where().eq("Distance", "-1").query();
            out = loads;
        } catch (Exception e) {
            // NOTHING
        }

        if (out.size() <= 0) return null;

        return out.get(0);
    }


    // 向user表中添加一条数据
    public void insert(TcParam data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(TcParam data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            dao.executeRaw("delete from load;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public void update(TcParam data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询user表中的所有数据
    public List<TcParam> selectAll() {
        List<TcParam> cranes = null;
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
    public TcParam queryById(int id) {
        TcParam crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public List<TcParam> queryForMatching(TcParam crane) {
        List<TcParam> cranes = null;
        try {
            cranes = dao.queryForMatching(crane);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

}

