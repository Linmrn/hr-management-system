package net.xju.service;

import net.xju.dao.PositionDao;
import net.xju.entity.Position;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class PositionService {

    private PositionDao positionDao = new PositionDao();

    // 获取所有职位
    public List<Position> getAllPositions() throws SQLException {
        return positionDao.findAll();
    }

    // 添加职位
    public Integer addPosition(Position position) throws SQLException {
        position.setEnabled(true);
        position.setCreateDate(new Date());
        return positionDao.insert(position);
    }

    // 更新职位
    public Integer updatePositions(Position position) throws SQLException {
        return positionDao.update(position);
    }

    // 删除单个职位
    public Integer deletePositionById(Integer id) throws SQLException {
        return positionDao.deleteById(id);
    }

    // 批量删除职位
    public Integer deletePositionsByIds(Integer[] ids) throws SQLException {
        return positionDao.deleteByIds(ids);
    }
}
