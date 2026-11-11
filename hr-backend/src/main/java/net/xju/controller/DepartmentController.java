package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Department;
import net.xju.service.DepartmentService;
import net.xju.service.OpLogService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class DepartmentController {

    private final DepartmentService departmentService = new DepartmentService();
    private final OpLogService opLogService = new OpLogService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/system/basic/department/home.do")
    public String home() {
        return "s:index";
    }

    @RequestMapping("/system/basic/department/list.do")
    public String getAllDepartments() {
        try {
            List<Department> list = departmentService.getAllDepartments();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/department/add.do", methods={"POST", "PUT", "DELETE"})
    public String addDep(HttpServletRequest request) {
        try {
            Department dep = objectMapper.readValue(request.getInputStream(), Department.class);
            departmentService.addDep(dep);
            if (dep.getResult() != null && dep.getResult() == 1) {
                opLogService.log("新增部门：" + dep.getName());
                return "json:" + JSONUtil.toJson(Result.success(dep, "添加成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/department/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateDep(HttpServletRequest request) {
        try {
            Department dep = objectMapper.readValue(request.getInputStream(), Department.class);
            departmentService.updateDep(dep);
            if (dep.getResult() != null && dep.getResult() == 1) {
                opLogService.log("更新部门：" + dep.getName());
                return "json:" + JSONUtil.toJson(Result.success(null, "更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/department/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteDepById(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Department dep = new Department();
            dep.setId(id);
            departmentService.deleteDepById(dep);
            if (dep.getResult() != null) {
                switch (dep.getResult()) {
                    case -2:
                        return "json:" + JSONUtil.toJson(Result.error("该部门下有子部门，删除失败"));
                    case -1:
                        return "json:" + JSONUtil.toJson(Result.error("该部门下有员工，删除失败"));
                    case 1:
                        opLogService.log("删除部门ID：" + id);
                        return "json:" + JSONUtil.toJson(Result.success(null, "删除成功"));
                }
            }
            return "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }
}
