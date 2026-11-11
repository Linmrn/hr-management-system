package net.xju.service;

import net.xju.dao.PoliticsstatusDao;
import net.xju.entity.Politicsstatus;

import java.sql.SQLException;
import java.util.List;

public class PoliticsstatusService {

    private PoliticsstatusDao politicsstatusDao = new PoliticsstatusDao();

    // 获取所有政治面貌
    public List<Politicsstatus> getAllPoliticsstatus() throws SQLException {
        return politicsstatusDao.findAll();
    }
}
