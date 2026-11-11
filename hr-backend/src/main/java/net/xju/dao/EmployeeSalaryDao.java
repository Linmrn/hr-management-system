package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Salary;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class EmployeeSalaryDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    /**
     * 根据员工ID查询其关联的工资账套完整信息
     * @param eid 员工ID
     * @return Salary 对象，若无关联则返回 null
     */
    public Salary findSalaryByEid(Integer eid) throws SQLException {
        String sql = "SELECT s.* FROM salary s " +
                "JOIN empsalary es ON s.id = es.sid " +
                "WHERE es.eid = ?";
        return runner.query(sql, new BeanHandler<>(Salary.class), eid);
    }
}