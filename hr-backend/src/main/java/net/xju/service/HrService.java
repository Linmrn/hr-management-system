package net.xju.service;

import net.xju.common.utils.HrUtils;
import net.xju.dao.HrDao;
import net.xju.entity.Hr;

import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

public class HrService {

    private HrDao hrDao = new HrDao();

    // ---------- 密码工具 ----------
    public static String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty() || rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
            throw new IllegalArgumentException("密码不能为空且不能超过72字节");
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2")) {
            if (rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) return false;
            try { return BCrypt.checkpw(rawPassword, storedPassword); }
            catch (IllegalArgumentException e) { return false; }
        }
        // Legacy passwords are upgraded after a successful login.
        return java.security.MessageDigest.isEqual(rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            storedPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public boolean verifyAndUpgradePassword(Hr hr, String password) throws SQLException {
        if (!checkPassword(password, hr.getPassword())) return false;
        if (!hr.getPassword().startsWith("$2")) {
            return hrDao.upgradePassword(hr.getId(), hr.getPassword(), encodePassword(password)) == 1;
        }
        return true;
    }

    // ---------- 根据用户名加载用户 ----------
    public Hr loadUserByUsername(String username) throws SQLException {
        return hrDao.loadUserByUsername(username);
    }

    // ---------- 获取所有 HR（排除当前用户） ----------
    public List<Hr> getAllHrs(String keywords) throws SQLException {
        Hr current = HrUtils.getCurrentHr();
        if (current == null) {
            throw new RuntimeException("用户未登录");
        }
        return hrDao.getAllHrsExceptCurrent(current.getId(), keywords);
    }

    // ---------- 更新 HR 信息 ----------
    public int updateHr(Hr hr) throws SQLException {
        return hrDao.update(hr);
    }

    // ---------- 更新 HR 角色 ----------
    public boolean updateHrRole(int hrid, int[] rids) throws SQLException {
        return hrDao.updateRoles(hrid, rids);
    }

    // ---------- 删除 HR ----------
    public int deleteHrById(int id) throws SQLException {
        return hrDao.deleteById(id);
    }

    // ---------- 获取除当前用户外的所有 HR ----------
    public List<Hr> getAllHrsExceptCurrentHr() throws SQLException {
        Hr current = HrUtils.getCurrentHr();
        if (current == null) {
            throw new RuntimeException("用户未登录");
        }
        return hrDao.getAllHrsExceptCurrent(current.getId());
    }

    // ---------- 根据 ID 获取 HR ----------
    public Hr getHrById(int id) throws SQLException {
        return hrDao.getHrById(id);
    }

    // ---------- 修改密码 ----------
    public boolean updateHrPasswd(String oldpass, String newpass, int hrid) throws SQLException {
        Hr hr = hrDao.getHrById(hrid);
        if (hr == null) {
            return false;
        }
        if (checkPassword(oldpass, hr.getPassword())) {
            return hrDao.updatePassword(hrid, encodePassword(newpass)) == 1;
        }
        return false;
    }

    // ---------- 更新用户头像 ----------
    public int updateUserface(String url, int id) throws SQLException {
        return hrDao.updateUserface(id, url);
    }

    // ---------- 注册 ----------
    public boolean register(Hr hr) throws SQLException {
        hr.setPassword(encodePassword(hr.getPassword()));
        return hrDao.insert(hr);
    }
}