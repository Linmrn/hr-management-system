package net.xju.controller;

import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.PageInfo;
import net.xju.common.utils.Result;
import net.xju.entity.Salary;
import net.xju.service.EmployeeService;
import net.xju.service.SalaryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class SobConfigController {

    private EmployeeService employeeService = new EmployeeService();
    private SalaryService salaryService = new SalaryService();

    // 分页查询员工工资信息
    @RequestMapping("/salary/sobcfg/list.do")
    public String getEmployeeByPageWithSalary(HttpServletRequest request, HttpServletResponse response) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            String keyword = request.getParameter("keyword");
            System.out.println("=== 员工账套列表请求: page=" + page + ", size=" + size);
            PageInfo<?> pageInfo = employeeService.getEmployeeByPageWithSalary(page, size, keyword);
            return "json:" + JSONUtil.toJson(Result.success(pageInfo));
        } catch (Exception e) {
            e.printStackTrace(); // 控制台会打印完整堆栈
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }
//获取所有工资账套
    @RequestMapping("/salary/sobcfg/salaries.do")
    public String getAllSalaries(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("=== 获取所有工资账套");
            List<Salary> list = salaryService.getAllSalaries();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("加载账套失败: " + e.getMessage()));
        }
    }

    // 更新员工工资账套关联
    @RequestMapping(value="/salary/sobcfg/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateEmployeeSalaryById(HttpServletRequest request,HttpServletResponse response) {
        try {
            int eid = Integer.parseInt(request.getParameter("eid"));
            int sid = Integer.parseInt(request.getParameter("sid"));
            Integer result = employeeService.updateEmployeeSalaryById(eid, sid);
            if (result != null && result >= 1) {
                return "json:" + JSONUtil.toJson(Result.success("更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}