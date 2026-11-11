package net.xju.service;

import net.xju.dao.EmployeetrainDao;
import net.xju.entity.Employeetrain;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class EmployeetrainService {

    private final EmployeetrainDao employeetrainDao = new EmployeetrainDao();

    public List<Employeetrain> getAllEmployeetrain() throws SQLException {
        return employeetrainDao.findAll();
    }

    public List<Employeetrain> getEmployeetrainByPage(int page, int size, Integer eid) throws SQLException {
        return employeetrainDao.findByPage((page - 1) * size, size, eid);
    }

    public int count(Integer eid) throws SQLException {
        return employeetrainDao.count(eid);
    }

    public Employeetrain getEmployeetrainById(Integer id) throws SQLException {
        return employeetrainDao.findById(id);
    }

    public Integer addEmployeetrain(Employeetrain train) throws SQLException {
        if (train.getTraindate() == null) {
            train.setTraindate(new Date());
        }
        return employeetrainDao.insert(train);
    }

    public Integer updateEmployeetrain(Employeetrain train) throws SQLException {
        return employeetrainDao.update(train);
    }

    public Integer deleteEmployeetrainById(Integer id) throws SQLException {
        return employeetrainDao.deleteById(id);
    }
}
