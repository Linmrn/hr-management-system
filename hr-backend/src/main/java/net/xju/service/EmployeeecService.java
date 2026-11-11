package net.xju.service;

import net.xju.dao.EmployeeecDao;
import net.xju.entity.Employeeec;
import net.xju.entity.Employee;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class EmployeeecService {

    private final EmployeeecDao employeeecDao = new EmployeeecDao();

    public List<Employeeec> getAllEmployeeec() throws SQLException {
        return employeeecDao.findAll();
    }

    public List<Employeeec> getEmployeeecByPage(int page, int size, Integer eid) throws SQLException {
        return employeeecDao.findByPage((page - 1) * size, size, eid);
    }

    public int count(Integer eid) throws SQLException {
        return employeeecDao.count(eid);
    }

    public Employeeec getEmployeeecById(Integer id) throws SQLException {
        return employeeecDao.findById(id);
    }

    public Integer addEmployeeec(Employeeec ec) throws SQLException {
        if (ec.getEcdate() == null) {
            ec.setEcdate(new Date());
        }
        return employeeecDao.insert(ec);
    }

    public Integer updateEmployeeec(Employeeec ec) throws SQLException {
        return employeeecDao.update(ec);
    }

    public Integer deleteEmployeeecById(Integer id) throws SQLException {
        return employeeecDao.deleteById(id);
    }
}
