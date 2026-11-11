package net.xju.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.*;
import net.xju.entity.*;
import net.xju.service.*;
import net.xju.dao.EmployeeDao;
import net.xju.common.utils.JDBCUtils;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


public class EmpBasicController {

    private EmployeeService employeeService = new EmployeeService();
    private NationService nationService = new NationService();
    private PoliticsstatusService politicsstatusService = new PoliticsstatusService();
    private JobLevelService jobLevelService = new JobLevelService();
    private PositionService positionService = new PositionService();
    private DepartmentService departmentService = new DepartmentService();

    private ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // ---------- 分页查询员工 ----------
    @RequestMapping("/employee/basic/list.do")
    public String getEmployeeByPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            Employee condition = new Employee();
            if (request.getParameter("name") != null) condition.setName(request.getParameter("name"));
            if (request.getParameter("nationId") != null) condition.setNationId(parseInt(request.getParameter("nationId"), 0));
            if (request.getParameter("politicId") != null) condition.setPoliticId(parseInt(request.getParameter("politicId"), 0));
            if (request.getParameter("departmentId") != null) condition.setDepartmentId(parseInt(request.getParameter("departmentId"), 0));
            if (request.getParameter("jobLevelId") != null) condition.setJobLevelId(parseInt(request.getParameter("jobLevelId"), 0));
            if (request.getParameter("posId") != null) condition.setPosId(parseInt(request.getParameter("posId"), 0));
            Date[] beginDateScope = null;
            String startDate = request.getParameter("beginDateScope[0]");
            String endDate = request.getParameter("beginDateScope[1]");
            if (startDate != null && endDate != null) {
                beginDateScope = new Date[]{
                        DateUtils.parse(startDate),
                        DateUtils.parse(endDate)
                };
            }
            PageInfo<Employee> pageInfo = employeeService.getEmployeeByPage(page, size, condition, beginDateScope);

            return "json:" + JSONUtil.toJson(Result.success(pageInfo));

        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    // ---------- 添加员工 ----------
    @RequestMapping(value="/employee/basic/add.do", methods={"POST", "PUT", "DELETE"})
    public String addEmp(HttpServletRequest request, HttpServletResponse response) {
        try {
            Employee employee = parseJsonBody(request, Employee.class);
            normalizeWorkId(employee);
            if (employeeService.addEmp(employee) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("添加成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    // ---------- 删除员工 ----------
    @RequestMapping(value="/employee/basic/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteEmpByEid(HttpServletRequest request, HttpServletResponse response) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (employeeService.deleteEmpByEid(id) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("删除成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    // ---------- 更新员工 ----------
    @RequestMapping(value="/employee/basic/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateEmp(HttpServletRequest request, HttpServletResponse response) {
        try {
            Employee employee = parseJsonBody(request, Employee.class);
            normalizeWorkId(employee);
            if (employeeService.updateEmp(employee) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    // ---------- 获取所有民族 ----------
    @RequestMapping("/employee/basic/nations.do")
    public String getAllNations(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Nation> list = nationService.getAllNations();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败"));
        }
    }

    // ---------- 获取所有政治面貌 ----------
    @RequestMapping("/employee/basic/politicsstatus.do")
    public String getAllPoliticsstatus(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Politicsstatus> list = politicsstatusService.getAllPoliticsstatus();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败"));
        }
    }

    // ---------- 获取所有职称 ----------
    @RequestMapping("/employee/basic/joblevels.do")
    public String getAllJobLevels(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<JobLevel> list = jobLevelService.getAllJobLevels();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败"));
        }
    }

    // ---------- 获取所有职位 ----------
    @RequestMapping("/employee/basic/positions.do")
    public String getAllPositions(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Position> list = positionService.getAllPositions();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败"));
        }
    }

    // ---------- 获取最大工号 ----------
    @RequestMapping("/employee/basic/maxWorkID.do")
    public String maxWorkID(HttpServletRequest request, HttpServletResponse response) {
        try {
            int maxId = employeeService.maxWorkID() + 1;
            String formatted = String.format("%08d", maxId);
            return "json:" + JSONUtil.toJson(Result.success(formatted));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败"));
        }
    }

    // ---------- 获取所有部门 ----------
    @RequestMapping("/employee/basic/deps.do")
    public String getAllDepartments(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Department> list = departmentService.getAllDepartmentsFlat();  // 新的，查所有部门
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败"));
        }
    }

    // ---------- 导出员工 Excel ----------
    @RequestMapping("/employee/basic/export.do")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Employee> list = (List<Employee>) employeeService.getEmployeeByPage(1, Integer.MAX_VALUE, new Employee(), null).getRecords();
            POIUtils.employee2Excel(list, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- 导入员工 Excel ----------
    @RequestMapping(value="/employee/basic/import.do", methods={"POST", "PUT", "DELETE"})
    public String importData(HttpServletRequest request, HttpServletResponse response) {
        try {
            Part filePart = request.getPart("file");
            List<Nation> nations = nationService.getAllNations();
            List<Politicsstatus> politicsstatuses = politicsstatusService.getAllPoliticsstatus();
            List<Department> departments = departmentService.getAllDepartmentsWithOutChildren();
            List<Position> positions = positionService.getAllPositions();
            List<JobLevel> jobLevels = jobLevelService.getAllJobLevels();
            List<Employee> employees = POIUtils.excel2Employee(filePart.getInputStream(), nations, politicsstatuses, departments, positions, jobLevels);
            if (employeeService.addEmps(employees) == employees.size()) {
                return "json:" + JSONUtil.toJson(Result.success("上传成功"));
            }
            return "json:" + JSONUtil.toJson(Result.error("上传失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("上传失败: " + e.getMessage()));
        }
    }

    // ---------- 工具方法 ----------
    private <T> T parseJsonBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        return objectMapper.readValue(request.getInputStream(), clazz);
    }

    private void normalizeWorkId(Employee employee) throws SQLException {
        if (employee == null) return;
        String workID = employee.getWorkID();
        if (workID == null || workID.trim().isEmpty() || "[object Object]".equals(workID.trim())) {
            Integer max = employeeService.maxWorkID();
            employee.setWorkID(String.valueOf((max == null ? 0 : max) + 1));
        } else {
            employee.setWorkID(workID.trim());
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
    // ==================== 工资信息接口 ====================

    @RequestMapping("/employee/salary/info.do")
    public String getSalaryInfo(HttpServletRequest request, HttpServletResponse response) {
        try {
            String eidParam = request.getParameter("eid");
            if (eidParam == null || eidParam.isEmpty()) {
                return "json:" + JSONUtil.toJson(Result.error("缺少员工ID"));
            }
            int eid = Integer.parseInt(eidParam);
            Salary salary = employeeService.getSalaryByEmployeeId(eid);
            return "json:" + JSONUtil.toJson(Result.success(salary));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }
    // ---------- 统计接口 ----------
    @RequestMapping("/statistics/birthday.do")
    public String getBirthdayStats(HttpServletRequest request, HttpServletResponse response) {
        try {
            String yearParam = request.getParameter("year");
            EmployeeDao dao = new EmployeeDao();
            List<Map<String, Object>> data;
            if (yearParam != null && !yearParam.isEmpty()) {
                Integer year = Integer.parseInt(yearParam);
                data = dao.countByBirthdayMonthWithYear(year);
            } else {
                data = dao.countByBirthdayMonthAll();
            }
            return "json:" + JSONUtil.toJson(Result.success(data));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping("/statistics/conversion.do")
    public String getConversionStats(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 直接使用 QueryRunner 查询数据库
            QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

            String sqlConverted = "SELECT COUNT(*) FROM employee WHERE conversionTime IS NOT NULL";
            Long converted = runner.query(sqlConverted, new ScalarHandler<Long>());

            String sqlUnconverted = "SELECT COUNT(*) FROM employee WHERE conversionTime IS NULL";
            Long unconverted = runner.query(sqlUnconverted, new ScalarHandler<Long>());

            Map<String, Object> data = new HashMap<>();
            data.put("converted", converted == null ? 0 : converted.intValue());
            data.put("unconverted", unconverted == null ? 0 : unconverted.intValue());

            return "json:" + JSONUtil.toJson(Result.success(data));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }
    @RequestMapping("/statistics/score.do")
    public String getScoreStats(HttpServletRequest request, HttpServletResponse response) {
        try {
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
                return "json:" + JSONUtil.toJson(Result.error("请选择日期范围"));
            }

            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            int offset = (page - 1) * size;

            java.sql.Date start = java.sql.Date.valueOf(startDate);
            java.sql.Date end = java.sql.Date.valueOf(endDate);

            QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

            String countSql = "SELECT COUNT(*) FROM (" +
                    "SELECT e.id " +
                    "FROM employee e " +
                    "JOIN employeeec ec ON e.id = ec.eid " +
                    "WHERE ec.ecDate BETWEEN ? AND ? " +
                    "GROUP BY e.id" +
                    ") t";
            Long total = runner.query(countSql, new ScalarHandler<Long>(), start, end);

            String dataSql = "SELECT e.id, e.name, " +
                    "COALESCE(SUM(CASE WHEN ec.ecType = 0 THEN ec.ecPoint ELSE -ec.ecPoint END), 0) AS totalScore, " +
                    "COALESCE(SUM(CASE WHEN ec.ecType = 0 THEN ec.ecPoint ELSE 0 END), 0) AS rewardScore, " +
                    "COALESCE(SUM(CASE WHEN ec.ecType = 1 THEN ec.ecPoint ELSE 0 END), 0) AS punishScore " +
                    "FROM employee e " +
                    "JOIN employeeec ec ON e.id = ec.eid " +
                    "WHERE ec.ecDate BETWEEN ? AND ? " +
                    "GROUP BY e.id, e.name " +
                    "ORDER BY totalScore DESC " +
                    "LIMIT ?, ?";
            List<Map<String, Object>> records = runner.query(dataSql, new MapListHandler(), start, end, offset, size);

            Map<String, Object> result = new HashMap<>();
            result.put("total", total == null ? 0 : total.intValue());
            result.put("records", records);
            result.put("page", page);
            result.put("size", size);

            return "json:" + JSONUtil.toJson(Result.success(result));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }
    @RequestMapping("/statistics/personnel.do")
    public String getPersonnelStats(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 获取筛选参数
            String gender = request.getParameter("gender");
            String deptId = request.getParameter("deptId");
            String jobLevelId = request.getParameter("jobLevelId");
            String salaryId = request.getParameter("salaryId");
            String pageStr = request.getParameter("page");
            String sizeStr = request.getParameter("size");

            int page = parseInt(pageStr, 1);
            int size = parseInt(sizeStr, 10);
            int offset = (page - 1) * size;

            // 构建动态 SQL（去掉 salary 关联，避免因无工资账套而查不到人）
            StringBuilder sql = new StringBuilder(
                    "SELECT e.id, e.name, e.gender, e.phone, e.workID, " +
                            "d.name as deptName, j.name as jobLevelName, p.name as posName " +
                            "FROM employee e " +
                            "LEFT JOIN department d ON e.departmentId = d.id " +
                            "LEFT JOIN joblevel j ON e.jobLevelId = j.id " +
                            "LEFT JOIN position p ON e.posId = p.id " +
                            "WHERE 1=1 "
            );

            List<Object> params = new ArrayList<>();

            if (gender != null && !gender.isEmpty()) {
                sql.append(" AND e.gender = ? ");
                params.add(gender);
            }
            if (deptId != null && !deptId.isEmpty()) {
                sql.append(" AND e.departmentId = ? ");
                params.add(Integer.parseInt(deptId));
            }
            if (jobLevelId != null && !jobLevelId.isEmpty()) {
                sql.append(" AND e.jobLevelId = ? ");
                params.add(Integer.parseInt(jobLevelId));
            }
            // 注意：salaryId 过滤需要关联 empsalary 表，如果需要可以加上，但现在先去掉以保证所有员工都能显示
            // 所以 salaryId 参数暂时忽略（或者你可以选择加上，但那样可能查不到数据）

            // 分页查询
            String countSql = "SELECT COUNT(*) FROM (" + sql.toString() + ") t";
            String dataSql = sql.toString() + " LIMIT ?, ?";
            params.add(offset);
            params.add(size);

            QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

            // 查询总数（去掉 LIMIT 参数）
            List<Object> countParams = new ArrayList<>(params);
            countParams.remove(countParams.size() - 1); // 去掉 size
            countParams.remove(countParams.size() - 1); // 去掉 offset
            Long total = runner.query(countSql, new ScalarHandler<Long>(), countParams.toArray());

            // 查询数据
            List<Map<String, Object>> data = runner.query(dataSql, new MapListHandler(), params.toArray());

            Map<String, Object> result = new HashMap<>();
            result.put("total", total == null ? 0 : total.intValue());
            result.put("data", data);
            result.put("page", page);
            result.put("size", size);

            return "json:" + JSONUtil.toJson(Result.success(result));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }
    @RequestMapping("/statistics/record.do")
    public String getRecordStats(HttpServletRequest request, HttpServletResponse response) {
        try {
            String deptId = request.getParameter("deptId");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            int offset = (page - 1) * size;

            if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
                return "json:" + JSONUtil.toJson(Result.error("请选择日期范围"));
            }

            QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

            StringBuilder baseSql = new StringBuilder();
            baseSql.append("FROM employee e ")
                  .append("LEFT JOIN department d ON e.departmentId = d.id ")
                  .append("LEFT JOIN position p ON e.posId = p.id ")
                  .append("INNER JOIN employeeec ec ON e.id = ec.eid AND ec.ecDate BETWEEN ? AND ? ");

            List<Object> params = new java.util.ArrayList<>();
            params.add(startDate);
            params.add(endDate);

            if (deptId != null && !deptId.isEmpty()) {
                baseSql.append("WHERE e.departmentId = ? ");
                params.add(Integer.parseInt(deptId));
            }

            String selectSql = "SELECT e.id, e.name, d.name AS departmentName, p.name AS positionName, " +
                    "COALESCE(SUM(CASE WHEN ec.ecType = 0 THEN ec.ecPoint ELSE 0 END), 0) as rewardScore, " +
                    "COALESCE(SUM(CASE WHEN ec.ecType = 1 THEN ec.ecPoint ELSE 0 END), 0) as punishScore, " +
                    "COALESCE(SUM(CASE WHEN ec.ecType = 0 THEN ec.ecPoint ELSE -ec.ecPoint END), 0) as totalScore, " +
                    "COUNT(ec.id) as ecCount " +
                    baseSql.toString() +
                    "GROUP BY e.id, e.name, d.name, p.name ORDER BY totalScore DESC LIMIT ?, ?";

            String countSql = "SELECT COUNT(*) AS total FROM (SELECT e.id " + baseSql.toString() + "GROUP BY e.id) t";

            List<Object> queryParams = new java.util.ArrayList<>(params);
            queryParams.add(offset);
            queryParams.add(size);

            Long total = runner.query(countSql, new ScalarHandler<Long>(), params.toArray());
            List<Map<String, Object>> data = runner.query(selectSql, new MapListHandler(), queryParams.toArray());

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("total", total == null ? 0 : total.intValue());
            result.put("data", data);
            result.put("page", page);
            result.put("size", size);

            return "json:" + JSONUtil.toJson(Result.success(result));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }
}