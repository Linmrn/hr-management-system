package net.xju.service;

import net.xju.dao.RoleDao;
import net.xju.entity.Role;

import java.sql.SQLException;
import java.util.List;

public class RoleService {

    private RoleDao roleDao = new RoleDao();

    // 获取所有角色
    public List<Role> getAllRoles() throws SQLException {
        return roleDao.findAll();
    }

    // 添加角色（自动添加 ROLE_ 前缀）
    public Integer addRole(Role role) throws SQLException {
        if (role.getName() != null && !role.getName().startsWith("ROLE_")) {
            role.setName("ROLE_" + role.getName());
        }
        return roleDao.insert(role);
    }

    // 删除角色
    public Integer deleteRoleById(Integer rid) throws SQLException {
        return roleDao.deleteById(rid);
    }
}
