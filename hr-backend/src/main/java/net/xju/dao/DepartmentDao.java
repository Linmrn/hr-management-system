package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Department;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

public class DepartmentDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Department> findRootDepartments() throws SQLException {
        String sql = "SELECT * FROM department WHERE parentId = -1";
        return runner.query(sql, new BeanListHandler<>(Department.class));
    }

    public List<Department> findAll() throws SQLException {
        return runner.query("SELECT * FROM department", new BeanListHandler<>(Department.class));
    }

    public void insert(Department dep) throws SQLException {
        String sql = "INSERT INTO department (name, parentId, depPath, enabled, isParent) VALUES (?, ?, ?, ?, ?)";
        runner.update(sql, dep.getName(), dep.getParentId(), dep.getDepPath(), dep.getEnabled(), dep.getParent());
    }

    public int update(Department dep) throws SQLException {
        String sql = "UPDATE department SET name = ?, parentId = ?, depPath = ?, enabled = ?, isParent = ? WHERE id = ?";
        return runner.update(sql, dep.getName(), dep.getParentId(), dep.getDepPath(), dep.getEnabled(), dep.getParent(), dep.getId());
    }

    public Long countChildren(int depId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM department WHERE parentId = ?";
        return runner.query(sql, new ScalarHandler<Long>(), depId);
    }

    public Long countEmployees(int depId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employee WHERE departmentId = ?";
        return runner.query(sql, new ScalarHandler<Long>(), depId);
    }

    public Integer getIsParent(int depId) throws SQLException {
        String sql = "SELECT isParent FROM department WHERE id = ?";
        return runner.query(sql, new ScalarHandler<Integer>(), depId);
    }

    public Integer getParentId(int depId) throws SQLException {
        String sql = "SELECT parentId FROM department WHERE id = ?";
        return runner.query(sql, new ScalarHandler<Integer>(), depId);
    }

    public int deleteById(int depId) throws SQLException {
        String sql = "DELETE FROM department WHERE id = ?";
        return runner.update(sql, depId);
    }

    public void updateIsParent(int depId, int isParent) throws SQLException {
        String sql = "UPDATE department SET isParent = ? WHERE id = ?";
        runner.update(sql, isParent, depId);
    }

    public List<Department> findWithoutChildren() throws SQLException {
        String sql = "SELECT * FROM department WHERE isParent = 0";
        return runner.query(sql, new BeanListHandler<>(Department.class));
    }

    public List<Department> findAllEnabled() throws SQLException {
        String sql = "SELECT id, name, parentId FROM department WHERE enabled = 1 ORDER BY id";
        return runner.query(sql, new BeanListHandler<>(Department.class));
    }
}
