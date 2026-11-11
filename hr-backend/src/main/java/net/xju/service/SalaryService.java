package net.xju.service;

import net.xju.dao.SalaryDao;
import net.xju.entity.Salary;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SalaryService {

    private SalaryDao salaryDao = new SalaryDao();

    // 获取所有工资账套
    public List<Salary> getAllSalaries() throws SQLException {
        return salaryDao.findAll();
    }

    // 添加工资账套
    public Integer addSalary(Salary salary) throws SQLException {
        salary.setCreateDate(new Date());
        return salaryDao.insert(salary);
    }

    // 删除工资账套
    public Integer deleteSalaryById(Integer id) throws SQLException {
        return salaryDao.deleteById(id);
    }

    // 更新工资账套
    public Integer updateSalaryById(Salary salary) throws SQLException {
        return salaryDao.update(salary);
    }
}
