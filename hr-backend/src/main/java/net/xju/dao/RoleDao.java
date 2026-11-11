package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Role;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class RoleDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Role> findAll() throws SQLException {
        String sql = "SELECT * FROM role";
        return runner.query(sql, new BeanListHandler<>(Role.class));
    }

    public int insert(Role role) throws SQLException {
        String sql = "INSERT INTO role (name, nameZh) VALUES (?, ?)";
        return runner.update(sql, role.getName(), role.getNameZh());
    }

    public int deleteById(Integer rid) throws SQLException {
        String sql = "DELETE FROM role WHERE id = ?";
        return runner.update(sql, rid);
    }
}
