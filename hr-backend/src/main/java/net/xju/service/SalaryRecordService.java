package net.xju.service;

import net.xju.dao.SalaryRecordDao;
import net.xju.entity.SalaryRecord;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class SalaryRecordService {
    private SalaryRecordDao dao = new SalaryRecordDao();

    public List<SalaryRecord> getByPage(int page, int size, String yearMonth, String keyword) throws SQLException {
        int offset = (page - 1) * size;
        return dao.findByPage(offset, size, yearMonth, keyword);
    }

    public int count(String yearMonth, String keyword) throws SQLException {
        return dao.count(yearMonth, keyword);
    }

    public int add(SalaryRecord record) throws SQLException {
        calculate(record);
        return dao.insert(record);
    }

    public int update(SalaryRecord record) throws SQLException {
        calculate(record);
        return dao.update(record);
    }

    public int delete(Integer id) throws SQLException {
        return dao.deleteById(id);
    }

    private void calculate(SalaryRecord r) {
        // 🔥 修复：所有字段判空，避免 NPE
        if (r.getBasicSalary() == null) r.setBasicSalary(BigDecimal.ZERO);
        if (r.getBonus() == null) r.setBonus(BigDecimal.ZERO);
        if (r.getLunchSalary() == null) r.setLunchSalary(BigDecimal.ZERO);
        if (r.getTrafficSalary() == null) r.setTrafficSalary(BigDecimal.ZERO);
        if (r.getPension() == null) r.setPension(BigDecimal.ZERO);
        if (r.getMedical() == null) r.setMedical(BigDecimal.ZERO);
        if (r.getAccumulationFund() == null) r.setAccumulationFund(BigDecimal.ZERO);
        if (r.getTax() == null) r.setTax(BigDecimal.ZERO);
        if (r.getStatus() == null) r.setStatus(0);
        if (r.getStatus() != 0 && r.getStatus() != 1) throw new IllegalArgumentException("无效结算状态");

        BigDecimal all = r.getBasicSalary()
                .add(r.getBonus())
                .add(r.getLunchSalary())
                .add(r.getTrafficSalary());
        r.setAllSalary(all);

        BigDecimal actual = all
                .subtract(r.getPension())
                .subtract(r.getMedical())
                .subtract(r.getAccumulationFund())
                .subtract(r.getTax());
        r.setActualSalary(actual);
    }
}