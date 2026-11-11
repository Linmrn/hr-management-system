package net.xju.service;

import net.xju.dao.AppraiseDao;
import net.xju.entity.Appraise;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AppraiseService {

    private final AppraiseDao appraiseDao = new AppraiseDao();

    public List<Appraise> getAllAppraise() throws SQLException {
        return appraiseDao.findAll();
    }

    public List<Appraise> getAppraiseByPage(int page, int size, Integer eid) throws SQLException {
        return appraiseDao.findByPage((page - 1) * size, size, eid);
    }

    public int count(Integer eid) throws SQLException {
        return appraiseDao.count(eid);
    }

    public Appraise getAppraiseById(Integer id) throws SQLException {
        return appraiseDao.findById(id);
    }

    public Integer addAppraise(Appraise appraise) throws SQLException {
        if (appraise.getAppdate() == null) {
            appraise.setAppdate(new Date());
        }
        return appraiseDao.insert(appraise);
    }

    public Integer updateAppraise(Appraise appraise) throws SQLException {
        return appraiseDao.update(appraise);
    }

    public Integer deleteAppraiseById(Integer id) throws SQLException {
        return appraiseDao.deleteById(id);
    }
}
