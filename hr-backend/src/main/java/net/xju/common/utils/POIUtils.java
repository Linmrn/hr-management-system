package net.xju.common.utils;

import net.xju.entity.*;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class POIUtils {

    /**
     * 导出员工数据为 Excel 文件
     */
    public static void employee2Excel(List<Employee> list, HttpServletResponse response) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        DocumentSummaryInformation docInfo = workbook.getDocumentSummaryInformation();
        docInfo.setCategory("员工信息");
        docInfo.setManager("admin");
        docInfo.setCompany("www.example.com");
        SummaryInformation summInfo = workbook.getSummaryInformation();
        summInfo.setTitle("员工信息表");
        summInfo.setAuthor("admin");
        summInfo.setComments("本文档由系统自动生成");

        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        HSSFCellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

        HSSFSheet sheet = workbook.createSheet("员工信息表");
        // 设置列宽
        sheet.setColumnWidth(0, 5 * 256);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 10 * 256);
        sheet.setColumnWidth(3, 5 * 256);
        sheet.setColumnWidth(4, 12 * 256);
        sheet.setColumnWidth(5, 20 * 256);
        sheet.setColumnWidth(6, 10 * 256);
        sheet.setColumnWidth(7, 10 * 256);
        sheet.setColumnWidth(8, 16 * 256);
        sheet.setColumnWidth(9, 12 * 256);
        sheet.setColumnWidth(10, 15 * 256);
        sheet.setColumnWidth(11, 20 * 256);
        sheet.setColumnWidth(12, 16 * 256);
        sheet.setColumnWidth(13, 14 * 256);
        sheet.setColumnWidth(14, 14 * 256);
        sheet.setColumnWidth(15, 12 * 256);
        sheet.setColumnWidth(16, 8 * 256);
        sheet.setColumnWidth(17, 20 * 256);
        sheet.setColumnWidth(18, 20 * 256);
        sheet.setColumnWidth(19, 15 * 256);
        sheet.setColumnWidth(20, 8 * 256);
        sheet.setColumnWidth(21, 25 * 256);
        sheet.setColumnWidth(22, 14 * 256);
        sheet.setColumnWidth(23, 15 * 256);
        sheet.setColumnWidth(24, 15 * 256);

        // 标题行
        HSSFRow r0 = sheet.createRow(0);
        String[] titles = {"编号", "姓名", "工号", "性别", "出生日期", "身份证号码", "婚姻状况", "民族",
                "籍贯", "政治面貌", "电话号码", "联系地址", "所属部门", "职称", "职位", "聘用形式",
                "最高学历", "专业", "毕业院校", "入职日期", "在职状态", "邮箱", "合同期限(年)",
                "合同起始日期", "合同终止日期"};
        for (int i = 0; i < titles.length; i++) {
            HSSFCell cell = r0.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(headerStyle);
        }

        // 数据填充
        for (int i = 0; i < list.size(); i++) {
            Employee emp = list.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getName());
            row.createCell(2).setCellValue(emp.getWorkID());
            row.createCell(3).setCellValue(emp.getGender());
            HSSFCell cell4 = row.createCell(4);
            cell4.setCellStyle(dateCellStyle);
            cell4.setCellValue(emp.getBirthday());
            row.createCell(5).setCellValue(emp.getIdCard());
            row.createCell(6).setCellValue(emp.getWedlock());
            row.createCell(7).setCellValue(emp.getNation() != null ? emp.getNation().getName() : "");
            row.createCell(8).setCellValue(emp.getNativePlace());
            row.createCell(9).setCellValue(emp.getPoliticsstatus() != null ? emp.getPoliticsstatus().getName() : "");
            row.createCell(10).setCellValue(emp.getPhone());
            row.createCell(11).setCellValue(emp.getAddress());
            row.createCell(12).setCellValue(emp.getDepartment() != null ? emp.getDepartment().getName() : "");
            row.createCell(13).setCellValue(emp.getJobLevel() != null ? emp.getJobLevel().getName() : "");
            row.createCell(14).setCellValue(emp.getPosition() != null ? emp.getPosition().getName() : "");
            row.createCell(15).setCellValue(emp.getEngageForm());
            row.createCell(16).setCellValue(emp.getTiptopDegree());
            row.createCell(17).setCellValue(emp.getSpecialty());
            row.createCell(18).setCellValue(emp.getSchool());
            HSSFCell cell19 = row.createCell(19);
            cell19.setCellStyle(dateCellStyle);
            cell19.setCellValue(emp.getBeginDate());
            row.createCell(20).setCellValue(emp.getWorkState());
            row.createCell(21).setCellValue(emp.getEmail());
            row.createCell(22).setCellValue(emp.getContractTerm());
            HSSFCell cell23 = row.createCell(23);
            cell23.setCellStyle(dateCellStyle);
            cell23.setCellValue(emp.getBeginContract());
            HSSFCell cell24 = row.createCell(24);
            cell24.setCellStyle(dateCellStyle);
            cell24.setCellValue(emp.getEndContract());
        }

        String fileName = URLEncoder.encode("员工表.xls", "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType("application/vnd.ms-excel");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
        out.close();
    }

    /**
     * Excel 解析成员工数据集合（接受 InputStream，供 Controller 调用）
     */
    public static List<Employee> excel2Employee(InputStream inputStream,
                                                List<Nation> allNations,
                                                List<Politicsstatus> allPoliticsstatus,
                                                List<Department> allDepartments,
                                                List<Position> allPositions,
                                                List<JobLevel> allJobLevels) throws IOException {
        List<Employee> list = new ArrayList<>();
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            HSSFSheet sheet = workbook.getSheetAt(i);
            int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
            for (int j = 0; j < physicalNumberOfRows; j++) {
                if (j == 0) continue; // 跳过标题行
                HSSFRow row = sheet.getRow(j);
                if (row == null) continue;

                Employee employee = new Employee();
                int physicalNumberOfCells = row.getPhysicalNumberOfCells();
                for (int k = 0; k < physicalNumberOfCells; k++) {
                    HSSFCell cell = row.getCell(k);
                    switch (cell.getCellType()) {
                        case STRING:
                            String cellValue = cell.getStringCellValue();
                            switch (k) {
                                case 1: employee.setName(cellValue); break;
                                case 2: employee.setWorkID(cellValue); break;
                                case 3: employee.setGender(cellValue); break;
                                case 5: employee.setIdCard(cellValue); break;
                                case 6: employee.setWedlock(cellValue); break;
                                case 7:
                                    int nationIndex = allNations.indexOf(new Nation(cellValue));
                                    if (nationIndex >= 0) employee.setNationId(allNations.get(nationIndex).getId());
                                    break;
                                case 8: employee.setNativePlace(cellValue); break;
                                case 9:
                                    int politicIndex = allPoliticsstatus.indexOf(new Politicsstatus(cellValue));
                                    if (politicIndex >= 0) employee.setPoliticId(allPoliticsstatus.get(politicIndex).getId());
                                    break;
                                case 10: employee.setPhone(cellValue); break;
                                case 11: employee.setAddress(cellValue); break;
                                case 12:
                                    int depIndex = allDepartments.indexOf(new Department(cellValue));
                                    if (depIndex >= 0) employee.setDepartmentId(allDepartments.get(depIndex).getId());
                                    break;
                                case 13:
                                    int jobLevelIndex = allJobLevels.indexOf(new JobLevel(cellValue));
                                    if (jobLevelIndex >= 0) employee.setJobLevelId(allJobLevels.get(jobLevelIndex).getId());
                                    break;
                                case 14:
                                    int posIndex = allPositions.indexOf(new Position(cellValue));
                                    if (posIndex >= 0) employee.setPosId(allPositions.get(posIndex).getId());
                                    break;
                                case 15: employee.setEngageForm(cellValue); break;
                                case 16: employee.setTiptopDegree(cellValue); break;
                                case 17: employee.setSpecialty(cellValue); break;
                                case 18: employee.setSchool(cellValue); break;
                                case 20: employee.setWorkState(cellValue); break;
                                case 21: employee.setEmail(cellValue); break;
                            }
                            break;
                        default:
                            switch (k) {
                                case 4: employee.setBirthday(cell.getDateCellValue()); break;
                                case 19: employee.setBeginDate(cell.getDateCellValue()); break;
                                case 22: employee.setContractTerm(cell.getNumericCellValue()); break;
                                case 23: employee.setBeginContract(cell.getDateCellValue()); break;
                                case 24: employee.setEndContract(cell.getDateCellValue()); break;
                            }
                            break;
                    }
                }
                list.add(employee);
            }
        }
        return list;
    }
}