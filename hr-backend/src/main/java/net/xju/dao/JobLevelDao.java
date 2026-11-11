package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.JobLevel;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JobLevelDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<JobLevel> findAll() throws SQLException {
        String sql = "SELECT * FROM joblevel";
        return runner.query(sql, new BeanListHandler<>(JobLevel.class));
    }

    public int insert(JobLevel jobLevel) throws SQLException {
        String sql = "INSERT INTO joblevel (name, titleLevel, createDate, enabled) VALUES (?, ?, ?, ?)";
        return runner.update(sql,
                jobLevel.getName(),
                jobLevel.getTitleLevel(),
                jobLevel.getCreateDate(),
                jobLevel.getEnabled());
    }

    public int update(JobLevel jobLevel) throws SQLException {
        String sql = "UPDATE joblevel SET name = ?, titleLevel = ?, createDate = ?, enabled = ? WHERE id = ?";
        return runner.update(sql,
                jobLevel.getName(),
                jobLevel.getTitleLevel(),
                jobLevel.getCreateDate(),
                jobLevel.getEnabled(),
                jobLevel.getId());
    }

    public int deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM joblevel WHERE id = ?";
        return runner.update(sql, id);
    }

    public int deleteByIds(Integer[] ids) throws SQLException {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        String placeholders = Arrays.stream(ids)
                .map(id -> "?")
                .collect(Collectors.joining(","));
        String sql = "DELETE FROM joblevel WHERE id IN (" + placeholders + ")";
        return runner.update(sql, (Object[]) ids);
    }
}
