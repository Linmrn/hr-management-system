package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JDBCUtils;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Salary;
import net.xju.service.SalaryService;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

public class SalaryController {

    private SalaryService salaryService = new SalaryService();
    private ObjectMapper objectMapper = new ObjectMapper();

    // ---------- 1. 查询所有账套 ----------
    @RequestMapping("/salary/sob/list.do")
    public String getAllSalaries(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Salary> list = salaryService.getAllSalaries();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    // ---------- 2. 添加工资账套 ----------
    @RequestMapping(value="/salary/sob/add.do", methods={"POST", "PUT", "DELETE"})
    public String addSalary(HttpServletRequest request, HttpServletResponse response) {
        try {
            Salary salary = parseJsonBody(request, Salary.class);
            if (salaryService.addSalary(salary) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("添加成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    // ---------- 3. 修改工资账套 ----------
    @RequestMapping(value="/salary/sob/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateSalaryById(HttpServletRequest request, HttpServletResponse response) {
        try {
            Salary salary = parseJsonBody(request, Salary.class);
            if (salary.getId() == null) {
                return "json:" + JSONUtil.toJson(Result.fail("缺少账套ID"));
            }
            if (salaryService.updateSalaryById(salary) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    // ---------- 4. 删除工资账套（带外键检查） ----------
    @RequestMapping(value="/salary/sob/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteSalaryById(HttpServletRequest request, HttpServletResponse response) {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                return "json:" + JSONUtil.toJson(Result.fail("请提供要删除的账套ID"));
            }

            int id = Integer.parseInt(idStr.trim());

            // ✅ 检查该账套是否正在被员工使用（外键约束）
            QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());
            String checkSql = "SELECT COUNT(*) FROM empsalary WHERE sid = ?";
            Long count = runner.query(checkSql, new ScalarHandler<Long>(), id);
            if (count != null && count > 0) {
                return "json:" + JSONUtil.toJson(Result.fail("该账套正在被员工使用，不能删除", 400));
            }

            int result = salaryService.deleteSalaryById(id);
            if (result == 1) {
                return "json:" + JSONUtil.toJson(Result.success("删除成功"));
            } else {
                return "json:" + JSONUtil.toJson(Result.fail("删除失败，记录不存在"));
            }
        } catch (NumberFormatException e) {
            return "json:" + JSONUtil.toJson(Result.error("账套ID格式错误", 400));
        } catch (SQLException e) {
            e.printStackTrace();
            String msg = e.getMessage();
            // 捕获外键约束异常（备用）
            if (msg != null && (msg.contains("foreign key") || msg.contains("integrity constraint"))) {
                msg = "该账套正在被员工使用，不能删除";
            }
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + msg, 500));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage(), 500));
        }
    }

    // ---------- 工具方法：解析 JSON 请求体 ----------
    private <T> T parseJsonBody(HttpServletRequest request, Class<T> clazz) throws Exception {
        return objectMapper.readValue(request.getInputStream(), clazz);
    }
}