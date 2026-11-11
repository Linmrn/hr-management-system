package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Employeetrain;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class EmployeetrainDao {

    private final QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Employeetrain> findAll() throws SQLException {
        return runner.query("SELECT * FROM employeetrain ORDER BY traindate DESC", new BeanListHandler<>(Employeetrain.class));
    }

    public List<Employeetrain> findByPage(int offset, int size, Integer eid) throws SQLException {
        String sql = "SELECT * FROM employeetrain WHERE (? IS NULL OR eid = ?) ORDER BY traindate DESC LIMIT ?, ?";
        return runner.query(sql, new BeanListHandler<>(Employeetrain.class), eid, eid, offset, size);
    }

    public int count(Integer eid) throws SQLException {
        Long total = runner.query("SELECT COUNT(*) FROM employeetrain WHERE (? IS NULL OR eid = ?)", new ScalarHandler<Long>(), eid, eid);
        return total == null ? 0 : total.intValue();
    }

    public Employeetrain findById(Integer id) throws SQLException {
        List<Employeetrain> list = runner.query("SELECT * FROM employeetrain WHERE id = ?", new BeanListHandler<>(Employeetrain.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(Employeetrain train) throws SQLException {
        String sql = "INSERT INTO employeetrain (eid, traindate, traincontent, picture, remark) VALUES (?, ?, ?, ?, ?)";
        return runner.update(sql, train.getEid(), train.getTraindate(), train.getTraincontent(), train.getPicture(), train.getRemark());
    }

    public int update(Employeetrain train) throws SQLException {
        String sql = "UPDATE employeetrain SET eid=?, traindate=?, traincontent=?, picture=?, remark=? WHERE id=?";
        return runner.update(sql, train.getEid(), train.getTraindate(), train.getTraincontent(), train.getPicture(), train.getRemark(), train.getId());
    }

    public int deleteById(Integer id) throws SQLException {
        return runner.update("DELETE FROM employeetrain WHERE id = ?", id);
    }
}
