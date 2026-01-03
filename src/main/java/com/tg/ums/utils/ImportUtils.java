package com.tg.ums.utils;

import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.question.Question;
import com.tg.ums.entity.base.Student;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImportUtils {

    /**
     * 从Excel导入知识点数据
     */
    public static List<KnowledgePoint> importKnowledgePointsFromExcel(InputStream inputStream) throws Exception {
        List<KnowledgePoint> knowledgePoints = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // 跳过表头
            if (rows.hasNext()) {
                rows.next();
            }
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                KnowledgePoint knowledgePoint = new KnowledgePoint();
                
                // 假设Excel列顺序：知识点名称、章节ID、描述、核心考点
                Cell nameCell = currentRow.getCell(0);
                Cell chapterIdCell = currentRow.getCell(1);
                Cell descriptionCell = currentRow.getCell(2);
                Cell keyPointCell = currentRow.getCell(3);
                
                if (nameCell != null) {
                    knowledgePoint.setPointName(nameCell.getStringCellValue());
                }
                if (descriptionCell != null) {
                    knowledgePoint.setPointDescription(descriptionCell.getStringCellValue());
                }
                if (keyPointCell != null) {
                    knowledgePoint.setKeyPoints(keyPointCell.getStringCellValue());
                }
                
                // 设置临时章节ID字段
                if (chapterIdCell != null) {
                    knowledgePoint.setChapterId((int) chapterIdCell.getNumericCellValue());
                }
                
                knowledgePoints.add(knowledgePoint);
            }
        }
        return knowledgePoints;
    }

    /**
     * 辅助方法：安全获取单元格字符串值
     */
    private static String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                // 确保正确处理中文
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 处理数字类型，转换为字符串
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    // 尝试获取字符串值，确保正确处理中文
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    // 如果获取字符串失败，尝试获取数字值
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    /**
     * 从Excel导入试题信息
     * 适配"试题信息 (1).xlsx"格式
     */
    public static List<Question> importQuestionsFromExcel(InputStream inputStream) throws Exception {
        List<Question> questions = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // 跳过表头
            if (rows.hasNext()) {
                Row headerRow = rows.next();
                System.out.println("已跳过表头行");
                // 打印表头信息，了解Excel文件的实际列格式
                for (int i = 0; i < 15; i++) { // 查看前15列
                    Cell cell = headerRow.getCell(i);
                    if (cell != null) {
                        String headerValue = getCellStringValue(cell).trim();
                        System.out.println("表头列 " + i + " (" + (char)('A' + i) + "): " + headerValue);
                    }
                }
            }
            
            int rowNum = 1; // 记录当前处理的行号（从1开始，已跳过表头）
            int successCount = 0;
            int failCount = 0;
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNum++;
                System.out.println("开始处理第 " + rowNum + " 行数据");
                Question question = new Question();
                
                try {
                    // 处理"试题信息 (1).xlsx"实际格式
                    // 列A：题型
                    Cell typeCell = currentRow.getCell(0);
                    String questionType = getCellStringValue(typeCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 题型: ['" + questionType + "']");
                    
                    // 列B：题干
                    Cell contentCell = currentRow.getCell(1);
                    String questionContent = getCellStringValue(contentCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 题干: ['" + (questionContent.length() > 50 ? questionContent.substring(0, 50) + "..." : questionContent) + "']");
                    
                    // 列C：正确答案
                    Cell correctAnswerCell = currentRow.getCell(2);
                    String correctAnswer = getCellStringValue(correctAnswerCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 正确答案: ['" + correctAnswer + "']");
                    
                    // 列D-H：选项（D=A, E=B, F=C, G=D, H=E）
                    StringBuilder optionsBuilder = new StringBuilder();
                    char[] optionLabels = {'A', 'B', 'C', 'D', 'E'};
                    for (int i = 0; i < 5; i++) {
                        Cell optionCell = currentRow.getCell(3 + i); // D列对应索引3
                        String optionValue = getCellStringValue(optionCell).trim();
                        if (!optionValue.isEmpty()) {
                            if (optionsBuilder.length() > 0) {
                                optionsBuilder.append("; ");
                            }
                            optionsBuilder.append(optionLabels[i]).append(": ").append(optionValue);
                            System.out.println("第 " + rowNum + " 行 - 选项" + optionLabels[i] + ": ['" + optionValue + "']");
                        }
                    }
                    String options = optionsBuilder.toString();
                    if (!options.isEmpty()) {
                        question.setOptions(options);
                        System.out.println("第 " + rowNum + " 行 - 合并后选项: ['" + options + "']");
                    } else {
                        System.out.println("第 " + rowNum + " 行 - 未找到有效选项");
                    }
                    
                    // 列L：答案解析
                    Cell analysisCell = currentRow.getCell(11); // L列对应索引11
                    String analysis = getCellStringValue(analysisCell).trim();
                    if (!analysis.isEmpty()) {
                        question.setAnalysis(analysis);
                        System.out.println("第 " + rowNum + " 行 - 答案解析: ['" + (analysis.length() > 50 ? analysis.substring(0, 50) + "..." : analysis) + "']");
                    }
                    
                    // 必填字段验证和设置
                    if (questionType.isEmpty()) {
                        System.err.println("第 " + rowNum + " 行：题目类型不能为空，跳过该行");
                        failCount++;
                        continue;
                    }
                    if (questionContent.isEmpty()) {
                        System.err.println("第 " + rowNum + " 行：题干不能为空，跳过该行");
                        failCount++;
                        continue;
                    }
                    // 允许正确答案为空，因为有些题目可能是问答题
                    if (correctAnswer.isEmpty()) {
                        System.out.println("第 " + rowNum + " 行：注意 - 正确答案为空，可能是问答题");
                    }
                    
                    question.setQuestionType(questionType);
                    question.setQuestionContent(questionContent);
                    question.setCorrectAnswer(correctAnswer);
                    
                    // 设置默认值
                    question.setScore(new java.math.BigDecimal(10)); // 默认为10分
                    question.setDifficultyLevel("中等"); // 默认为中等难度
                    question.setIsQuizAvailable(true); // 默认可用于小测
                    
                    // 设置默认的知识点ID和课程ID
                    // 关联到"数据库原理与应用"课程
                    // 注意：这里设置的ID会在ImportService中被替换为实际存在的课程和知识点
                    question.setKnowledgePointId(1); // 默认知识点ID
                    question.setCourseId(1); // 默认课程ID
                    
                    questions.add(question);
                    successCount++;
                    System.out.println("第 " + rowNum + " 行 - 成功解析一条题目");
                } catch (Exception e) {
                    System.err.println("第 " + rowNum + " 行解析失败: " + e.getMessage());
                    e.printStackTrace();
                    // 跳过当前行继续处理，提高容错性
                    failCount++;
                    continue;
                }
            }
            
            System.out.println("Excel文件解析完成，共处理 " + (successCount + failCount) + " 行数据");
            System.out.println("成功解析：" + successCount + " 条题目");
            System.out.println("解析失败：" + failCount + " 条题目");
        }
        return questions;
    }
    
    /**
     * 从Excel导入学生信息
     */
    public static List<com.tg.ums.entity.base.Student> importStudentsFromExcel(InputStream inputStream) throws Exception {
        List<com.tg.ums.entity.base.Student> students = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // 跳过表头
            if (rows.hasNext()) {
                rows.next();
                System.out.println("已跳过表头行");
            }
            
            int rowNum = 1; // 记录当前处理的行号（从1开始，已跳过表头）
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNum++;
                System.out.println("开始处理第 " + rowNum + " 行学生数据");
                com.tg.ums.entity.base.Student student = new com.tg.ums.entity.base.Student();
                
                try {
                    // Excel列顺序：学号(A)、姓名(B)、性别(C)、专业名称(D)、届次(E)、电话(F)、邮箱(G)
                    Cell studentCodeCell = currentRow.getCell(0);
                    Cell studentNameCell = currentRow.getCell(1);
                    Cell genderCell = currentRow.getCell(2);
                    Cell majorNameCell = currentRow.getCell(3);
                    Cell batchYearCell = currentRow.getCell(4);
                    Cell phoneCell = currentRow.getCell(5);
                    Cell emailCell = currentRow.getCell(6);
                    
                    // 必填字段验证和设置
                    String studentCode = getCellStringValue(studentCodeCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 学号: ['" + studentCode + "']");
                    if (studentCode.isEmpty()) {
                        throw new IllegalArgumentException("第 " + rowNum + " 行：学号不能为空");
                    }
                    student.setStudentCode(studentCode);
                    
                    String studentName = getCellStringValue(studentNameCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 姓名: ['" + studentName + "']");
                    if (studentName.isEmpty()) {
                        throw new IllegalArgumentException("第 " + rowNum + " 行：姓名不能为空");
                    }
                    student.setStudentName(studentName);
                    
                    String gender = getCellStringValue(genderCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 性别: ['" + gender + "']");
                    if (gender.isEmpty()) {
                        gender = "男"; // 默认性别
                    }
                    student.setGender(gender);
                    
                    // 设置联系信息
                    String phone = getCellStringValue(phoneCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 电话: ['" + phone + "']");
                    if (phone.isEmpty()) {
                        phone = "13000000000"; // 默认电话
                    }
                    student.setPhone(phone);
                    
                    String email = getCellStringValue(emailCell).trim();
                    System.out.println("第 " + rowNum + " 行 - 邮箱: ['" + email + "']");
                    if (email.isEmpty()) {
                        email = studentCode + "@example.com"; // 默认邮箱
                    }
                    student.setEmail(email);
                    
                    students.add(student);
                    System.out.println("第 " + rowNum + " 行 - 成功解析一条学生信息");
                } catch (Exception e) {
                    System.err.println("第 " + rowNum + " 行学生数据解析失败: " + e.getMessage());
                    // 可以选择跳过当前行继续处理，或者抛出异常终止整个导入
                    // 这里选择抛出异常，让用户修复问题后重新导入
                    throw e;
                }
            }
            
            System.out.println("Excel文件解析完成，共成功解析 " + students.size() + " 条学生信息");
        }
        return students;
    }
    
    /**
     * 从Excel导入课程数据
     */
    public static List<com.tg.ums.entity.course.Course> importCoursesFromExcel(InputStream inputStream) throws Exception {
        List<com.tg.ums.entity.course.Course> courses = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // 跳过表头
            if (rows.hasNext()) {
                Row headerRow = rows.next();
                System.out.println("已跳过表头行");
                // 打印表头信息，了解Excel文件的列格式
                for (int i = 0; i < 10; i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell != null) {
                        System.out.println("表头列 " + i + ": " + getCellStringValue(cell));
                    }
                }
            }
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                com.tg.ums.entity.course.Course course = new com.tg.ums.entity.course.Course();
                System.out.println("处理行号: " + currentRow.getRowNum());
                
                try {
                    // 灵活处理Excel列，根据实际情况调整
                    // 先获取所有可能的单元格
                    Cell courseCodeCell = currentRow.getCell(0);
                    Cell courseNameCell = currentRow.getCell(1);
                    Cell creditsCell = currentRow.getCell(2);
                    Cell totalHoursCell = currentRow.getCell(3);
                    Cell theoryHoursCell = currentRow.getCell(4);
                    Cell practiceHoursCell = currentRow.getCell(5);
                    Cell courseTypeCell = currentRow.getCell(6);
                    Cell courseNatureCell = currentRow.getCell(7);
                    
                    // 打印单元格值，调试用
                    System.out.println("课程代码: " + (courseCodeCell != null ? getCellStringValue(courseCodeCell) : "null"));
                    System.out.println("课程名称: " + (courseNameCell != null ? getCellStringValue(courseNameCell) : "null"));
                    System.out.println("学分: " + (creditsCell != null ? creditsCell.getNumericCellValue() : "null"));
                    System.out.println("总学时: " + (totalHoursCell != null ? totalHoursCell.getNumericCellValue() : "null"));
                    System.out.println("理论学时: " + (theoryHoursCell != null ? theoryHoursCell.getNumericCellValue() : "null"));
                    System.out.println("实践学时: " + (practiceHoursCell != null ? practiceHoursCell.getNumericCellValue() : "null"));
                    System.out.println("课程类型: " + (courseTypeCell != null ? getCellStringValue(courseTypeCell) : "null"));
                    System.out.println("课程性质: " + (courseNatureCell != null ? getCellStringValue(courseNatureCell) : "null"));
                    
                    // 设置课程属性，确保必填字段有值
                    if (courseCodeCell != null) {
                        String courseCode = getCellStringValue(courseCodeCell).trim();
                        if (!courseCode.isEmpty()) {
                            course.setCourseCode(courseCode);
                        } else {
                            course.setCourseCode("COURSE_" + (courses.size() + 1)); // 默认课程代码
                        }
                    } else {
                        course.setCourseCode("COURSE_" + (courses.size() + 1)); // 默认课程代码
                    }
                    
                    if (courseNameCell != null) {
                        String courseName = getCellStringValue(courseNameCell).trim();
                        if (!courseName.isEmpty()) {
                            course.setCourseName(courseName);
                        } else {
                            course.setCourseName("未命名课程" + (courses.size() + 1)); // 默认课程名称
                        }
                    } else {
                        course.setCourseName("未命名课程" + (courses.size() + 1)); // 默认课程名称
                    }
                    
                    if (creditsCell != null) {
                        double credits = creditsCell.getNumericCellValue();
                        course.setCredits(new java.math.BigDecimal(credits));
                    } else {
                        course.setCredits(new java.math.BigDecimal(3.0)); // 默认学分
                    }
                    
                    if (totalHoursCell != null) {
                        course.setTotalHours((int) totalHoursCell.getNumericCellValue());
                    } else {
                        course.setTotalHours(48); // 默认总学时
                    }
                    
                    if (theoryHoursCell != null) {
                        course.setTheoryHours((int) theoryHoursCell.getNumericCellValue());
                    } else {
                        course.setTheoryHours(32); // 默认理论学时
                    }
                    
                    if (practiceHoursCell != null) {
                        course.setPracticeHours((int) practiceHoursCell.getNumericCellValue());
                    } else {
                        course.setPracticeHours(16); // 默认实践学时
                    }
                    
                    if (courseTypeCell != null) {
                        String courseType = getCellStringValue(courseTypeCell).trim();
                        if (!courseType.isEmpty()) {
                            course.setCourseType(courseType);
                        } else {
                            course.setCourseType("必修课"); // 默认课程类型
                        }
                    } else {
                        course.setCourseType("必修课"); // 默认课程类型
                    }
                    
                    if (courseNatureCell != null) {
                        String courseNature = getCellStringValue(courseNatureCell).trim();
                        if (!courseNature.isEmpty()) {
                            course.setCourseNature(courseNature);
                        } else {
                            course.setCourseNature("专业课"); // 默认课程性质
                        }
                    } else {
                        course.setCourseNature("专业课"); // 默认课程性质
                    }
                    
                    courses.add(course);
                    System.out.println("成功添加课程: " + course.getCourseName());
                } catch (Exception e) {
                    System.err.println("处理行 " + currentRow.getRowNum() + " 时出错: " + e.getMessage());
                    e.printStackTrace();
                    // 跳过当前行，继续处理下一行
                    continue;
                }
            }
        }
        System.out.println("共解析到 " + courses.size() + " 条课程数据");
        return courses;
    }
    
    /**
     * 从"数据库课程--知识点.xlsx"导入数据库课程和知识点
     * 该方法处理特定格式的Excel文件，包含数据库课程的多级知识点信息
     */
    public static Map<String, Object> importDatabaseCourseAndKnowledgePoints(InputStream inputStream) throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<com.tg.ums.entity.course.Course> courses = new ArrayList<>();
        List<com.tg.ums.entity.course.Chapter> chapters = new ArrayList<>();
        List<KnowledgePoint> knowledgePoints = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // 跳过表头
            if (rows.hasNext()) {
                Row headerRow = rows.next();
                System.out.println("已跳过表头行");
                // 打印表头信息，了解Excel文件的实际列格式
                for (int i = 0; i < 15; i++) {
                    Cell cell = headerRow.getCell(i);
                    if (cell != null) {
                        String headerValue = getCellStringValue(cell).trim();
                        System.out.println("表头列 " + i + " (" + (char)('A' + i) + "): " + headerValue);
                    }
                }
            }
            
            // 创建默认数据库课程
            com.tg.ums.entity.course.Course databaseCourse = new com.tg.ums.entity.course.Course();
            databaseCourse.setCourseCode("DB_COURSE");
            databaseCourse.setCourseName("数据库系统");
            databaseCourse.setCredits(new java.math.BigDecimal(4.0));
            databaseCourse.setTotalHours(64);
            databaseCourse.setTheoryHours(48);
            databaseCourse.setPracticeHours(16);
            databaseCourse.setCourseType("必修课");
            databaseCourse.setCourseNature("专业基础课");
            courses.add(databaseCourse);
            System.out.println("已创建默认数据库课程: 数据库系统");
            
            // 存储各级知识点和章节，用于构建层级关系
            Map<String, KnowledgePoint> pointMap = new HashMap<>();
            Map<String, com.tg.ums.entity.course.Chapter> chapterMap = new HashMap<>();
            
            // 存储当前处理的各级知识点名称
            String currentLevel1 = "";
            String currentLevel2 = "";
            String currentLevel3 = "";
            String currentLevel4 = "";
            String currentLevel5 = "";
            String currentLevel6 = "";
            
            // 存储当前层级的知识点对象
            KnowledgePoint parentLevel1 = null;
            KnowledgePoint parentLevel2 = null;
            KnowledgePoint parentLevel3 = null;
            KnowledgePoint parentLevel4 = null;
            KnowledgePoint parentLevel5 = null;
            KnowledgePoint parentLevel6 = null;
            
            int chapterOrder = 1;
            int rowNum = 1;
            int successCount = 0;
            int failCount = 0;
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNum++;
                System.out.println("开始处理第 " + rowNum + " 行数据");
                
                try {
                    // 处理"数据库课程--知识点.xlsx"实际格式
                    // 列A-G：各级知识点
                    String level1 = getCellStringValue(currentRow.getCell(0)).trim(); // 一级知识点
                    String level2 = getCellStringValue(currentRow.getCell(1)).trim(); // 二级知识点
                    String level3 = getCellStringValue(currentRow.getCell(2)).trim(); // 三级知识点
                    String level4 = getCellStringValue(currentRow.getCell(3)).trim(); // 四级知识点
                    String level5 = getCellStringValue(currentRow.getCell(4)).trim(); // 五级知识点
                    String level6 = getCellStringValue(currentRow.getCell(5)).trim(); // 六级知识点
                    String level7 = getCellStringValue(currentRow.getCell(6)).trim(); // 七级知识点
                    
                    // 列H-M：其他属性
                    String prerequisites = getCellStringValue(currentRow.getCell(7)).trim(); // 前置知识点
                    String postrequisites = getCellStringValue(currentRow.getCell(8)).trim(); // 后置知识点
                    String relatedPoints = getCellStringValue(currentRow.getCell(9)).trim(); // 关联知识点
                    String tags = getCellStringValue(currentRow.getCell(10)).trim(); // 标签
                    String cognitiveLevel = getCellStringValue(currentRow.getCell(11)).trim(); // 认知难度
                    String classification = getCellStringValue(currentRow.getCell(12)).trim(); // 分类结论
                    
                    // 打印当前行的各级知识点
                    System.out.println("第 " + rowNum + " 行 - 知识点层级: L1: '" + level1 + "', L2: '" + level2 + "', L3: '" + level3 + "', L4: '" + level4 + "', L5: '" + level5 + "', L6: '" + level6 + "', L7: '" + level7 + "'");
                    
                    // 一级知识点处理 - 章节级别
                    if (!level1.isEmpty()) {
                        // 按一级知识点创建章节
                        if (!chapterMap.containsKey(level1)) {
                            com.tg.ums.entity.course.Chapter chapter = new com.tg.ums.entity.course.Chapter();
                            chapter.setCourse(databaseCourse);
                            chapter.setChapterName(level1);
                            chapter.setChapterOrder(chapterOrder++);
                            chapter.setChapterDescription("章节：" + level1);
                            chapters.add(chapter);
                            chapterMap.put(level1, chapter);
                            System.out.println("已创建章节: " + level1);
                        }
                        
                        // 创建或更新一级知识点
                        if (!pointMap.containsKey(level1)) {
                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                            knowledgePoint.setPointName(level1);
                            knowledgePoint.setPointDescription(level1);
                            knowledgePoint.setLevel(1); // 一级知识点
                            knowledgePoint.setChapterId(1); // 临时设置，后续更新
                            knowledgePoint.setParent(null);
                            knowledgePoints.add(knowledgePoint);
                            pointMap.put(level1, knowledgePoint);
                            System.out.println("已创建一级知识点: " + level1);
                        }
                        currentLevel1 = level1;
                        parentLevel1 = pointMap.get(level1);
                        currentLevel2 = "";
                        currentLevel3 = "";
                        currentLevel4 = "";
                        currentLevel5 = "";
                        currentLevel6 = "";
                    }
                    
                    // 二级知识点处理
                    if (!level2.isEmpty()) {
                        String fullPath = level1 + "." + level2;
                        if (!pointMap.containsKey(fullPath)) {
                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                            knowledgePoint.setPointName(level2);
                            knowledgePoint.setPointDescription(level1 + " > " + level2);
                            knowledgePoint.setLevel(2); // 二级知识点
                            knowledgePoint.setChapterId(1); // 临时设置，后续更新
                            knowledgePoint.setParent(parentLevel1);
                            knowledgePoints.add(knowledgePoint);
                            pointMap.put(fullPath, knowledgePoint);
                            System.out.println("已创建二级知识点: " + level2 + "，父级: " + parentLevel1.getPointName());
                        }
                        currentLevel2 = level2;
                        parentLevel2 = pointMap.get(fullPath);
                        currentLevel3 = "";
                        currentLevel4 = "";
                        currentLevel5 = "";
                        currentLevel6 = "";
                    }
                    
                    // 三级知识点处理
                    if (!level3.isEmpty()) {
                        String fullPath = level1 + "." + level2 + "." + level3;
                        if (!pointMap.containsKey(fullPath)) {
                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                            knowledgePoint.setPointName(level3);
                            knowledgePoint.setPointDescription(level1 + " > " + level2 + " > " + level3);
                            knowledgePoint.setLevel(3); // 三级知识点
                            knowledgePoint.setChapterId(1); // 临时设置，后续更新
                            knowledgePoint.setParent(parentLevel2);
                            knowledgePoints.add(knowledgePoint);
                            pointMap.put(fullPath, knowledgePoint);
                            System.out.println("已创建三级知识点: " + level3 + "，父级: " + parentLevel2.getPointName());
                        }
                        currentLevel3 = level3;
                        parentLevel3 = pointMap.get(fullPath);
                        currentLevel4 = "";
                        currentLevel5 = "";
                        currentLevel6 = "";
                    }
                    
                    // 四级知识点处理
                    if (!level4.isEmpty()) {
                        String fullPath = level1 + "." + level2 + "." + level3 + "." + level4;
                        if (!pointMap.containsKey(fullPath)) {
                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                            knowledgePoint.setPointName(level4);
                            knowledgePoint.setPointDescription(level1 + " > " + level2 + " > " + level3 + " > " + level4);
                            knowledgePoint.setLevel(4); // 四级知识点
                            knowledgePoint.setChapterId(1); // 临时设置，后续更新
                            knowledgePoint.setParent(parentLevel3);
                            knowledgePoints.add(knowledgePoint);
                            pointMap.put(fullPath, knowledgePoint);
                            System.out.println("已创建四级知识点: " + level4 + "，父级: " + parentLevel3.getPointName());
                        }
                        currentLevel4 = level4;
                        parentLevel4 = pointMap.get(fullPath);
                        currentLevel5 = "";
                        currentLevel6 = "";
                    }
                    
                    // 五级知识点处理
                    if (!level5.isEmpty()) {
                        String fullPath = level1 + "." + level2 + "." + level3 + "." + level4 + "." + level5;
                        if (!pointMap.containsKey(fullPath)) {
                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                            knowledgePoint.setPointName(level5);
                            knowledgePoint.setPointDescription(level1 + " > " + level2 + " > " + level3 + " > " + level4 + " > " + level5);
                            knowledgePoint.setLevel(5); // 五级知识点
                            knowledgePoint.setChapterId(1); // 临时设置，后续更新
                            knowledgePoint.setParent(parentLevel4);
                            knowledgePoints.add(knowledgePoint);
                            pointMap.put(fullPath, knowledgePoint);
                            System.out.println("已创建五级知识点: " + level5 + "，父级: " + parentLevel4.getPointName());
                        }
                        currentLevel5 = level5;
                        parentLevel5 = pointMap.get(fullPath);
                        currentLevel6 = "";
                    }
                    
                    // 六级知识点处理
                    if (!level6.isEmpty()) {
                        String fullPath = level1 + "." + level2 + "." + level3 + "." + level4 + "." + level5 + "." + level6;
                        if (!pointMap.containsKey(fullPath)) {
                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                            knowledgePoint.setPointName(level6);
                            knowledgePoint.setPointDescription(level1 + " > " + level2 + " > " + level3 + " > " + level4 + " > " + level5 + " > " + level6);
                            knowledgePoint.setLevel(6); // 六级知识点
                            knowledgePoint.setChapterId(1); // 临时设置，后续更新
                            knowledgePoint.setParent(parentLevel5);
                            knowledgePoints.add(knowledgePoint);
                            pointMap.put(fullPath, knowledgePoint);
                            System.out.println("已创建六级知识点: " + level6 + "，父级: " + parentLevel5.getPointName());
                        }
                        currentLevel6 = level6;
                        parentLevel6 = pointMap.get(fullPath);
                    }
                    
                    // 七级知识点处理 - 最细粒度的知识点
                    if (!level7.isEmpty()) {
                        String fullPath = level1 + "." + level2 + "." + level3 + "." + level4 + "." + level5 + "." + level6 + "." + level7;
                        if (!pointMap.containsKey(fullPath)) {
                            KnowledgePoint knowledgePoint = new KnowledgePoint();
                            knowledgePoint.setPointName(level7);
                            
                            // 设置知识点描述，使用各级知识点组合作为描述
                            StringBuilder descriptionBuilder = new StringBuilder();
                            if (!level1.isEmpty()) descriptionBuilder.append(level1);
                            if (!level2.isEmpty()) descriptionBuilder.append(" > " + level2);
                            if (!level3.isEmpty()) descriptionBuilder.append(" > " + level3);
                            if (!level4.isEmpty()) descriptionBuilder.append(" > " + level4);
                            if (!level5.isEmpty()) descriptionBuilder.append(" > " + level5);
                            if (!level6.isEmpty()) descriptionBuilder.append(" > " + level6);
                            if (!level7.isEmpty()) descriptionBuilder.append(" > " + level7);
                            knowledgePoint.setPointDescription(descriptionBuilder.toString());
                            
                            // 设置核心考点，使用相关属性组合
                            StringBuilder keyPointsBuilder = new StringBuilder();
                            if (!tags.isEmpty()) keyPointsBuilder.append("标签: " + tags + "; ");
                            if (!cognitiveLevel.isEmpty()) keyPointsBuilder.append("认知难度: " + cognitiveLevel + "; ");
                            if (!classification.isEmpty()) keyPointsBuilder.append("分类: " + classification);
                            if (keyPointsBuilder.length() > 0) {
                                knowledgePoint.setKeyPoints(keyPointsBuilder.toString());
                            }
                            
                            // 设置层级关系
                            knowledgePoint.setLevel(7); // 七级知识点
                            knowledgePoint.setChapterId(1); // 临时设置，后续更新
                            
                            // 设置父知识点
                            if (parentLevel6 != null) {
                                knowledgePoint.setParent(parentLevel6);
                            } else if (parentLevel5 != null) {
                                knowledgePoint.setParent(parentLevel5);
                            } else if (parentLevel4 != null) {
                                knowledgePoint.setParent(parentLevel4);
                            } else if (parentLevel3 != null) {
                                knowledgePoint.setParent(parentLevel3);
                            } else if (parentLevel2 != null) {
                                knowledgePoint.setParent(parentLevel2);
                            } else if (parentLevel1 != null) {
                                knowledgePoint.setParent(parentLevel1);
                            } else {
                                knowledgePoint.setParent(null);
                            }
                            
                            // 添加到知识点列表
                            knowledgePoints.add(knowledgePoint);
                            pointMap.put(fullPath, knowledgePoint);
                            successCount++;
                            System.out.println("第 " + rowNum + " 行 - 成功添加七级知识点: " + level7 + "，父级: " + (knowledgePoint.getParent() != null ? knowledgePoint.getParent().getPointName() : "无"));
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("第 " + rowNum + " 行解析失败: " + e.getMessage());
                    e.printStackTrace();
                    // 跳过当前行，继续处理下一行
                    failCount++;
                    continue;
                }
            }
            
            // 为所有知识点设置正确的章节对象和章节ID
                    for (KnowledgePoint knowledgePoint : knowledgePoints) {
                        if (knowledgePoint.getLevel() == 1 && chapterMap.containsKey(knowledgePoint.getPointName())) {
                            // 一级知识点关联对应的章节
                            com.tg.ums.entity.course.Chapter chapter = chapterMap.get(knowledgePoint.getPointName());
                            knowledgePoint.setChapter(chapter);
                            // 章节ID设置为章节在列表中的索引+1（因为ID是自增的）
                            knowledgePoint.setChapterId(chapters.indexOf(chapter) + 1);
                        } else {
                            // 其他级知识点继承父级的章节
                            if (knowledgePoint.getParent() != null) {
                                knowledgePoint.setChapter(knowledgePoint.getParent().getChapter());
                                knowledgePoint.setChapterId(knowledgePoint.getParent().getChapterId());
                            } else {
                                // 如果没有父级，使用第一个章节
                                knowledgePoint.setChapter(chapters.get(0));
                                knowledgePoint.setChapterId(1);
                            }
                        }
                    }
            
            System.out.println("Excel文件解析完成，共处理 " + (successCount + failCount) + " 行数据");
            System.out.println("成功解析：" + successCount + " 条知识点");
            System.out.println("解析失败：" + failCount + " 条知识点");
            System.out.println("已创建 " + chapters.size() + " 个章节");
        }
        
        result.put("courses", courses);
        result.put("chapters", chapters);
        result.put("knowledgePoints", knowledgePoints);
        System.out.println("共生成 " + courses.size() + " 条课程数据，" + chapters.size() + " 个章节和 " + knowledgePoints.size() + " 条知识点数据");
        
        return result;
    }
}