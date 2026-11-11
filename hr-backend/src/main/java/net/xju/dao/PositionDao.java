package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Position;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositionDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Position> findAll() throws SQLException {
        String sql = "SELECT * FROM position";
        return runner.query(sql, new BeanListHandler<>(Position.class));
    }

    public int insert(Position position) throws SQLException {
        String sql = "INSERT INTO position (name, createDate, enabled) VALUES (?, ?, ?)";
        return runner.update(sql,
                position.getName(),
                position.getCreateDate(),
                position.getEnabled());
    }

    public int update(Position position) throws SQLException {
        String sql = "UPDATE position SET name = ?, createDate = ?, enabled = ? WHERE id = ?";
        return runner.update(sql,
                position.getName(),
                position.getCreateDate(),
                position.getEnabled(),
                position.getId());
    }

    public int deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM position WHERE id = ?";
        return runner.update(sql, id);
    }

    public int deleteByIds(Integer[] ids) throws SQLException {
        if (ids == null || ids.length == 0) {
            return 0;
        }
        String placeholders = Arrays.stream(ids)
                .map(id -> "?")
                .collect(Collectors.joining(","));
        String sql = "DELETE FROM position WHERE id IN (" + placeholders + ")";
        return runner.update(sql, (Object[]) ids);
    }
}
