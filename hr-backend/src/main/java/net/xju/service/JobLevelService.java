package net.xju.service;

import net.xju.dao.JobLevelDao;
import net.xju.entity.JobLevel;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class JobLevelService {

    private JobLevelDao jobLevelDao = new JobLevelDao();

    // 获取所有职称
    public List<JobLevel> getAllJobLevels() throws SQLException {
        return jobLevelDao.findAll();
    }

    // 添加职称
    public Integer addJobLevel(JobLevel jobLevel) throws SQLException {
        jobLevel.setCreateDate(new Date());
        jobLevel.setEnabled(true);
        return jobLevelDao.insert(jobLevel);
    }

    // 更新职称
    public Integer updateJobLevelById(JobLevel jobLevel) throws SQLException {
        return jobLevelDao.update(jobLevel);
    }

    // 删除单个职称
    public Integer deleteJobLevelById(Integer id) throws SQLException {
        return jobLevelDao.deleteById(id);
    }

    // 批量删除职称（通过ID数组）
    public Integer deleteJobLevelsByIds(Integer[] ids) throws SQLException {
        return jobLevelDao.deleteByIds(ids);
    }
}
