package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Nation;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class NationDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Nation> findAll() throws SQLException {
        String sql = "SELECT * FROM nation";
        return runner.query(sql, new BeanListHandler<>(Nation.class));
    }
}
