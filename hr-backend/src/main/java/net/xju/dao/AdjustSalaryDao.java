package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.AdjustSalary;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class AdjustSalaryDao {

    private final QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<AdjustSalary> findAll() throws SQLException {
        return runner.query("SELECT * FROM adjustsalary ORDER BY asdate DESC", new BeanListHandler<>(AdjustSalary.class));
    }

    public List<AdjustSalary> findByPage(int offset, int size, Integer eid) throws SQLException {
        String sql = "SELECT * FROM adjustsalary WHERE (? IS NULL OR eid = ?) ORDER BY asdate DESC LIMIT ?, ?";
        return runner.query(sql, new BeanListHandler<>(AdjustSalary.class), eid, eid, offset, size);
    }

    public int count(Integer eid) throws SQLException {
        Long total = runner.query("SELECT COUNT(*) FROM adjustsalary WHERE (? IS NULL OR eid = ?)", new ScalarHandler<Long>(), eid, eid);
        return total == null ? 0 : total.intValue();
    }

    public AdjustSalary findById(Integer id) throws SQLException {
        List<AdjustSalary> list = runner.query("SELECT * FROM adjustsalary WHERE id = ?", new BeanListHandler<>(AdjustSalary.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(AdjustSalary adjustSalary) throws SQLException {
        String sql = "INSERT INTO adjustsalary (eid, asdate, beforesalary, aftersalary, reason, remark) VALUES (?, ?, ?, ?, ?, ?)";
        return runner.update(sql, adjustSalary.getEid(), adjustSalary.getAsdate(), adjustSalary.getBeforesalary(), adjustSalary.getAftersalary(), adjustSalary.getReason(), adjustSalary.getRemark());
    }

    public int update(AdjustSalary adjustSalary) throws SQLException {
        String sql = "UPDATE adjustsalary SET eid=?, asdate=?, beforesalary=?, aftersalary=?, reason=?, remark=? WHERE id=?";
        return runner.update(sql, adjustSalary.getEid(), adjustSalary.getAsdate(), adjustSalary.getBeforesalary(), adjustSalary.getAftersalary(), adjustSalary.getReason(), adjustSalary.getRemark(), adjustSalary.getId());
    }

    public int deleteById(Integer id) throws SQLException {
        return runner.update("DELETE FROM adjustsalary WHERE id = ?", id);
    }
}
