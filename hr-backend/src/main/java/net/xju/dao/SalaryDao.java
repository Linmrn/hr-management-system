package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Salary;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.List;

public class SalaryDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Salary> findAll() throws SQLException {
        String sql = "SELECT * FROM salary";
        return runner.query(sql, new BeanListHandler<>(Salary.class));
    }

    public int insert(Salary salary) throws SQLException {
        String sql = "INSERT INTO salary (basicSalary, bonus, lunchSalary, trafficSalary, allSalary, " +
                "pensionBase, pensionPer, createDate, medicalBase, medicalPer, " +
                "accumulationFundBase, accumulationFundPer, name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return runner.update(sql,
                salary.getBasicSalary(),
                salary.getBonus(),
                salary.getLunchSalary(),
                salary.getTrafficSalary(),
                salary.getAllSalary(),
                salary.getPensionBase(),
                salary.getPensionPer(),
                salary.getCreateDate(),
                salary.getMedicalBase(),
                salary.getMedicalPer(),
                salary.getAccumulationFundBase(),
                salary.getAccumulationFundPer(),
                salary.getName());
    }

    public int deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM salary WHERE id = ?";
        return runner.update(sql, id);
    }

    public int update(Salary salary) throws SQLException {
        String sql = "UPDATE salary SET basicSalary=?, bonus=?, lunchSalary=?, trafficSalary=?, allSalary=?, " +
                "pensionBase=?, pensionPer=?, medicalBase=?, medicalPer=?, accumulationFundBase=?, " +
                "accumulationFundPer=?, name=? WHERE id=?";
        return runner.update(sql,
                salary.getBasicSalary(),
                salary.getBonus(),
                salary.getLunchSalary(),
                salary.getTrafficSalary(),
                salary.getAllSalary(),
                salary.getPensionBase(),
                salary.getPensionPer(),
                salary.getMedicalBase(),
                salary.getMedicalPer(),
                salary.getAccumulationFundBase(),
                salary.getAccumulationFundPer(),
                salary.getName(),
                salary.getId());
    }
}
