package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Menu;
import net.xju.entity.Role;
import net.xju.service.MenuService;
import net.xju.service.OpLogService;
import net.xju.service.RoleService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

public class PermissController {

    private final RoleService roleService = new RoleService();
    private final MenuService menuService = new MenuService();
    private final OpLogService opLogService = new OpLogService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/system/basic/permiss/roles.do")
    public String getAllRoles(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Role> list = roleService.getAllRoles();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/system/basic/permiss/menus.do")
    public String getAllMenus(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Menu> list = menuService.getAllMenus();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/system/basic/permiss/mids.do")
    public String getMidsByRid(HttpServletRequest request, HttpServletResponse response) {
        try {
            int rid = Integer.parseInt(request.getParameter("rid"));
            List<Integer> mids = menuService.getMidsByRid(rid);
            return "json:" + JSONUtil.toJson(Result.success(mids));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/permiss/updateMenuRole.do", methods={"POST", "PUT", "DELETE"})
    public String updateMenuRole(HttpServletRequest request, HttpServletResponse response) {
        try {
            int rid = Integer.parseInt(request.getParameter("rid"));
            String[] midStrs = request.getParameterValues("mids");
            Integer[] mids = null;
            if (midStrs != null) {
                mids = Arrays.stream(midStrs).map(Integer::parseInt).toArray(Integer[]::new);
            }
            if (menuService.updateMenuRole(rid, mids)) {
                opLogService.log("更新角色权限，角色ID：" + rid);
                return "json:" + JSONUtil.toJson(Result.success("更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.error("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/permiss/addRole.do", methods={"POST", "PUT", "DELETE"})
    public String addRole(HttpServletRequest request, HttpServletResponse response) {
        try {
            Role role = objectMapper.readValue(request.getInputStream(), Role.class);
            if (roleService.addRole(role) == 1) {
                opLogService.log("新增角色：" + role.getNameZh());
                return "json:" + JSONUtil.toJson(Result.success("添加成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/permiss/deleteRole.do", methods={"POST", "PUT", "DELETE"})
    public String deleteRoleById(HttpServletRequest request, HttpServletResponse response) {
        try {
            int rid = Integer.parseInt(request.getParameter("rid"));
            if (roleService.deleteRoleById(rid) == 1) {
                opLogService.log("删除角色ID：" + rid);
                return "json:" + JSONUtil.toJson(Result.success("删除成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }
}
