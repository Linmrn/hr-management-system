package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Politicsstatus;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class PoliticsstatusDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Politicsstatus> findAll() throws SQLException {
        String sql = "SELECT * FROM politicsstatus";
        return runner.query(sql, new BeanListHandler<>(Politicsstatus.class));
    }
}
