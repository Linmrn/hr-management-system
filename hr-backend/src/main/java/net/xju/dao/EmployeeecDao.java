package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Employeeec;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class EmployeeecDao {

    private final QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Employeeec> findAll() throws SQLException {
        return runner.query("SELECT * FROM employeeec ORDER BY ecdate DESC", new BeanListHandler<>(Employeeec.class));
    }

    public List<Employeeec> findByPage(int offset, int size, Integer eid) throws SQLException {
        String sql = "SELECT * FROM employeeec WHERE (? IS NULL OR eid = ?) ORDER BY ecdate DESC LIMIT ?, ?";
        return runner.query(sql, new BeanListHandler<>(Employeeec.class), eid, eid, offset, size);
    }

    public int count(Integer eid) throws SQLException {
        Long total = runner.query("SELECT COUNT(*) FROM employeeec WHERE (? IS NULL OR eid = ?)", new ScalarHandler<Long>(), eid, eid);
        return total == null ? 0 : total.intValue();
    }

    public Employeeec findById(Integer id) throws SQLException {
        List<Employeeec> list = runner.query("SELECT * FROM employeeec WHERE id = ?", new BeanListHandler<>(Employeeec.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(Employeeec ec) throws SQLException {
        String sql = "INSERT INTO employeeec (eid, ecdate, ecreason, ecpoint, ectype, remark) VALUES (?, ?, ?, ?, ?, ?)";
        return runner.update(sql, ec.getEid(), ec.getEcdate(), ec.getEcreason(), ec.getEcpoint(), ec.getEctype(), ec.getRemark());
    }

    public int update(Employeeec ec) throws SQLException {
        String sql = "UPDATE employeeec SET eid=?, ecdate=?, ecreason=?, ecpoint=?, ectype=?, remark=? WHERE id=?";
        return runner.update(sql, ec.getEid(), ec.getEcdate(), ec.getEcreason(), ec.getEcpoint(), ec.getEctype(), ec.getRemark(), ec.getId());
    }

    public int deleteById(Integer id) throws SQLException {
        return runner.update("DELETE FROM employeeec WHERE id = ?", id);
    }
}
