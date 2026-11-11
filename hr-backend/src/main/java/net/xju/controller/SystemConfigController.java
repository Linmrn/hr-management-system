package net.xju.controller;

import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.HrUtils;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Hr;
import net.xju.entity.Menu;
import net.xju.service.MenuService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SystemConfigController {

    private MenuService menuService = new MenuService();

    // 获取当前登录用户的菜单树（首页动态菜单）
    @RequestMapping("/system/config/menu.do")
    public String getMenuTree(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 从 Session 获取登录用户
            Hr currentHr = (Hr) request.getSession().getAttribute("loginUser");
            if (currentHr == null) {
                return "json:" + JSONUtil.toJson(Result.error("用户未登录", 401));
            }
            List<Menu> menuTree = menuService.getMenuTreeByHrId(currentHr.getId());
            return "json:" + JSONUtil.toJson(Result.success(menuTree));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("获取菜单失败: " + e.getMessage()));
        }
    }
}
