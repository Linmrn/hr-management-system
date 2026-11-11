package net.xju.service;

import net.xju.common.utils.PageInfo;
import net.xju.dao.EmployeeDao;
import net.xju.dao.EmployeeSalaryDao;
import net.xju.dao.DepartmentDao;
import net.xju.entity.*;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EmployeeService {

    private EmployeeDao employeeDao = new EmployeeDao();
    private NationService nationService = new NationService();
    private PoliticsstatusService politicsstatusService = new PoliticsstatusService();
    private DepartmentService departmentService = new DepartmentService();
    private JobLevelService jobLevelService = new JobLevelService();
    private PositionService positionService = new PositionService();
    private DepartmentDao departmentDao = new DepartmentDao();

    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    private DecimalFormat decimalFormat = new DecimalFormat("##.00");

    public PageInfo<Employee> getEmployeeByPage(int page, int size, Employee condition, Date[] beginDateScope) throws SQLException {
        int offset = (page - 1) * size;
        List<Employee> data = employeeDao.findByPage(offset, size, condition.getName(), condition.getNationId(), condition.getPoliticId(), condition.getDepartmentId(), condition.getJobLevelId(), condition.getPosId(), beginDateScope);
        int total = employeeDao.countAll(condition.getName(), condition.getNationId(), condition.getPoliticId(), condition.getDepartmentId(), condition.getJobLevelId(), condition.getPosId(), beginDateScope);
        return new PageInfo<>(page, size, total, data);
    }

    public Integer addEmp(Employee employee) throws Exception {
        calculateContractTerm(employee);
        return employeeDao.insert(employee);
    }

    public Integer maxWorkID() throws SQLException { return employeeDao.maxWorkID(); }
    public Integer deleteEmpByEid(Integer id) throws SQLException { return employeeDao.deleteById(id); }
    public Integer updateEmp(Employee employee) throws SQLException { return employeeDao.update(employee); }

    public Integer addEmps(List<Employee> list) throws SQLException {
        int count = 0;
        for (Employee emp : list) { employeeDao.insert(emp); count++; }
        return count;
    }

    public PageInfo<Employee> getEmployeeByPageWithSalary(int page, int size,String keyword) throws SQLException {
        int offset = (page - 1) * size;
        List<Employee> data = employeeDao.findByPageWithSalary(offset, size, keyword);
        enrichEmployees(data);
        for (Employee employee : data) employee.setSalary(getSalaryByEmployeeId(employee.getId()));
        int total = employeeDao.countEmployees();
        return new PageInfo<>(page, size, total, data);
    }

    public Integer updateEmployeeSalaryById(Integer eid, Integer sid) throws SQLException { return employeeDao.updateEmployeeSalary(eid, sid); }

    public Employee getEmployeeById(Integer empId) throws SQLException {
        Employee employee = employeeDao.getById(empId);
        if (employee != null) enrichEmployees(java.util.Collections.singletonList(employee));
        return employee;
    }

    private void enrichEmployees(List<Employee> employees) throws SQLException {
        if (employees == null || employees.isEmpty()) return;
        Map<Integer, Nation> nations = nationService.getAllNations().stream().collect(Collectors.toMap(Nation::getId, Function.identity()));
        Map<Integer, Politicsstatus> politics = politicsstatusService.getAllPoliticsstatus().stream().collect(Collectors.toMap(Politicsstatus::getId, Function.identity()));
        Map<Integer, Department> departments = departmentDao.findAll().stream().collect(Collectors.toMap(Department::getId, Function.identity()));
        Map<Integer, JobLevel> jobLevels = jobLevelService.getAllJobLevels().stream().collect(Collectors.toMap(JobLevel::getId, Function.identity()));
        Map<Integer, Position> positions = positionService.getAllPositions().stream().collect(Collectors.toMap(Position::getId, Function.identity()));
        for (Employee employee : employees) {
            employee.setNation(nations.get(employee.getNationId()));
            employee.setPoliticsstatus(politics.get(employee.getPoliticId()));
            employee.setDepartment(departments.get(employee.getDepartmentId()));
            employee.setJobLevel(jobLevels.get(employee.getJobLevelId()));
            employee.setPosition(positions.get(employee.getPosId()));
        }
    }

    private List<Department> flattenDepartments(List<Department> departments) {
        List<Department> result = new java.util.ArrayList<>();
        for (Department department : departments) {
            result.add(department);
            if (department.getChildren() != null) result.addAll(flattenDepartments(department.getChildren()));
        }
        return result;
    }

    private void calculateContractTerm(Employee employee) throws Exception {
        Date beginContract = employee.getBeginContract();
        Date endContract = employee.getEndContract();
        if (beginContract != null && endContract != null) {
            double yearDiff = Double.parseDouble(yearFormat.format(endContract)) - Double.parseDouble(yearFormat.format(beginContract));
            double monthDiff = Double.parseDouble(monthFormat.format(endContract)) - Double.parseDouble(monthFormat.format(beginContract));
            employee.setContractTerm(Double.parseDouble(decimalFormat.format((yearDiff * 12 + monthDiff) / 12)));
        }
    }

    public Map<String, Object> getFullEmployeeDetail(int id) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        result.put("employee", getEmployeeById(id));
        return result;
    }

    public Salary getSalaryByEmployeeId(Integer eid) throws SQLException { return new EmployeeSalaryDao().findSalaryByEid(eid); }

    public Employee getEmployeeWithSalaryById(Integer eid) throws SQLException {
        Employee emp = getEmployeeById(eid);
        if (emp != null) emp.setSalary(getSalaryByEmployeeId(eid));
        return emp;
    }
}
