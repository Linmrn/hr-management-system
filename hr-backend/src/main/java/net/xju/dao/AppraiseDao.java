package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Appraise;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class AppraiseDao {

    private final QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Appraise> findAll() throws SQLException {
        return runner.query("SELECT * FROM appraise ORDER BY appdate DESC", new BeanListHandler<>(Appraise.class));
    }

    public List<Appraise> findByPage(int offset, int size, Integer eid) throws SQLException {
        String sql = "SELECT * FROM appraise WHERE (? IS NULL OR eid = ?) ORDER BY appdate DESC LIMIT ?, ?";
        return runner.query(sql, new BeanListHandler<>(Appraise.class), eid, eid, offset, size);
    }

    public int count(Integer eid) throws SQLException {
        Long total = runner.query("SELECT COUNT(*) FROM appraise WHERE (? IS NULL OR eid = ?)", new ScalarHandler<Long>(), eid, eid);
        return total == null ? 0 : total.intValue();
    }

    public Appraise findById(Integer id) throws SQLException {
        List<Appraise> list = runner.query("SELECT * FROM appraise WHERE id = ?", new BeanListHandler<>(Appraise.class), id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(Appraise appraise) throws SQLException {
        String sql = "INSERT INTO appraise (eid, appdate, appresult, appcontent, remark) VALUES (?, ?, ?, ?, ?)";
        return runner.update(sql, appraise.getEid(), appraise.getAppdate(), appraise.getAppresult(), appraise.getAppcontent(), appraise.getRemark());
    }

    public int update(Appraise appraise) throws SQLException {
        String sql = "UPDATE appraise SET eid=?, appdate=?, appresult=?, appcontent=?, remark=? WHERE id=?";
        return runner.update(sql, appraise.getEid(), appraise.getAppdate(), appraise.getAppresult(), appraise.getAppcontent(), appraise.getRemark(), appraise.getId());
    }

    public int deleteById(Integer id) throws SQLException {
        return runner.update("DELETE FROM appraise WHERE id = ?", id);
    }
}
