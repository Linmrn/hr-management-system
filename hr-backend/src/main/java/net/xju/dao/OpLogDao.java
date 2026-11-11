package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.common.utils.PageInfo;
import net.xju.entity.OpLog;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class OpLogDao {

    private final QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public int insert(OpLog opLog) throws SQLException {
        String sql = "INSERT INTO oplog (adddate, operate, hrid) VALUES (?, ?, ?)";
        return runner.update(sql, opLog.getAdddate(), opLog.getOperate(), opLog.getHrid());
    }

    public PageInfo<OpLog> findByPage(int page, int size, String keyword) throws SQLException {
        int offset = (page - 1) * size;
        String where = " WHERE 1=1 ";
        Object[] countParams;
        Object[] dataParams;
        if (keyword != null && !keyword.trim().isEmpty()) {
            where += " AND operate LIKE ? ";
            String like = "%" + keyword.trim() + "%";
            countParams = new Object[]{like};
            dataParams = new Object[]{like, offset, size};
        } else {
            countParams = new Object[]{};
            dataParams = new Object[]{offset, size};
        }

        String countSql = "SELECT COUNT(*) FROM oplog" + where;
        String dataSql = "SELECT * FROM oplog" + where + " ORDER BY adddate DESC LIMIT ?, ?";
        Long total = runner.query(countSql, new ScalarHandler<Long>(), countParams);
        List<OpLog> records = runner.query(dataSql, new BeanListHandler<>(OpLog.class), dataParams);
        return new PageInfo<>(page, size, total == null ? 0 : total.intValue(), records);
    }
}
