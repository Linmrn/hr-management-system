package net.xju.service;

import net.xju.dao.AdjustSalaryDao;
import net.xju.entity.AdjustSalary;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AdjustSalaryService {

    private final AdjustSalaryDao adjustSalaryDao = new AdjustSalaryDao();

    public List<AdjustSalary> getAllAdjustSalary() throws SQLException {
        return adjustSalaryDao.findAll();
    }

    public List<AdjustSalary> getAdjustSalaryByPage(int page, int size, Integer eid) throws SQLException {
        return adjustSalaryDao.findByPage((page - 1) * size, size, eid);
    }

    public int count(Integer eid) throws SQLException {
        return adjustSalaryDao.count(eid);
    }

    public AdjustSalary getAdjustSalaryById(Integer id) throws SQLException {
        return adjustSalaryDao.findById(id);
    }

    public Integer addAdjustSalary(AdjustSalary adjustSalary) throws SQLException {
        if (adjustSalary.getAsdate() == null) {
            adjustSalary.setAsdate(new Date());
        }
        return adjustSalaryDao.insert(adjustSalary);
    }

    public Integer updateAdjustSalary(AdjustSalary adjustSalary) throws SQLException {
        return adjustSalaryDao.update(adjustSalary);
    }

    public Integer deleteAdjustSalaryById(Integer id) throws SQLException {
        return adjustSalaryDao.deleteById(id);
    }
}
