package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.HrUtils;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Hr;
import net.xju.entity.Role;
import net.xju.service.HrService;
import net.xju.service.OpLogService;
import net.xju.service.RoleService;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class HrController {

    private final HrService hrService = new HrService();
    private final RoleService roleService = new RoleService();
    private final OpLogService opLogService = new OpLogService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/hr/home.do")
    public String home() {
        return "s:index";
    }

    @RequestMapping("/login.do")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        return "json:" + JSONUtil.toJson(Result.error("尚未登录，请登录！", 401));
    }

    @RequestMapping(value="/doLogin.do", methods={"POST", "PUT", "DELETE"})
    public String doLogin(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return "json:" + JSONUtil.toJson(Result.fail("用户名和密码不能为空"));
        }

        try {
            Hr hr = hrService.loadUserByUsername(username.trim());
            if (hr == null) {
                return "json:" + JSONUtil.toJson(Result.fail("用户名或密码错误"));
            }
            if (hr.getEnabled() == null || !hr.getEnabled()) {
                return "json:" + JSONUtil.toJson(Result.fail("账号已被禁用，请联系管理员"));
            }
            if (!hrService.verifyAndUpgradePassword(hr, password)) {
                return "json:" + JSONUtil.toJson(Result.fail("用户名或密码错误"));
            }

            hr.setPassword(null);
            HttpSession session = request.getSession();
            request.changeSessionId();
            session.setAttribute("loginUser", hr);
            HrUtils.setCurrentHr(hr);
            opLogService.log("管理员登录：" + hr.getUsername());
            return "json:" + JSONUtil.toJson(Result.success(hr, "登录成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("登录失败，服务器内部错误"));
        }
    }

    @RequestMapping(value="/doRegister.do", methods={"POST", "PUT", "DELETE"})
    public String doRegister(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return "json:" + JSONUtil.toJson(Result.fail("用户名和密码不能为空"));
        }

        try {
            Hr existHr = hrService.loadUserByUsername(username.trim());
            if (existHr != null) {
                return "json:" + JSONUtil.toJson(Result.fail("用户名已存在，请更换"));
            }

            Hr newHr = new Hr();
            newHr.setUsername(username.trim());
            newHr.setPassword(password);
            newHr.setEnabled(false);
            newHr.setName(username.trim());

            if (hrService.register(newHr)) {
                opLogService.log("管理员注册：" + username.trim());
                return "json:" + JSONUtil.toJson(Result.success(null, "申请已提交，请联系管理员启用账号并分配角色"));
            } else {
                return "json:" + JSONUtil.toJson(Result.fail("注册失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("注册失败，服务器内部错误"));
        }
    }

    @RequestMapping("/system/hr/list.do")
    public String getAllHrs(HttpServletRequest request, HttpServletResponse response) {
        try {
            String keywords = request.getParameter("keywords");
            List<Hr> list = hrService.getAllHrs(keywords);
            for (Hr hr : list) {
                hr.setPassword(null);
            }
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/hr/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateHr(HttpServletRequest request, HttpServletResponse response) {
        try {
            Hr hr = objectMapper.readValue(request.getInputStream(), Hr.class);
            if (hrService.updateHr(hr) == 1) {
                opLogService.log("更新操作员：" + hr.getName());
                return "json:" + JSONUtil.toJson(Result.success(null, "更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/system/hr/roles.do")
    public String getAllRoles(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Role> list = roleService.getAllRoles();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/hr/role.do", methods={"POST", "PUT", "DELETE"})
    public String updateHrRole(HttpServletRequest request, HttpServletResponse response) {
        try {
            int hrid = Integer.parseInt(request.getParameter("hrid"));
            String[] ridStr = request.getParameterValues("rids");
            int[] rids = null;
            if (ridStr != null && ridStr.length > 0) {
                rids = new int[ridStr.length];
                for (int i = 0; i < ridStr.length; i++) {
                    rids[i] = Integer.parseInt(ridStr[i]);
                }
            }
            if (hrService.updateHrRole(hrid, rids)) {
                opLogService.log("更新操作员角色，操作员ID：" + hrid);
                return "json:" + JSONUtil.toJson(Result.success(null, "更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/hr/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteHrById(HttpServletRequest request, HttpServletResponse response) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (hrService.deleteHrById(id) == 1) {
                opLogService.log("删除操作员ID：" + id);
                return "json:" + JSONUtil.toJson(Result.success(null, "删除成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }
}
