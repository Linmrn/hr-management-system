package net.xju.dao;

import net.xju.common.utils.JDBCUtils;
import net.xju.entity.SalaryRecord;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalaryRecordDao {
    private QueryRunner runner = new QueryRunner(JDBCUtils.getDataSource());

    public List<SalaryRecord> findByPage(int offset, int size, String yearMonth, String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM salary_record WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (yearMonth != null && !yearMonth.isEmpty()) {
            sql.append(" AND yearMonth = ?");
            params.add(yearMonth);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND eid IN (SELECT id FROM employee WHERE name LIKE ? OR workID LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        sql.append(" ORDER BY id ASC LIMIT ?, ?");
        params.add(offset);
        params.add(size);

        return runner.query(sql.toString(), new BeanListHandler<>(SalaryRecord.class), params.toArray());
    }

    public int count(String yearMonth, String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM salary_record WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (yearMonth != null && !yearMonth.isEmpty()) {
            sql.append(" AND yearMonth = ?");
            params.add(yearMonth);
        }
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND eid IN (SELECT id FROM employee WHERE name LIKE ? OR workID LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        Long total = runner.query(sql.toString(), new ScalarHandler<Long>(), params.toArray());
        return total == null ? 0 : total.intValue();
    }

    public int insert(SalaryRecord record) throws SQLException {
        String sql = "INSERT INTO salary_record (eid, yearMonth, basicSalary, bonus, lunchSalary, trafficSalary, " +
                "pension, medical, accumulationFund, tax, allSalary, actualSalary, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return runner.update(sql,
                record.getEid(),
                record.getYearMonth(),
                record.getBasicSalary(),
                record.getBonus(),
                record.getLunchSalary(),
                record.getTrafficSalary(),
                record.getPension(),
                record.getMedical(),
                record.getAccumulationFund(),
                record.getTax(),
                record.getAllSalary(),
                record.getActualSalary(),
                record.getStatus() != null ? record.getStatus() : 0
        );
    }

    public int update(SalaryRecord record) throws SQLException {
        String sql = "UPDATE salary_record SET basicSalary=?, bonus=?, lunchSalary=?, trafficSalary=?, " +
                "pension=?, medical=?, accumulationFund=?, tax=?, allSalary=?, actualSalary=?, status=? " +
                "WHERE id=? AND status=0";
        return runner.update(sql,
                record.getBasicSalary(),
                record.getBonus(),
                record.getLunchSalary(),
                record.getTrafficSalary(),
                record.getPension(),
                record.getMedical(),
                record.getAccumulationFund(),
                record.getTax(),
                record.getAllSalary(),
                record.getActualSalary(),
                record.getStatus(),
                record.getId()
        );
    }

    public int deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM salary_record WHERE id = ? AND status=0";
        return runner.update(sql, id);
    }
}