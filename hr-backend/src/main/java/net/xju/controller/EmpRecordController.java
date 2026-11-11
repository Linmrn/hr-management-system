package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.PageInfo;
import net.xju.common.utils.Result;
import net.xju.entity.*;
import net.xju.service.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class EmpRecordController {

    private final EmployeeecService employeeecService = new EmployeeecService();
    private final EmployeetrainService employeetrainService = new EmployeetrainService();
    private final AppraiseService appraiseService = new AppraiseService();
    private final AdjustSalaryService adjustSalaryService = new AdjustSalaryService();
    private final EmployeeremoveService employeeremoveService = new EmployeeremoveService();
    private final EmployeeService employeeService = new EmployeeService();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @RequestMapping("/employee/record/home.do")
    public String home() {
        return "s:index";
    }

    @RequestMapping("/employee/ec/list.do")
    public String getAllEmployeeec() {
        try {
            return "json:" + JSONUtil.toJson(Result.success(employeeecService.getAllEmployeeec()));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/ec/page.do")
    public String getEmployeeecByPage(HttpServletRequest request) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            Integer eid = parseNullableInt(request.getParameter("eid"));
            List<Employeeec> list = employeeecService.getEmployeeecByPage(page, size, eid);
            int total = employeeecService.count(eid);
            return "json:" + JSONUtil.toJson(Result.success(new PageInfo<>(page, size, total, list)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/ec/emp.do")
    public String getEmployeeecById(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return "json:" + JSONUtil.toJson(Result.success(employeeecService.getEmployeeecById(id)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/ec/add.do", methods={"POST", "PUT", "DELETE"})
    public String addEmployeeec(HttpServletRequest request) {
        try {
            Employeeec ec = objectMapper.readValue(request.getInputStream(), Employeeec.class);
            return employeeecService.addEmployeeec(ec) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "添加成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/ec/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateEmployeeec(HttpServletRequest request) {
        try {
            Employeeec ec = objectMapper.readValue(request.getInputStream(), Employeeec.class);
            return employeeecService.updateEmployeeec(ec) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "更新成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/ec/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteEmployeeec(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return employeeecService.deleteEmployeeecById(id) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "删除成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/train/list.do")
    public String getAllEmployeetrain() {
        try {
            return "json:" + JSONUtil.toJson(Result.success(employeetrainService.getAllEmployeetrain()));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/train/page.do")
    public String getEmployeetrainByPage(HttpServletRequest request) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            Integer eid = parseNullableInt(request.getParameter("eid"));
            List<Employeetrain> list = employeetrainService.getEmployeetrainByPage(page, size, eid);
            int total = employeetrainService.count(eid);
            return "json:" + JSONUtil.toJson(Result.success(new PageInfo<>(page, size, total, list)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/train/emp.do")
    public String getEmployeetrainById(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return "json:" + JSONUtil.toJson(Result.success(employeetrainService.getEmployeetrainById(id)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/train/add.do", methods={"POST", "PUT", "DELETE"})
    public String addEmployeetrain(HttpServletRequest request) {
        try {
            Employeetrain train = objectMapper.readValue(request.getInputStream(), Employeetrain.class);
            return employeetrainService.addEmployeetrain(train) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "添加成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/train/form.do")
    public String getEmployeetrainForm(HttpServletRequest request) {
        try {
            Integer id = parseNullableInt(request.getParameter("id"));
            Employeetrain train = id == null ? null : employeetrainService.getEmployeetrainById(id);
            return "json:" + JSONUtil.toJson(Result.success(train));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/train/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateEmployeetrain(HttpServletRequest request) {
        try {
            Employeetrain train = objectMapper.readValue(request.getInputStream(), Employeetrain.class);
            return employeetrainService.updateEmployeetrain(train) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "更新成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/train/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteEmployeetrain(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return employeetrainService.deleteEmployeetrainById(id) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "删除成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/appraise/list.do")
    public String getAllAppraise() {
        try {
            return "json:" + JSONUtil.toJson(Result.success(appraiseService.getAllAppraise()));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/appraise/page.do")
    public String getAppraiseByPage(HttpServletRequest request) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            Integer eid = parseNullableInt(request.getParameter("eid"));
            List<Appraise> list = appraiseService.getAppraiseByPage(page, size, eid);
            int total = appraiseService.count(eid);
            return "json:" + JSONUtil.toJson(Result.success(new PageInfo<>(page, size, total, list)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/appraise/emp.do")
    public String getAppraiseById(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return "json:" + JSONUtil.toJson(Result.success(appraiseService.getAppraiseById(id)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/appraise/add.do", methods={"POST", "PUT", "DELETE"})
    public String addAppraise(HttpServletRequest request) {
        try {
            Appraise appraise = objectMapper.readValue(request.getInputStream(), Appraise.class);
            return appraiseService.addAppraise(appraise) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "添加成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/appraise/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateAppraise(HttpServletRequest request) {
        try {
            Appraise appraise = objectMapper.readValue(request.getInputStream(), Appraise.class);
            return appraiseService.updateAppraise(appraise) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "更新成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/appraise/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteAppraise(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return appraiseService.deleteAppraiseById(id) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "删除成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/adjustsalary/list.do")
    public String getAllAdjustSalary() {
        try {
            return "json:" + JSONUtil.toJson(Result.success(adjustSalaryService.getAllAdjustSalary()));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/adjustsalary/page.do")
    public String getAdjustSalaryByPage(HttpServletRequest request) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            Integer eid = parseNullableInt(request.getParameter("eid"));
            List<AdjustSalary> list = adjustSalaryService.getAdjustSalaryByPage(page, size, eid);
            int total = adjustSalaryService.count(eid);
            return "json:" + JSONUtil.toJson(Result.success(new PageInfo<>(page, size, total, list)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/adjustsalary/emp.do")
    public String getAdjustSalaryById(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return "json:" + JSONUtil.toJson(Result.success(adjustSalaryService.getAdjustSalaryById(id)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/adjustsalary/add.do", methods={"POST", "PUT", "DELETE"})
    public String addAdjustSalary(HttpServletRequest request) {
        try {
            AdjustSalary adjustSalary = objectMapper.readValue(request.getInputStream(), AdjustSalary.class);
            return adjustSalaryService.addAdjustSalary(adjustSalary) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "添加成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/adjustsalary/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateAdjustSalary(HttpServletRequest request) {
        try {
            AdjustSalary adjustSalary = objectMapper.readValue(request.getInputStream(), AdjustSalary.class);
            return adjustSalaryService.updateAdjustSalary(adjustSalary) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "更新成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/adjustsalary/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteAdjustSalary(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return adjustSalaryService.deleteAdjustSalaryById(id) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "删除成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/remove/list.do")
    public String getAllEmployeeremove() {
        try {
            return "json:" + JSONUtil.toJson(Result.success(employeeremoveService.getAllEmployeeremove()));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/remove/page.do")
    public String getEmployeeremoveByPage(HttpServletRequest request) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            Integer eid = parseNullableInt(request.getParameter("eid"));
            List<Employeeremove> list = employeeremoveService.getEmployeeremoveByPage(page, size, eid);
            int total = employeeremoveService.count(eid);
            return "json:" + JSONUtil.toJson(Result.success(new PageInfo<>(page, size, total, list)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/remove/emp.do")
    public String getEmployeeremoveById(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return "json:" + JSONUtil.toJson(Result.success(employeeremoveService.getEmployeeremoveById(id)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/remove/add.do", methods={"POST", "PUT", "DELETE"})
    public String addEmployeeremove(HttpServletRequest request) {
        try {
            Employeeremove remove = objectMapper.readValue(request.getInputStream(), Employeeremove.class);
            return employeeremoveService.addEmployeeremove(remove) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "添加成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/remove/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateEmployeeremove(HttpServletRequest request) {
        try {
            Employeeremove remove = objectMapper.readValue(request.getInputStream(), Employeeremove.class);
            return employeeremoveService.updateEmployeeremove(remove) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "更新成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/employee/remove/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteEmployeeremove(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            return employeeremoveService.deleteEmployeeremoveById(id) == 1
                    ? "json:" + JSONUtil.toJson(Result.success(null, "删除成功"))
                    : "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/employee/record/emp.do")
    public String getEmployeeById(HttpServletRequest request) {
        try {
            Integer id = Integer.parseInt(request.getParameter("id"));
            Employee employee = employeeService.getEmployeeById(id);
            return "json:" + JSONUtil.toJson(Result.success(employee));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
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

    private Integer parseNullableInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
