package net.xju.service;

import net.xju.dao.NationDao;
import net.xju.entity.Nation;

import java.sql.SQLException;
import java.util.List;

public class NationService {

    private NationDao nationDao = new NationDao();

    // 获取所有民族
    public List<Nation> getAllNations() throws SQLException {
        return nationDao.findAll();
    }
}
