package net.xju.service;

import net.xju.dao.EmployeeremoveDao;
import net.xju.entity.Employeeremove;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class EmployeeremoveService {

    private final EmployeeremoveDao employeeremoveDao = new EmployeeremoveDao();

    public List<Employeeremove> getAllEmployeeremove() throws SQLException {
        return employeeremoveDao.findAll();
    }

    public List<Employeeremove> getEmployeeremoveByPage(int page, int size, Integer eid) throws SQLException {
        return employeeremoveDao.findByPage((page - 1) * size, size, eid);
    }

    public int count(Integer eid) throws SQLException {
        return employeeremoveDao.count(eid);
    }

    public Employeeremove getEmployeeremoveById(Integer id) throws SQLException {
        return employeeremoveDao.findById(id);
    }

    public Integer addEmployeeremove(Employeeremove remove) throws SQLException {
        if (remove.getRemovedate() == null) {
            remove.setRemovedate(new Date());
        }
        return employeeremoveDao.insert(remove);
    }

    public Integer updateEmployeeremove(Employeeremove remove) throws SQLException {
        return employeeremoveDao.update(remove);
    }

    public Integer deleteEmployeeremoveById(Integer id) throws SQLException {
        return employeeremoveDao.deleteById(id);
    }
}
