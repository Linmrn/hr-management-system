package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Hr;
import net.xju.entity.Role;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HrDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    // ---------- 根据用户名查询用户 ----------
    public Hr loadUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM hr WHERE username = ?";
        Hr hr = runner.query(sql, new BeanHandler<>(Hr.class), username);
        if (hr != null) {
            String roleSql = "SELECT r.* FROM role r " +
                    "JOIN hr_role hrr ON r.id = hrr.rid " +
                    "WHERE hrr.hrid = ?";
            List<Role> roles = runner.query(roleSql, new BeanListHandler<>(Role.class), hr.getId());
            hr.setRoles(roles);
        }
        return hr;
    }

    // ---------- 根据 ID 查询用户 ----------
    public Hr getHrById(int id) throws SQLException {
        String sql = "SELECT * FROM hr WHERE id = ?";
        return runner.query(sql, new BeanHandler<>(Hr.class), id);
    }

    // ---------- 查询所有 HR ----------
    public List<Hr> getAllHrsExceptCurrent(int currentUserId, String keywords) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM hr WHERE id != ?");
        if (keywords != null && !keywords.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            return runner.query(sql.toString(), new BeanListHandler<>(Hr.class),
                    currentUserId, "%" + keywords.trim() + "%");
        } else {
            return runner.query(sql.toString(), new BeanListHandler<>(Hr.class), currentUserId);
        }
    }

    // ---------- 查询除当前用户外的所有 HR ----------
    public List<Hr> getAllHrsExceptCurrent(int currentUserId) throws SQLException {
        String sql = "SELECT * FROM hr WHERE id != ?";
        return runner.query(sql, new BeanListHandler<>(Hr.class), currentUserId);
    }

    // ---------- 新增 HR ----------
    public boolean insert(Hr hr) throws SQLException {
        String sql = "INSERT INTO hr (name, username, password, enabled) VALUES (?, ?, ?, ?)";
        return runner.update(sql,
                hr.getName(),
                hr.getUsername(),
                hr.getPassword(),
                hr.getEnabled()) == 1;
    }

    // ---------- 更新 HR 信息 ----------
    public int update(Hr hr) throws SQLException {
        String sql = "UPDATE hr SET name=?, phone=?, telephone=?, address=?, enabled=?, username=?, remark=? WHERE id=?";
        return runner.update(sql,
                hr.getName(),
                hr.getPhone(),
                hr.getTelephone(),
                hr.getAddress(),
                hr.getEnabled(),
                hr.getUsername(),
                hr.getRemark(),
                hr.getId());
    }

    // ---------- 更新密码 ----------
    public int upgradePassword(int id, String oldPassword, String hash) throws SQLException {
        return runner.update("UPDATE hr SET password=? WHERE id=? AND BINARY password=?", hash, id, oldPassword);
    }

    public int updatePassword(int id, String newPassword) throws SQLException {
        String sql = "UPDATE hr SET password = ? WHERE id = ?";
        return runner.update(sql, newPassword, id);
    }

    // ---------- 更新头像 ----------
    public int updateUserface(int id, String url) throws SQLException {
        String sql = "UPDATE hr SET userface = ? WHERE id = ?";
        return runner.update(sql, url, id);
    }

    // ---------- 删除 HR ----------
    public int deleteById(int id) throws SQLException {
        String sql = "DELETE FROM hr WHERE id = ?";
        return runner.update(sql, id);
    }

    // ---------- 更新 HR 角色（事务） ----------
    public boolean updateRoles(int hrid, int[] rids) throws SQLException {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
            QueryRunner txRunner = new QueryRunner();
            // 删除旧角色
            txRunner.update(conn, "DELETE FROM hr_role WHERE hrid = ?", hrid);
            // 插入新角色
            if (rids != null && rids.length > 0) {
                String insertSql = "INSERT INTO hr_role (hrid, rid) VALUES (?, ?)";
                for (int rid : rids) {
                    txRunner.update(conn, insertSql, hrid, rid);
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw e;
        } finally {
            JDBCUtils.close();
        }
    }
}