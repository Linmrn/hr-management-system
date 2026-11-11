package net.xju.controller;

import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.PageInfo;
import net.xju.common.utils.Result;
import net.xju.entity.SalaryRecord;
import net.xju.service.SalaryRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.net.URLEncoder;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import java.io.IOException;

/**
 * 工资表管理控制器
 * 负责工资记录的增删改查、分页查询和Excel导出
 **/
public class SalaryTableController {
    private SalaryRecordService service = new SalaryRecordService();
    private ObjectMapper mapper = new ObjectMapper();

    //分页查询工作记录
    @RequestMapping("/salary/table/list.do")
    public String list(HttpServletRequest req) {
        try {
            int page = parseInt(req.getParameter("page"), 1);
            int size = parseInt(req.getParameter("size"), 10);
            String yearMonth = req.getParameter("yearMonth");
            String keyword = req.getParameter("keyword");
            List<SalaryRecord> records = service.getByPage(page, size, yearMonth, keyword);
            int total = service.count(yearMonth, keyword);
            return "json:" + JSONUtil.toJson(Result.success(new PageInfo<>(page, size, total, records)));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 添加工资记录
     * 接收前端JSON数据，自动计算应发合计和实发工资
     */
    @RequestMapping(value="/salary/table/add.do", methods={"POST", "PUT", "DELETE"})
    public String add(HttpServletRequest req) {
        try {
            SalaryRecord record = mapper.readValue(req.getInputStream(), SalaryRecord.class);
            if (service.add(record) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("添加成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    /**
     * 更新工资记录
     * 已结算的记录不可修改（数据库条件原子校验）
     */
    // 🔥 修复：补全 update 方法
    @RequestMapping(value="/salary/table/update.do", methods={"POST", "PUT", "DELETE"})
    public String update(HttpServletRequest req) {
        try {
            SalaryRecord record = mapper.readValue(req.getInputStream(), SalaryRecord.class);
            if (service.update(record) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.error("记录不存在或已结算，无法修改", 409));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除工资记录
     * 已结算的记录不可删除（数据库条件原子校验）
     */
    @RequestMapping(value="/salary/table/deleteRecord.do", methods={"POST", "PUT", "DELETE"})
    public String delete(HttpServletRequest req) {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            if (service.delete(id) == 1) {
                return "json:" + JSONUtil.toJson(Result.success("删除成功"));
            }
            return "json:" + JSONUtil.toJson(Result.error("记录不存在或已结算，无法删除", 409));
        } catch (Exception e) {
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    /**
     * 导出Excel
     * 根据搜索条件导出所有符合条件的工资记录
     */
    @RequestMapping("/salary/table/export.do")
    public void export(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String yearMonth = req.getParameter("yearMonth");
            String keyword = req.getParameter("keyword");

            // 查询所有符合条件的记录（不分页）
            List<SalaryRecord> list = service.getByPage(1, Integer.MAX_VALUE, yearMonth, keyword);

            // 调用 POI 工具类生成 Excel
            exportToExcel(list, resp);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                resp.getWriter().write("导出失败：" + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 生成 Excel 文件并输出到响应流
     * 使用 Apache POI 创建 .xls 格式文件
     * 文件名包含时间戳，避免重复下载时覆盖
     */
    private void exportToExcel(List<SalaryRecord> list, HttpServletResponse resp) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("工资表");

        // 创建表头
        String[] headers = {"员工ID", "年月", "基本工资", "奖金", "餐补", "交通补",
                "应发合计", "养老保险", "医疗保险", "公积金", "个税", "实发工资", "状态"};
        HSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (SalaryRecord r : list) {
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getEid());
            row.createCell(1).setCellValue(r.getYearMonth());
            row.createCell(2).setCellValue(r.getBasicSalary().doubleValue());
            row.createCell(3).setCellValue(r.getBonus().doubleValue());
            row.createCell(4).setCellValue(r.getLunchSalary().doubleValue());
            row.createCell(5).setCellValue(r.getTrafficSalary().doubleValue());
            row.createCell(6).setCellValue(r.getAllSalary().doubleValue());
            row.createCell(7).setCellValue(r.getPension().doubleValue());
            row.createCell(8).setCellValue(r.getMedical().doubleValue());
            row.createCell(9).setCellValue(r.getAccumulationFund().doubleValue());
            row.createCell(10).setCellValue(r.getTax().doubleValue());
            row.createCell(11).setCellValue(r.getActualSalary().doubleValue());
            row.createCell(12).setCellValue(r.getStatus() == 1 ? "已结算" : "未结算");
        }

        // 输出到响应
        resp.setContentType("application/vnd.ms-excel");
        String fileName = URLEncoder.encode("工资表_" + System.currentTimeMillis() + ".xls", "UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        workbook.write(resp.getOutputStream());
        workbook.close();
    }

    /**
     * 解析整数参数，若为空或无效则返回默认值
     */
    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}