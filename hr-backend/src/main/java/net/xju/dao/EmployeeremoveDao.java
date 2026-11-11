package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Employeeremove;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class EmployeeremoveDao {

    private final QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Employeeremove> findAll() throws SQLException {
        return runner.query("SELECT * FROM employeeremove ORDER BY removedate DESC", new BeanListHandler<>(Employeeremove.class));
    }

    public List<Employeeremove> findByPage(int offset, int size, Integer eid) throws SQLException {
        String sql = "SELECT * FROM employeeremove WHERE (? IS NULL OR eid = ?) ORDER BY removedate DESC LIMIT ?, ?";
        return runner.query(sql, new BeanListHandler<>(Employeeremove.class), eid, eid, offset, size);
    }

    public int count(Integer eid) throws SQLException {
        Long total = runner.query("SELECT COUNT(*) FROM employeeremove WHERE (? IS NULL OR eid = ?)", new ScalarHandler<Long>(), eid, eid);
        return total == null ? 0 : total.intValue();
    }

    public Employeeremove findById(Integer id) throws SQLException {
        List<Employeeremove> list = runner.query("SELECT * FROM employeeremove WHERE id = ?", new BeanListHandler<>(Employeeremove.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(Employeeremove remove) throws SQLException {
        String sql = "INSERT INTO employeeremove (eid, afterdepid, afterjobid, removedate, reason, remark) VALUES (?, ?, ?, ?, ?, ?)";
        return runner.update(sql, remove.getEid(), remove.getAfterdepid(), remove.getAfterjobid(), remove.getRemovedate(), remove.getReason(), remove.getRemark());
    }

    public int update(Employeeremove remove) throws SQLException {
        String sql = "UPDATE employeeremove SET eid=?, afterdepid=?, afterjobid=?, removedate=?, reason=?, remark=? WHERE id=?";
        return runner.update(sql, remove.getEid(), remove.getAfterdepid(), remove.getAfterjobid(), remove.getRemovedate(), remove.getReason(), remove.getRemark(), remove.getId());
    }

    public int deleteById(Integer id) throws SQLException {
        return runner.update("DELETE FROM employeeremove WHERE id = ?", id);
    }
}
