package net.xju.service;

import net.xju.common.utils.HrUtils;
import net.xju.dao.MenuDao;
import net.xju.entity.Hr;
import net.xju.entity.Menu;
import net.xju.entity.Meta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;

public class MenuService {

    private MenuDao menuDao = new MenuDao();

    // 根据当前登录用户ID获取菜单扁平列表（原有方法保留）
    public List<Menu> getMenusByHrId() throws SQLException {
        Hr currentHr = HrUtils.getCurrentHr();
        if (currentHr == null) {
            throw new RuntimeException("用户未登录");
        }
        return wrapMeta(menuDao.getMenusByHrId(currentHr.getId()));
    }


    public List<Menu> getMenuTreeByHrId(int hrId) throws SQLException {
        return buildMenuTree(menuDao.getMenusByHrId(hrId), menuDao.getAllMenus());
    }

    static List<Menu> buildMenuTree(List<Menu> allMenus, List<Menu> catalog) {
        // Include only ancestors of granted menus, never unauthorized siblings.
        Map<Integer, Menu> available = new LinkedHashMap<>();
        for (Menu menu : catalog) {
            if (Boolean.TRUE.equals(menu.getEnabled())) available.put(menu.getId(), menu);
        }
        Map<Integer, Menu> selected = new LinkedHashMap<>();
        for (Menu menu : allMenus) {
            List<Menu> chain = new ArrayList<>();
            Set<Integer> visited = new HashSet<>();
            Menu current = menu;
            while (current != null && visited.add(current.getId())) {
                chain.add(current);
                if (Integer.valueOf(1).equals(current.getParentId())) {
                    for (Menu ancestor : chain) selected.put(ancestor.getId(), ancestor);
                    break;
                }
                current = available.get(current.getParentId());
            }
        }
        allMenus = wrapMeta(new ArrayList<>(selected.values()));
        List<Menu> roots = new ArrayList<>();
        for (Menu menu : allMenus) {
            if (menu.getParentId() != null && menu.getParentId() == 1) {
                roots.add(menu);
            }
        }
        for (Menu root : roots) {
            buildChildren(root, allMenus);
        }
        return roots;
    }

    private static void buildChildren(Menu parent, List<Menu> allMenus) {
        List<Menu> children = new ArrayList<>();
        for (Menu menu : allMenus) {
            if (menu.getParentId() != null && menu.getParentId().equals(parent.getId())) {
                children.add(menu);
                buildChildren(menu, allMenus);
            }
        }
        parent.setChildren(children);
    }

    // 获取所有菜单（不含角色）
    public List<Menu> getAllMenus() throws SQLException {
        return menuDao.getAllMenus();
    }

    // 获取所有菜单（含角色信息）
    public List<Menu> getAllMenusWithRole() throws SQLException {
        return menuDao.getAllMenusWithRole();
    }

    // 根据角色ID获取菜单ID列表
    public List<Integer> getMidsByRid(Integer rid) throws SQLException {
        return menuDao.getMidsByRid(rid);
    }

    // 更新角色菜单
    public boolean updateMenuRole(Integer rid, Integer[] mids) throws SQLException {
        menuDao.updateMenuRole(rid, mids);
        return true;
    }

    private static List<Menu> wrapMeta(List<Menu> menus) {
        for (Menu menu : menus) {
            Meta meta = new Meta();
            meta.setKeepAlive(menu.getKeepAlive());
            meta.setRequireAuth(menu.getRequireAuth());
            menu.setMeta(meta);
        }
        return menus;
    }
}