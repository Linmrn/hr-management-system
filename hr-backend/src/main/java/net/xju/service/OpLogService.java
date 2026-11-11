package net.xju.service;

import net.xju.common.utils.HrUtils;
import net.xju.common.utils.PageInfo;
import net.xju.dao.OpLogDao;
import net.xju.entity.Hr;
import net.xju.entity.OpLog;

import java.sql.SQLException;
import java.util.Date;

public class OpLogService {

    private final OpLogDao opLogDao = new OpLogDao();

    public void log(String operate) throws SQLException {
        Hr currentHr = HrUtils.getCurrentHr();
        if (currentHr == null) return;
        OpLog opLog = new OpLog();
        opLog.setAdddate(new Date());
        opLog.setOperate(operate);
        opLog.setHrid(currentHr.getId());
        opLogDao.insert(opLog);
    }

    public PageInfo<OpLog> getByPage(int page, int size, String keyword) throws SQLException {
        return opLogDao.findByPage(page, size, keyword);
    }
}
