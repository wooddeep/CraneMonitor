package com.wooddeep.crane.persist.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.entity.Load;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// ormlite的使用
// https://blog.csdn.net/industriously/article/details/50790624

public class LoadDao {

    private Context context;
    // ORMLite提供的DAO类对象，第一个泛型是要操作的数据表映射成的实体类；第二个泛型是这个实体类中ID的数据类型
    private Dao<Load, Integer> dao;

    public LoadDao(Context context) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDaoX(Load.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<String> getCraneTypes() {
        List<String> out = new ArrayList<>();

        try {
            List<Load> loads = dao.queryBuilder().selectColumns("craneType").distinct().query();
            for (Load load : loads) {
                out.add(load.getCraneType());
            }
        } catch (Exception e) {

        }
        return out;
    }

    public List<String> getArmLengths(String craneType) {
        List<String> out = new ArrayList<>();

        try {
            List<Load> loads = dao.queryBuilder().selectColumns("armLength").distinct()
                .where().eq("craneType", craneType).query();
            for (Load load : loads) {
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
            List<Load> loads = dao.queryBuilder().selectColumns("power").distinct().where()
                .eq("craneType", craneType).and().eq("armLength", armLength).query();
            for (Load load : loads) {
                out.add(load.getPower());
            }
        } catch (Exception e) {
            // NOTHING
        }
        return out;
    }


    public List<Load> getLoads(String craneType, String armLength, String power) {
        List<Load> out = new ArrayList<>();

        try {
            List<Load> loads = dao.queryBuilder().where().eq("craneType", craneType)
                .and().eq("armLength", armLength).and().eq("power", power).query();
            out = loads;
        } catch (Exception e) {
            // NOTHING
        }
        return out;
    }

    public Load getSaveLoad() {
        List<Load> out = new ArrayList<>();

        try {
            List<Load> loads = dao.queryBuilder().where().eq("craneType", "X").query();
            out = loads;
        } catch (Exception e) {
            // NOTHING
        }

        if (out.size() <= 0) return null;

        return out.get(0);
    }


    // 向user表中添加一条数据
    public void insert(Load data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除user表中的一条数据
    public void delete(Load data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try {
            dao.executeRaw("delete from load where craneType != 'X';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改user表中的一条数据
    public void update(Load data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询user表中的所有数据
    public List<Load> selectAll() {
        List<Load> cranes = null;
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
    public Load queryById(int id) {
        Load crane = null;
        try {
            crane = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crane;
    }

    public List<Load> queryForMatching(Load crane) {
        List<Load> cranes = null;
        try {
            cranes = dao.queryForMatching(crane);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cranes;
    }

}

