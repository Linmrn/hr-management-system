package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.Department;
import net.xju.entity.Employee;
import net.xju.entity.JobLevel;
import net.xju.entity.Nation;
import net.xju.entity.Politicsstatus;
import net.xju.entity.Position;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDao {

    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<Employee> findByPage(int offset, int size, Date[] beginDateScope) throws SQLException {
        return findByPage(offset, size, null, null, null, null, null, null, beginDateScope);
    }

    public List<Employee> findByPage(int offset, int size, String name, Integer nationId, Integer politicId, Integer departmentId, Integer jobLevelId, Integer posId, Date[] beginDateScope) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM employee WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (name != null && !name.trim().isEmpty()) { sqlBuilder.append(" AND name LIKE ?"); params.add("%" + name.trim() + "%"); }
        if (nationId != null) { sqlBuilder.append(" AND nationId = ?"); params.add(nationId); }
        if (politicId != null) { sqlBuilder.append(" AND politicId = ?"); params.add(politicId); }
        if (departmentId != null) { sqlBuilder.append(" AND departmentId = ?"); params.add(departmentId); }
        if (jobLevelId != null) { sqlBuilder.append(" AND jobLevelId = ?"); params.add(jobLevelId); }
        if (posId != null) { sqlBuilder.append(" AND posId = ?"); params.add(posId); }
        if (beginDateScope != null && beginDateScope.length == 2) { sqlBuilder.append(" AND beginDate BETWEEN ? AND ?"); params.add(beginDateScope[0]); params.add(beginDateScope[1]); }
        sqlBuilder.append(" LIMIT ?, ?");
        params.add(offset);
        params.add(size);
        List<Employee> employees = runner.query(sqlBuilder.toString(), new BeanListHandler<>(Employee.class), params.toArray());
        hydrateRelations(employees);
        return employees;
    }

    public int countAll() throws SQLException { return countAll(null, null, null, null, null, null, null); }

    public int countAll(String name, Integer nationId, Integer politicId, Integer departmentId, Integer jobLevelId, Integer posId, Date[] beginDateScope) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM employee WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (name != null && !name.trim().isEmpty()) { sql.append(" AND name LIKE ?"); params.add("%" + name.trim() + "%"); }
        if (nationId != null) { sql.append(" AND nationId = ?"); params.add(nationId); }
        if (politicId != null) { sql.append(" AND politicId = ?"); params.add(politicId); }
        if (departmentId != null) { sql.append(" AND departmentId = ?"); params.add(departmentId); }
        if (jobLevelId != null) { sql.append(" AND jobLevelId = ?"); params.add(jobLevelId); }
        if (posId != null) { sql.append(" AND posId = ?"); params.add(posId); }
        if (beginDateScope != null && beginDateScope.length == 2) { sql.append(" AND beginDate BETWEEN ? AND ?"); params.add(beginDateScope[0]); params.add(beginDateScope[1]); }
        Long total = runner.query(sql.toString(), new ScalarHandler<Long>(), params.toArray());
        return total != null ? total.intValue() : 0;
    }

    public int insert(Employee employee) throws SQLException {
        String insertSql = "INSERT INTO employee (name, gender, birthday, idCard, wedlock, nationId, nativePlace, politicId, email, phone, address, departmentId, jobLevelId, posId, engageForm, tiptopDegree, specialty, school, beginDate, workState, workID, contractTerm, conversionTime, notWorkDate, beginContract, endContract, workAge) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        return runner.update(insertSql, employee.getName(), employee.getGender(), employee.getBirthday(), employee.getIdCard(), employee.getWedlock(), employee.getNationId(), employee.getNativePlace(), employee.getPoliticId(), employee.getEmail(), employee.getPhone(), employee.getAddress(), employee.getDepartmentId(), employee.getJobLevelId(), employee.getPosId(), employee.getEngageForm(), employee.getTiptopDegree(), employee.getSpecialty(), employee.getSchool(), employee.getBeginDate(), employee.getWorkState(), employee.getWorkID(), employee.getContractTerm(), employee.getConversionTime(), employee.getNotWorkDate(), employee.getBeginContract(), employee.getEndContract(), employee.getWorkAge());
    }

    public Integer maxWorkID() throws SQLException {
        java.math.BigInteger max = runner.query("SELECT MAX(CAST(workID AS UNSIGNED)) FROM employee", new ScalarHandler<java.math.BigInteger>());
        return max != null ? max.intValue() : 0;
    }

    public int deleteById(Integer id) throws SQLException { return runner.update("UPDATE employee SET workState = '离职' WHERE id = ?", id); }

    public int update(Employee employee) throws SQLException {
        String sql = "UPDATE employee SET name=?, gender=?, birthday=?, idCard=?, wedlock=?, nationId=?, nativePlace=?, politicId=?, email=?, phone=?, address=?, departmentId=?, jobLevelId=?, posId=?, engageForm=?, tiptopDegree=?, specialty=?, school=?, beginDate=?, workState=?, workID=?, contractTerm=?, conversionTime=?, notWorkDate=?, beginContract=?, endContract=?, workAge=? WHERE id=?";
        return runner.update(sql, employee.getName(), employee.getGender(), employee.getBirthday(), employee.getIdCard(), employee.getWedlock(), employee.getNationId(), employee.getNativePlace(), employee.getPoliticId(), employee.getEmail(), employee.getPhone(), employee.getAddress(), employee.getDepartmentId(), employee.getJobLevelId(), employee.getPosId(), employee.getEngageForm(), employee.getTiptopDegree(), employee.getSpecialty(), employee.getSchool(), employee.getBeginDate(), employee.getWorkState(), employee.getWorkID(), employee.getContractTerm(), employee.getConversionTime(), employee.getNotWorkDate(), employee.getBeginContract(), employee.getEndContract(), employee.getWorkAge(), employee.getId());
    }

    public List<Employee> findByPageWithSalary(int offset, int size,String keyword) throws SQLException {
        List<Employee> employees = runner.query("SELECT e.* FROM employee e LEFT JOIN empsalary es ON e.id = es.eid LIMIT ?, ?", new BeanListHandler<>(Employee.class), offset, size);
        hydrateRelations(employees);
        return employees;
    }

    public int countEmployees() throws SQLException {
        Long total = runner.query("SELECT COUNT(*) FROM employee", new ScalarHandler<Long>());
        return total != null ? total.intValue() : 0;
    }

    public int updateEmployeeSalary(Integer eid, Integer sid) throws SQLException {
        return runner.update("INSERT INTO empsalary (eid, sid) VALUES (?, ?) ON DUPLICATE KEY UPDATE sid = ?", eid, sid, sid);
    }

    public Employee getById(Integer empId) throws SQLException {
        Employee employee = runner.query("SELECT * FROM employee WHERE id = ?", new BeanHandler<>(Employee.class), empId);
        hydrateRelations(java.util.Collections.singletonList(employee));
        return employee;
    }

    private void hydrateRelations(List<Employee> employees) throws SQLException {
        if (employees == null || employees.isEmpty()) return;
        List<Map<String, Object>> rows = runner.query(
                "SELECT e.id AS eid, n.id AS nationIdRef, n.name AS nationName, ps.id AS politicIdRef, ps.name AS politicName, d.id AS departmentIdRef, d.name AS departmentName, p.id AS positionIdRef, p.name AS positionName, jl.id AS jobLevelIdRef, jl.name AS jobLevelName " +
                        "FROM employee e " +
                        "LEFT JOIN nation n ON e.nationId = n.id " +
                        "LEFT JOIN politicsstatus ps ON e.politicId = ps.id " +
                        "LEFT JOIN department d ON e.departmentId = d.id " +
                        "LEFT JOIN position p ON e.posId = p.id " +
                        "LEFT JOIN joblevel jl ON e.jobLevelId = jl.id " +
                        "WHERE e.id IN (" + placeholders(employees.size()) + ")",
                new MapListHandler(), employeeIds(employees));
        Map<Integer, Map<String, Object>> relationMap = new HashMap<>();
        for (Map<String, Object> row : rows) relationMap.put(((Number) row.get("eid")).intValue(), row);
        for (Employee employee : employees) {
            Map<String, Object> row = relationMap.get(employee.getId());
            if (row == null) continue;
            if (row.get("nationIdRef") != null) { Nation nation = new Nation(); nation.setId(number(row.get("nationIdRef"))); nation.setName((String) row.get("nationName")); employee.setNation(nation); }
            if (row.get("politicIdRef") != null) { Politicsstatus politics = new Politicsstatus(); politics.setId(number(row.get("politicIdRef"))); politics.setName((String) row.get("politicName")); employee.setPoliticsstatus(politics); }
            if (row.get("departmentIdRef") != null) { Department department = new Department(); department.setId(number(row.get("departmentIdRef"))); department.setName((String) row.get("departmentName")); employee.setDepartment(department); }
            if (row.get("positionIdRef") != null) { Position position = new Position(); position.setId(number(row.get("positionIdRef"))); position.setName((String) row.get("positionName")); employee.setPosition(position); }
            if (row.get("jobLevelIdRef") != null) { JobLevel jobLevel = new JobLevel(); jobLevel.setId(number(row.get("jobLevelIdRef"))); jobLevel.setName((String) row.get("jobLevelName")); employee.setJobLevel(jobLevel); }
        }
    }

    private Object[] employeeIds(List<Employee> employees) {
        Object[] ids = new Object[employees.size()];
        for (int i = 0; i < employees.size(); i++) ids[i] = employees.get(i).getId();
        return ids;
    }

    private String placeholders(int count) { return String.join(",", java.util.Collections.nCopies(count, "?")); }
    private Integer number(Object value) { return ((Number) value).intValue(); }

    public List<Map<String, Object>> countByBirthdayMonthWithYear(Integer year) throws SQLException {
        return runner.query("SELECT MONTH(birthday) as month, COUNT(*) as count FROM employee WHERE YEAR(birthday) = ? GROUP BY MONTH(birthday) ORDER BY month", new MapListHandler(), year);
    }

    public List<Map<String, Object>> countByBirthdayMonthAll() throws SQLException {
        return runner.query("SELECT MONTH(birthday) as month, COUNT(*) as count FROM employee GROUP BY MONTH(birthday) ORDER BY month", new MapListHandler());
    }

    public Map<String, Object> countByConversionStatus() throws SQLException {
        Long converted = runner.query("SELECT COUNT(*) FROM employee WHERE conversionTime IS NOT NULL", new ScalarHandler<Long>());
        Long unconverted = runner.query("SELECT COUNT(*) FROM employee WHERE conversionTime IS NULL", new ScalarHandler<Long>());
        Map<String, Object> result = new HashMap<>();
        result.put("converted", converted == null ? 0 : converted.intValue());
        result.put("unconverted", unconverted == null ? 0 : unconverted.intValue());
        return result;
    }
}
