package net.xju.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SalaryRecord {
    private Integer id;
    private Integer eid;
    private String yearMonth;
    private BigDecimal basicSalary;
    private BigDecimal bonus;
    private BigDecimal lunchSalary;
    private BigDecimal trafficSalary;
    private BigDecimal pension;
    private BigDecimal medical;
    private BigDecimal accumulationFund;
    private BigDecimal tax;
    private BigDecimal allSalary;
    private BigDecimal actualSalary;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ========== Getter & Setter ==========
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getEid() { return eid; }
    public void setEid(Integer eid) { this.eid = eid; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public BigDecimal getLunchSalary() { return lunchSalary; }
    public void setLunchSalary(BigDecimal lunchSalary) { this.lunchSalary = lunchSalary; }

    public BigDecimal getTrafficSalary() { return trafficSalary; }
    public void setTrafficSalary(BigDecimal trafficSalary) { this.trafficSalary = trafficSalary; }

    public BigDecimal getPension() { return pension; }
    public void setPension(BigDecimal pension) { this.pension = pension; }

    public BigDecimal getMedical() { return medical; }
    public void setMedical(BigDecimal medical) { this.medical = medical; }

    public BigDecimal getAccumulationFund() { return accumulationFund; }
    public void setAccumulationFund(BigDecimal accumulationFund) { this.accumulationFund = accumulationFund; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getAllSalary() { return allSalary; }
    public void setAllSalary(BigDecimal allSalary) { this.allSalary = allSalary; }

    public BigDecimal getActualSalary() { return actualSalary; }
    public void setActualSalary(BigDecimal actualSalary) { this.actualSalary = actualSalary; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}