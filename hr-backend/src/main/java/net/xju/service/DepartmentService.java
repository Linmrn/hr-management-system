package net.xju.service;

import net.xju.dao.DepartmentDao;
import net.xju.entity.Department;

import java.sql.SQLException;
import java.util.List;

public class DepartmentService {

    private DepartmentDao departmentDao = new DepartmentDao();

    public List<Department> getAllDepartments() throws SQLException {
        return departmentDao.findAllEnabled();
    }

    public void addDep(Department dep) throws SQLException {
        dep.setEnabled(true);
        if (dep.getParent() == null) {
            dep.setParent(false);
        }
        departmentDao.insert(dep);
        dep.setResult(1);
    }

    public void updateDep(Department dep) throws SQLException {
        if (dep.getParent() == null) {
            dep.setParent(false);
        }
        int rows = departmentDao.update(dep);
        dep.setResult(rows > 0 ? 1 : 0);
    }

    public void deleteDepById(Department dep) throws SQLException {
        Long childCount = departmentDao.countChildren(dep.getId());
        if (childCount != null && childCount > 0) {
            dep.setResult(-2);
            return;
        }

        Long empCount = departmentDao.countEmployees(dep.getId());
        if (empCount != null && empCount > 0) {
            dep.setResult(-1);
            return;
        }

        Integer isParent = departmentDao.getIsParent(dep.getId());
        if (isParent == null || isParent == 1) {
            dep.setResult(-2);
            return;
        }

        Integer parentId = departmentDao.getParentId(dep.getId());
        int rows = departmentDao.deleteById(dep.getId());
        if (rows > 0) {
            if (parentId != null) {
                Long siblingCount = departmentDao.countChildren(parentId);
                if (siblingCount != null && siblingCount == 0) {
                    departmentDao.updateIsParent(parentId, 0);
                }
            }
            dep.setResult(1);
        } else {
            dep.setResult(0);
        }
    }

    public List<Department> getAllDepartmentsWithOutChildren() throws SQLException {
        return departmentDao.findWithoutChildren();
    }

    public List<Department> getAllDepartmentsFlat() throws SQLException {
        return departmentDao.findAllEnabled();
    }
}
