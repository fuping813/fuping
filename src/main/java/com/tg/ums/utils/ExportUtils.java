package com.tg.ums.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.plan.TeachingPlan;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExportUtils {

    /**
     * 导出教学计划为Excel文件
     */
    public static byte[] exportTeachingPlanToExcel(List<TeachingPlan> teachingPlans) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("教学计划");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"专业", "届次", "学期", "课程名称", "学分", "授课教师组", "课程类型", "计划状态"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                // 设置表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                // 设置边框
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            int rowNum = 1;
            for (TeachingPlan plan : teachingPlans) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(plan.getMajor().getMajorName());
                row.createCell(1).setCellValue(plan.getBatch().getBatchYear());
                row.createCell(2).setCellValue(plan.getSemester().getSemesterName());
                row.createCell(3).setCellValue(plan.getCourse().getCourseName());
                row.createCell(4).setCellValue(plan.getCourse().getCredits().doubleValue());
                row.createCell(5).setCellValue(plan.getTeachingGroup());
                row.createCell(6).setCellValue(plan.getCourse().getCourseType());
                row.createCell(7).setCellValue(plan.getPlanStatus());
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 输出到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 导出教学计划为PDF文件
     */
    public static byte[] exportTeachingPlanToPDF(List<TeachingPlan> teachingPlans) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, outputStream);
        
        document.open();
        
        // 设置中文字体
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bfChinese, 18, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(bfChinese, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(bfChinese, 11, com.itextpdf.text.Font.NORMAL);
        
        // 添加标题
        Paragraph title = new Paragraph("教学计划安排表", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);
        
        // 创建表格
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        float[] columnWidths = {0.8f, 0.6f, 0.8f, 2.0f, 0.5f, 1.5f, 0.8f, 0.8f};
        table.setWidths(columnWidths);
        
        // 添加表头
        String[] headers = {"专业", "届次", "学期", "课程名称", "学分", "授课教师组", "课程类型", "计划状态"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(5f);
            table.addCell(cell);
        }
        
        // 填充数据
        for (TeachingPlan plan : teachingPlans) {
            addCellToTable(table, plan.getMajor().getMajorName(), contentFont);
            addCellToTable(table, String.valueOf(plan.getBatch().getBatchYear()), contentFont);
            addCellToTable(table, plan.getSemester().getSemesterName(), contentFont);
            addCellToTable(table, plan.getCourse().getCourseName(), contentFont);
            addCellToTable(table, String.valueOf(plan.getCourse().getCredits()), contentFont);
            addCellToTable(table, plan.getTeachingGroup(), contentFont);
            addCellToTable(table, plan.getCourse().getCourseType(), contentFont);
            addCellToTable(table, plan.getPlanStatus(), contentFont);
        }
        
        document.add(table);
        document.close();
        
        return outputStream.toByteArray();
    }
    
    private static void addCellToTable(PdfPTable table, String content, com.itextpdf.text.Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3f);
        table.addCell(cell);
    }

    /**
     * 导出课程目录为Excel文件
     */
    public static byte[] exportCourseCatalogToExcel(List<Course> courses, List<Chapter> chapters, List<KnowledgePoint> knowledgePoints) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("课程目录");
            
            // 创建表头，只包含课程相关信息
            Row headerRow = sheet.createRow(0);
            String[] headers = {"专业名称", "课程代码", "课程名称", "课程类型", "学分", "总学时"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据，只输出课程信息
            int rowNum = 1;
            for (Course course : courses) {
                // 获取专业名称，避免空指针
                String majorName = course.getMajor() != null && course.getMajor().getMajorName() != null ? course.getMajor().getMajorName() : "";
                
                // 只有课程信息，没有章节和知识点
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(majorName);
                row.createCell(1).setCellValue(course.getCourseCode() != null ? course.getCourseCode() : "");
                row.createCell(2).setCellValue(course.getCourseName() != null ? course.getCourseName() : "");
                row.createCell(3).setCellValue(course.getCourseType() != null ? course.getCourseType() : "");
                row.createCell(4).setCellValue(course.getCredits() != null ? course.getCredits().doubleValue() : 0);
                row.createCell(5).setCellValue(course.getTotalHours() != null ? course.getTotalHours() : 0);
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 输出到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 导出成绩统计为Excel文件
     */
    public static byte[] exportScoreStatisticsToExcel(List<Map<String, Object>> statistics) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("成绩统计");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"课程名称", "总人数", "平均分", "最高分", "最低分", "及格率", "优秀率", "不及格率"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            int rowNum = 1;
            for (Map<String, Object> stat : statistics) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(stat.getOrDefault("courseName", "").toString());
                row.createCell(1).setCellValue(Integer.parseInt(stat.getOrDefault("totalStudents", "0").toString()));
                row.createCell(2).setCellValue(Double.parseDouble(stat.getOrDefault("averageScore", "0.0").toString()));
                row.createCell(3).setCellValue(Double.parseDouble(stat.getOrDefault("highestScore", "0.0").toString()));
                row.createCell(4).setCellValue(Double.parseDouble(stat.getOrDefault("lowestScore", "0.0").toString()));
                row.createCell(5).setCellValue(stat.getOrDefault("passRate", "0%").toString());
                row.createCell(6).setCellValue(stat.getOrDefault("excellentRate", "0%").toString());
                row.createCell(7).setCellValue(stat.getOrDefault("failRate", "0%").toString());
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 输出到字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
