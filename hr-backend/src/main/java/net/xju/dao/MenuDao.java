package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Menu;
import net.xju.entity.Role;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MenuDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    // 根据用户ID查询该用户有权限的菜单（扁平列表）
    public List<Menu> getMenusByHrId(int hrId) throws SQLException {
        String sql = "SELECT DISTINCT m.* FROM menu m " +
                "LEFT JOIN menu_role mr ON m.id = mr.mid " +
                "LEFT JOIN hr_role hr ON mr.rid = hr.rid " +
                "WHERE hr.hrid = ? AND m.enabled = 1";
        return runner.query(sql, new BeanListHandler<>(Menu.class), hrId);
    }

    // 查询所有菜单（不含角色）
    public List<Menu> getAllMenus() throws SQLException {
        String sql = "SELECT * FROM menu";
        return runner.query(sql, new BeanListHandler<>(Menu.class));
    }

    // 查询所有菜单（含角色信息）
    public List<Menu> getAllMenusWithRole() throws SQLException {
        List<Menu> menus = getAllMenus();
        for (Menu menu : menus) {
            String roleSql = "SELECT r.* FROM role r " +
                    "LEFT JOIN menu_role mr ON r.id = mr.rid " +
                    "WHERE mr.mid = ?";
            List<Role> roles = runner.query(roleSql, new BeanListHandler<>(Role.class), menu.getId());
            menu.setRoles(roles);
        }
        return menus;
    }

    // 根据角色ID查询拥有的菜单ID列表
    public List<Integer> getMidsByRid(int rid) throws SQLException {
        String sql = "SELECT mid FROM menu_role WHERE rid = ?";
        return runner.query(sql, new ColumnListHandler<Integer>("mid"), rid);
    }

    // 更新角色菜单关联（事务）
    public void updateMenuRole(int rid, Integer[] mids) throws SQLException {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
            QueryRunner txRunner = new QueryRunner();
            // 删除原有关联
            txRunner.update(conn, "DELETE FROM menu_role WHERE rid = ?", rid);
            // 插入新关联
            if (mids != null && mids.length > 0) {
                String insertSql = "INSERT INTO menu_role (mid, rid) VALUES (?, ?)";
                for (int mid : mids) {
                    txRunner.update(conn, insertSql, mid, rid);
                }
            }
            conn.commit();
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