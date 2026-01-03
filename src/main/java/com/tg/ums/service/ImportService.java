package com.tg.ums.service;

import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.question.Question;
import com.tg.ums.repository.ChapterRepository;
import com.tg.ums.repository.CourseRepository;
import com.tg.ums.repository.KnowledgePointRepository;
import com.tg.ums.repository.QuestionRepository;
import com.tg.ums.repository.StudentRepository;
import com.tg.ums.repository.MajorRepository;
import com.tg.ums.repository.BatchRepository;
import com.tg.ums.utils.ImportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ImportService {

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private MajorRepository majorRepository;
    
    @Autowired
    private BatchRepository batchRepository;

    /**
     * 导入知识点数据
     */
    public List<KnowledgePoint> importKnowledgePoints(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 增强的文件格式校验：同时检查扩展名和ContentType
        boolean isXlsx = file.getOriginalFilename().toLowerCase().endsWith(".xlsx");
        boolean isExcelContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType()) ||
                                     "application/vnd.ms-excel".equals(file.getContentType()) ||
                                     "application/octet-stream".equals(file.getContentType());
                                      
        if (!isXlsx && !isExcelContentType) {
            throw new IllegalArgumentException("文件格式错误，请上传.xlsx格式的Excel文件");
        }

        List<KnowledgePoint> knowledgePoints = ImportUtils.importKnowledgePointsFromExcel(file.getInputStream());
        
        // 处理章节关联
        for (KnowledgePoint knowledgePoint : knowledgePoints) {
            if (knowledgePoint.getChapterId() != null) {
                Optional<Chapter> chapterOptional = chapterRepository.findById(knowledgePoint.getChapterId());
                if (chapterOptional.isEmpty()) {
                    throw new IllegalArgumentException("章节ID " + knowledgePoint.getChapterId() + " 不存在");
                }
                knowledgePoint.setChapter(chapterOptional.get());
            }
        }
        
        return knowledgePointRepository.saveAll(knowledgePoints);
    }

    /**
     * 导入试题信息
     */
    public List<Question> importQuestions(MultipartFile file) throws Exception {
        System.out.println("收到导入请求，文件名：" + file.getOriginalFilename());
        System.out.println("文件大小：" + file.getSize() + "字节");
        System.out.println("文件类型：" + file.getContentType());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 增强的文件格式校验：同时检查扩展名和ContentType
        boolean isXlsx = file.getOriginalFilename().toLowerCase().endsWith(".xlsx");
        boolean isExcelContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType()) ||
                                     "application/vnd.ms-excel".equals(file.getContentType()) ||
                                     "application/octet-stream".equals(file.getContentType());
                                      
        if (!isXlsx && !isExcelContentType) {
            System.out.println("文件格式校验失败：扩展名=" + (isXlsx ? "xlsx" : "非xlsx") + ", ContentType=" + file.getContentType());
            throw new IllegalArgumentException("文件格式错误，请上传.xlsx格式的Excel文件");
        }

        System.out.println("开始解析Excel文件...");
        List<Question> questions = ImportUtils.importQuestionsFromExcel(file.getInputStream());
        System.out.println("Excel文件解析完成，共解析到 " + questions.size() + " 条题目");
        
        // 查找或创建"数据库原理与应用"课程
        Optional<Course> courseOptional = courseRepository.findByCourseName("数据库原理与应用");
        Course databaseCourse = courseOptional.orElseGet(() -> {
            System.out.println("数据库原理与应用课程不存在，正在创建...");
            Course course = new Course();
            course.setCourseCode("DB_APPLICATION");
            course.setCourseName("数据库原理与应用");
            course.setCredits(new BigDecimal(4.0));
            course.setTotalHours(64);
            course.setTheoryHours(48);
            course.setPracticeHours(16);
            course.setCourseType("必修课");
            course.setCourseNature("专业基础课");
            return courseRepository.save(course);
        });
        System.out.println("已找到/创建数据库原理与应用课程，ID：" + databaseCourse.getCourseId());
        
        // 确保该课程有至少一个章节
        List<Chapter> courseChapters = chapterRepository.findByCourse(databaseCourse);
        Chapter defaultChapter;
        if (courseChapters.isEmpty()) {
            System.out.println("数据库原理与应用课程没有章节，正在创建默认章节...");
            defaultChapter = new Chapter();
            defaultChapter.setChapterName("默认章节");
            defaultChapter.setChapterOrder(1);
            defaultChapter.setCourse(databaseCourse);
            defaultChapter = chapterRepository.save(defaultChapter);
            System.out.println("已创建默认章节，ID：" + defaultChapter.getChapterId());
        } else {
            defaultChapter = courseChapters.get(0);
            System.out.println("使用现有章节：" + defaultChapter.getChapterName() + "，ID：" + defaultChapter.getChapterId());
        }
        
        // 确保该章节有至少一个知识点
        List<KnowledgePoint> chapterKnowledgePoints = knowledgePointRepository.findByChapter(defaultChapter);
        KnowledgePoint defaultKnowledgePoint;
        if (chapterKnowledgePoints.isEmpty()) {
            System.out.println("章节没有知识点，正在创建默认知识点...");
            defaultKnowledgePoint = new KnowledgePoint();
            defaultKnowledgePoint.setPointName("默认知识点");
            defaultKnowledgePoint.setPointDescription("默认知识点");
            defaultKnowledgePoint.setKeyPoints("默认核心考点");
            defaultKnowledgePoint.setChapter(defaultChapter);
            defaultKnowledgePoint.setLevel(1);
            defaultKnowledgePoint = knowledgePointRepository.save(defaultKnowledgePoint);
            System.out.println("已创建默认知识点，ID：" + defaultKnowledgePoint.getPointId());
        } else {
            defaultKnowledgePoint = chapterKnowledgePoints.get(0);
            System.out.println("使用现有知识点：" + defaultKnowledgePoint.getPointName() + "，ID：" + defaultKnowledgePoint.getPointId());
        }
        
        // 处理知识点和课程关联
        for (Question question : questions) {
            // 关联到数据库原理与应用课程
            question.setCourse(databaseCourse);
            
            // 关联到默认知识点
            question.setKnowledgePoint(defaultKnowledgePoint);
            
            System.out.println("已关联题目到课程：" + databaseCourse.getCourseName() + "，知识点：" + defaultKnowledgePoint.getPointName());
        }
        
        // 保存所有题目
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        System.out.println("已成功保存 " + savedQuestions.size() + " 条题目到数据库");
        
        return savedQuestions;
    }
    
    /**
     * 导入学生信息
     */
    public List<com.tg.ums.entity.base.Student> importStudents(MultipartFile file) throws Exception {
        System.out.println("收到导入学生信息请求，文件名：" + file.getOriginalFilename());
        System.out.println("文件大小：" + file.getSize() + "字节");
        System.out.println("文件类型：" + file.getContentType());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 增强的文件格式校验：同时检查扩展名和ContentType
        boolean isXlsx = file.getOriginalFilename().toLowerCase().endsWith(".xlsx");
        boolean isExcelContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType()) ||
                                     "application/vnd.ms-excel".equals(file.getContentType()) ||
                                     "application/octet-stream".equals(file.getContentType());
                                      
        if (!isXlsx && !isExcelContentType) {
            throw new IllegalArgumentException("文件格式错误，请上传.xlsx格式的Excel文件");
        }

        System.out.println("开始解析学生信息Excel文件...");
        List<com.tg.ums.entity.base.Student> students = ImportUtils.importStudentsFromExcel(file.getInputStream());
        System.out.println("Excel文件解析完成，共解析到 " + students.size() + " 条学生信息");
        
        // 处理专业和届次关联
        for (com.tg.ums.entity.base.Student student : students) {
            // 查找或创建默认专业
            Optional<com.tg.ums.entity.base.Major> majorOptional = majorRepository.findById(1);
            if (majorOptional.isEmpty()) {
                com.tg.ums.entity.base.Major defaultMajor = new com.tg.ums.entity.base.Major();
                defaultMajor.setMajorCode("CS");
                defaultMajor.setMajorName("计算机科学与技术");
                defaultMajor.setMajorCategory("工学");
                defaultMajor = majorRepository.save(defaultMajor);
                student.setMajor(defaultMajor);
                System.out.println("已创建默认专业并关联到学生");
            } else {
                student.setMajor(majorOptional.get());
            }
            
            // 查找或创建默认届次
            Optional<com.tg.ums.entity.base.Batch> batchOptional = batchRepository.findById(1);
            if (batchOptional.isEmpty()) {
                com.tg.ums.entity.base.Batch defaultBatch = new com.tg.ums.entity.base.Batch();
                defaultBatch.setBatchYear(2022);
                defaultBatch = batchRepository.save(defaultBatch);
                student.setBatch(defaultBatch);
                System.out.println("已创建默认届次并关联到学生");
            } else {
                student.setBatch(batchOptional.get());
            }
        }
        
        return studentRepository.saveAll(students);
    }
    
    /**
     * 导入课程数据
     */
    public List<com.tg.ums.entity.course.Course> importCourses(MultipartFile file) throws Exception {
        System.out.println("收到导入课程信息请求，文件名：" + file.getOriginalFilename());
        System.out.println("文件大小：" + file.getSize() + "字节");
        System.out.println("文件类型：" + file.getContentType());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 增强的文件格式校验：同时检查扩展名和ContentType
        boolean isXlsx = file.getOriginalFilename().toLowerCase().endsWith(".xlsx");
        boolean isExcelContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType()) ||
                                     "application/vnd.ms-excel".equals(file.getContentType()) ||
                                     "application/octet-stream".equals(file.getContentType());
                                      
        if (!isXlsx && !isExcelContentType) {
            throw new IllegalArgumentException("文件格式错误，请上传.xlsx格式的Excel文件");
        }

        System.out.println("开始解析课程信息Excel文件...");
        List<com.tg.ums.entity.course.Course> courses = ImportUtils.importCoursesFromExcel(file.getInputStream());
        System.out.println("Excel文件解析完成，共解析到 " + courses.size() + " 条课程信息");
        
        // 保存所有课程
        return courseRepository.saveAll(courses);
    }
    
    /**
     * 从"数据库课程--知识点.xlsx"导入数据库课程和知识点
     */
    public Map<String, Object> importDatabaseCourseAndKnowledgePoints(MultipartFile file) throws Exception {
        System.out.println("收到导入数据库课程和知识点请求，文件名：" + file.getOriginalFilename());
        System.out.println("文件大小：" + file.getSize() + "字节");
        System.out.println("文件类型：" + file.getContentType());
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 增强的文件格式校验：同时检查扩展名和ContentType
        boolean isXlsx = file.getOriginalFilename().toLowerCase().endsWith(".xlsx");
        boolean isExcelContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType()) ||
                                     "application/vnd.ms-excel".equals(file.getContentType()) ||
                                     "application/octet-stream".equals(file.getContentType());
                                      
        if (!isXlsx && !isExcelContentType) {
            throw new IllegalArgumentException("文件格式错误，请上传.xlsx格式的Excel文件");
        }

        System.out.println("开始解析数据库课程和知识点Excel文件...");
        Map<String, Object> importResult = ImportUtils.importDatabaseCourseAndKnowledgePoints(file.getInputStream());
        System.out.println("Excel文件解析完成");
        
        List<com.tg.ums.entity.course.Course> savedCourses = null;
        List<Chapter> savedChapters = null;
        List<KnowledgePoint> savedKnowledgePoints = new ArrayList<>();
        
        try {
            // 保存课程 - 检查是否已存在
            List<com.tg.ums.entity.course.Course> courses = (List<com.tg.ums.entity.course.Course>) importResult.get("courses");
            System.out.println("待保存课程数量：" + courses.size());
            
            // 检查课程是否已存在
            com.tg.ums.entity.course.Course course = courses.get(0);
            Optional<com.tg.ums.entity.course.Course> existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
            
            if (existingCourse.isPresent()) {
                System.out.println("课程已存在，使用现有课程：" + existingCourse.get().getCourseName());
                savedCourses = new ArrayList<>();
                savedCourses.add(existingCourse.get());
            } else {
                System.out.println("课程不存在，创建新课程：" + course.getCourseName());
                savedCourses = courseRepository.saveAll(courses);
            }
            System.out.println("已处理 " + savedCourses.size() + " 条课程数据");
            
            // 保存章节 - 检查是否已存在
            List<Chapter> chaptersFromExcel = (List<Chapter>) importResult.get("chapters");
            System.out.println("Excel解析出章节数量：" + chaptersFromExcel.size());
            
            List<Chapter> chaptersToSave = new ArrayList<>();
            Map<String, Chapter> chapterNameMap = new HashMap<>();
            
            // 检查现有章节
            List<Chapter> existingChapters = chapterRepository.findByCourse(savedCourses.get(0));
            for (Chapter existingChapter : existingChapters) {
                chapterNameMap.put(existingChapter.getChapterName(), existingChapter);
            }
            System.out.println("已存在章节数量：" + chapterNameMap.size() + "，章节名称：" + chapterNameMap.keySet());
            
            // 保存新章节
            for (Chapter chapterFromExcel : chaptersFromExcel) {
                if (!chapterNameMap.containsKey(chapterFromExcel.getChapterName())) {
                    // 章节不存在，保存新章节
                    chapterFromExcel.setCourse(savedCourses.get(0));
                    chaptersToSave.add(chapterFromExcel);
                } else {
                    // 章节已存在，使用现有章节
                    Chapter existingChapter = chapterNameMap.get(chapterFromExcel.getChapterName());
                    chaptersToSave.add(existingChapter);
                }
            }
            
            // 保存章节
            savedChapters = chapterRepository.saveAll(chaptersToSave);
            System.out.println("已保存 " + savedChapters.size() + " 条章节数据");
            
            // 更新章节名称映射
            chapterNameMap.clear();
            for (Chapter savedChapter : savedChapters) {
                chapterNameMap.put(savedChapter.getChapterName(), savedChapter);
            }
            System.out.println("更新后章节名称映射：" + chapterNameMap.keySet());
            
            // 获取所有知识点
            List<KnowledgePoint> excelKnowledgePoints = (List<KnowledgePoint>) importResult.get("knowledgePoints");
            System.out.println("Excel解析出知识点数量：" + excelKnowledgePoints.size());
            
            // 建立知识点名称到对象的映射，用于处理父子关系
            Map<String, KnowledgePoint> knowledgePointMap = new HashMap<>();
            
            // 先保存所有一级知识点，确保它们有正确的章节关联
            List<KnowledgePoint> level1Points = excelKnowledgePoints.stream()
                .filter(kp -> kp.getLevel() == 1)
                .toList();
            
            List<KnowledgePoint> savedLevel1Points = new ArrayList<>();
            for (KnowledgePoint level1Point : level1Points) {
                // 查找对应的章节
                Chapter chapter = chapterNameMap.get(level1Point.getPointName());
                if (chapter != null) {
                    level1Point.setChapter(chapter);
                    level1Point.setChapterId(chapter.getChapterId());
                    level1Point.setParent(null);
                    KnowledgePoint savedLevel1Point = knowledgePointRepository.save(level1Point);
                    savedLevel1Points.add(savedLevel1Point);
                    knowledgePointMap.put(level1Point.getPointName(), savedLevel1Point);
                    System.out.println("已保存一级知识点：" + level1Point.getPointName() + "，关联章节：" + chapter.getChapterName());
                }
            }
            
            // 保存所有非一级知识点，按照层级从小到大排序，确保父级先于子级保存
            List<KnowledgePoint> nonLevel1Points = excelKnowledgePoints.stream()
                .filter(kp -> kp.getLevel() > 1)
                .sorted(Comparator.comparingInt(KnowledgePoint::getLevel))
                .toList();
            
            List<KnowledgePoint> savedNonLevel1Points = new ArrayList<>();
            for (KnowledgePoint nonLevel1Point : nonLevel1Points) {
                // 处理父子关系
                KnowledgePoint parent = null;
                
                // 1. 首先尝试从当前已保存的知识点中查找父知识点
                if (nonLevel1Point.getParent() != null) {
                    // 根据父知识点名称查找
                    parent = knowledgePointMap.get(nonLevel1Point.getParent().getPointName());
                    
                    // 2. 如果找不到，尝试根据完整路径查找
                    if (parent == null) {
                        String[] pathParts = nonLevel1Point.getPointDescription().split(" > ");
                        if (pathParts.length >= nonLevel1Point.getLevel() - 1) {
                            // 获取直接父级的名称
                            String parentName = pathParts[nonLevel1Point.getLevel() - 2];
                            parent = knowledgePointMap.get(parentName);
                            if (parent != null) {
                                System.out.println("根据路径找到父知识点：" + parentName);
                            }
                        }
                    }
                    
                    // 3. 如果仍然找不到，尝试根据完整层级路径查找
                    if (parent == null) {
                        String[] pathParts = nonLevel1Point.getPointDescription().split(" > ");
                        // 构建所有可能的父级路径
                        for (int i = nonLevel1Point.getLevel() - 2; i >= 1; i--) {
                            StringBuilder parentPath = new StringBuilder();
                            for (int j = 0; j <= i; j++) {
                                if (j > 0) parentPath.append(".");
                                parentPath.append(pathParts[j]);
                            }
                            parent = knowledgePointMap.get(parentPath.toString());
                            if (parent != null) {
                                System.out.println("根据层级路径找到父知识点：" + parentPath);
                                break;
                            }
                        }
                    }
                }
                
                // 4. 如果还是找不到，使用最近的已保存的上一级知识点
                if (parent == null) {
                    // 查找已保存的上一级知识点
                    List<KnowledgePoint> higherLevelPoints = savedNonLevel1Points.stream()
                        .filter(kp -> kp.getLevel() == nonLevel1Point.getLevel() - 1)
                        .toList();
                    
                    if (!higherLevelPoints.isEmpty()) {
                        // 使用最近保存的上一级知识点作为父级
                        parent = higherLevelPoints.get(higherLevelPoints.size() - 1);
                        System.out.println("使用最近保存的上一级知识点作为父级：" + parent.getPointName());
                    } else if (!savedLevel1Points.isEmpty()) {
                        // 如果没有上一级知识点，使用第一个一级知识点
                        KnowledgePoint firstLevel1Point = savedLevel1Points.get(0);
                        parent = firstLevel1Point;
                        System.out.println("使用第一个一级知识点作为父级：" + firstLevel1Point.getPointName());
                    } else {
                        System.out.println("无法为知识点 " + nonLevel1Point.getPointName() + " 设置父知识点，跳过该知识点");
                        continue;
                    }
                }
                
                // 设置父知识点和章节
                nonLevel1Point.setParent(parent);
                nonLevel1Point.setChapter(parent.getChapter());
                nonLevel1Point.setChapterId(parent.getChapterId());
                System.out.println("为知识点 " + nonLevel1Point.getPointName() + " 设置父知识点：" + parent.getPointName() + " 和章节：" + parent.getChapter().getChapterName());
                
                // 保存知识点
                KnowledgePoint savedNonLevel1Point = knowledgePointRepository.save(nonLevel1Point);
                savedNonLevel1Points.add(savedNonLevel1Point);
                
                // 添加到知识点映射，使用完整路径作为键，确保唯一性
                String[] pathParts = nonLevel1Point.getPointDescription().split(" > ");
                StringBuilder fullPath = new StringBuilder();
                for (int i = 0; i < nonLevel1Point.getLevel(); i++) {
                    if (i > 0) fullPath.append(".");
                    fullPath.append(pathParts[i]);
                }
                knowledgePointMap.put(fullPath.toString(), savedNonLevel1Point);
                // 同时添加知识点名称作为键，方便查找
                knowledgePointMap.put(savedNonLevel1Point.getPointName(), savedNonLevel1Point);
            }
            
            // 合并所有保存的知识点
            savedKnowledgePoints.addAll(savedLevel1Points);
            savedKnowledgePoints.addAll(savedNonLevel1Points);
            System.out.println("已保存总知识点数量：" + savedKnowledgePoints.size());
            
        } catch (Exception e) {
            System.err.println("保存数据时发生异常：" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("courses", savedCourses);
        result.put("chapters", savedChapters);
        result.put("knowledgePoints", savedKnowledgePoints);
        
        return result;
    }
    
    /**
     * 直接从文件系统导入数据库课程和知识点
     */
    public Map<String, Object> importDatabaseCourseAndKnowledgePointsFromFile(String filePath) throws Exception {
        System.out.println("从文件系统导入，文件路径：" + filePath);
        
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在：" + filePath);
        }
        
        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("文件格式错误，请上传.xlsx格式的Excel文件");
        }
        
        System.out.println("开始解析数据库课程和知识点Excel文件...");
        Map<String, Object> importResult = ImportUtils.importDatabaseCourseAndKnowledgePoints(new FileInputStream(file));
        System.out.println("Excel文件解析完成");
        
        List<com.tg.ums.entity.course.Course> savedCourses = null;
        List<Chapter> savedChapters = null;
        List<KnowledgePoint> savedKnowledgePoints = new ArrayList<>();
        
        try {
            // 保存课程 - 检查是否已存在
            List<com.tg.ums.entity.course.Course> courses = (List<com.tg.ums.entity.course.Course>) importResult.get("courses");
            System.out.println("待保存课程数量：" + courses.size());
            
            // 检查课程是否已存在
            com.tg.ums.entity.course.Course course = courses.get(0);
            Optional<com.tg.ums.entity.course.Course> existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
            
            if (existingCourse.isPresent()) {
                System.out.println("课程已存在，使用现有课程：" + existingCourse.get().getCourseName());
                savedCourses = new ArrayList<>();
                savedCourses.add(existingCourse.get());
            } else {
                System.out.println("课程不存在，创建新课程：" + course.getCourseName());
                savedCourses = courseRepository.saveAll(courses);
            }
            System.out.println("已处理 " + savedCourses.size() + " 条课程数据");
            
            // 保存章节 - 检查是否已存在
            List<Chapter> chaptersFromExcel = (List<Chapter>) importResult.get("chapters");
            System.out.println("Excel解析出章节数量：" + chaptersFromExcel.size());
            
            List<Chapter> chaptersToSave = new ArrayList<>();
            Map<String, Chapter> chapterNameMap = new HashMap<>();
            
            // 检查现有章节
            List<Chapter> existingChapters = chapterRepository.findByCourse(savedCourses.get(0));
            for (Chapter existingChapter : existingChapters) {
                chapterNameMap.put(existingChapter.getChapterName(), existingChapter);
            }
            System.out.println("已存在章节数量：" + chapterNameMap.size() + "，章节名称：" + chapterNameMap.keySet());
            
            // 保存新章节
            for (Chapter chapterFromExcel : chaptersFromExcel) {
                if (!chapterNameMap.containsKey(chapterFromExcel.getChapterName())) {
                    // 章节不存在，保存新章节
                    chapterFromExcel.setCourse(savedCourses.get(0));
                    chaptersToSave.add(chapterFromExcel);
                } else {
                    // 章节已存在，使用现有章节
                    Chapter existingChapter = chapterNameMap.get(chapterFromExcel.getChapterName());
                    chaptersToSave.add(existingChapter);
                }
            }
            
            // 保存章节
            savedChapters = chapterRepository.saveAll(chaptersToSave);
            System.out.println("已保存 " + savedChapters.size() + " 条章节数据");
            
            // 更新章节名称映射
            chapterNameMap.clear();
            for (Chapter savedChapter : savedChapters) {
                chapterNameMap.put(savedChapter.getChapterName(), savedChapter);
            }
            System.out.println("更新后章节名称映射：" + chapterNameMap.keySet());
            
            // 获取所有知识点
            List<KnowledgePoint> excelKnowledgePoints = (List<KnowledgePoint>) importResult.get("knowledgePoints");
            System.out.println("Excel解析出知识点数量：" + excelKnowledgePoints.size());
            
            // 建立知识点名称到对象的映射，用于处理父子关系
            Map<String, KnowledgePoint> knowledgePointMap = new HashMap<>();
            
            // 先保存所有一级知识点，确保它们有正确的章节关联
            List<KnowledgePoint> level1Points = excelKnowledgePoints.stream()
                .filter(kp -> kp.getLevel() == 1)
                .toList();
            
            List<KnowledgePoint> savedLevel1Points = new ArrayList<>();
            for (KnowledgePoint level1Point : level1Points) {
                // 查找对应的章节
                Chapter chapter = chapterNameMap.get(level1Point.getPointName());
                if (chapter != null) {
                    level1Point.setChapter(chapter);
                    level1Point.setChapterId(chapter.getChapterId());
                    level1Point.setParent(null);
                    KnowledgePoint savedLevel1Point = knowledgePointRepository.save(level1Point);
                    savedLevel1Points.add(savedLevel1Point);
                    knowledgePointMap.put(level1Point.getPointName(), savedLevel1Point);
                    System.out.println("已保存一级知识点：" + level1Point.getPointName() + "，关联章节：" + chapter.getChapterName());
                }
            }
            
            // 保存所有非一级知识点，按照层级从小到大排序，确保父级先于子级保存
            List<KnowledgePoint> nonLevel1Points = excelKnowledgePoints.stream()
                .filter(kp -> kp.getLevel() > 1)
                .sorted(Comparator.comparingInt(KnowledgePoint::getLevel))
                .toList();
            
            List<KnowledgePoint> savedNonLevel1Points = new ArrayList<>();
            for (KnowledgePoint nonLevel1Point : nonLevel1Points) {
                // 处理父子关系
                KnowledgePoint parent = null;
                
                // 1. 首先尝试从当前已保存的知识点中查找父知识点
                if (nonLevel1Point.getParent() != null) {
                    // 根据父知识点名称查找
                    parent = knowledgePointMap.get(nonLevel1Point.getParent().getPointName());
                    
                    // 2. 如果找不到，尝试根据完整路径查找
                    if (parent == null) {
                        String[] pathParts = nonLevel1Point.getPointDescription().split(" > ");
                        if (pathParts.length >= nonLevel1Point.getLevel() - 1) {
                            // 获取直接父级的名称
                            String parentName = pathParts[nonLevel1Point.getLevel() - 2];
                            parent = knowledgePointMap.get(parentName);
                            if (parent != null) {
                                System.out.println("根据路径找到父知识点：" + parentName);
                            }
                        }
                    }
                    
                    // 3. 如果仍然找不到，尝试根据完整层级路径查找
                    if (parent == null) {
                        String[] pathParts = nonLevel1Point.getPointDescription().split(" > ");
                        // 构建所有可能的父级路径
                        for (int i = nonLevel1Point.getLevel() - 2; i >= 1; i--) {
                            StringBuilder parentPath = new StringBuilder();
                            for (int j = 0; j <= i; j++) {
                                if (j > 0) parentPath.append(".");
                                parentPath.append(pathParts[j]);
                            }
                            parent = knowledgePointMap.get(parentPath.toString());
                            if (parent != null) {
                                System.out.println("根据层级路径找到父知识点：" + parentPath);
                                break;
                            }
                        }
                    }
                }
                
                // 4. 如果还是找不到，使用最近的已保存的上一级知识点
                if (parent == null) {
                    // 查找已保存的上一级知识点
                    List<KnowledgePoint> higherLevelPoints = savedNonLevel1Points.stream()
                        .filter(kp -> kp.getLevel() == nonLevel1Point.getLevel() - 1)
                        .toList();
                    
                    if (!higherLevelPoints.isEmpty()) {
                        // 使用最近保存的上一级知识点作为父级
                        parent = higherLevelPoints.get(higherLevelPoints.size() - 1);
                        System.out.println("使用最近保存的上一级知识点作为父级：" + parent.getPointName());
                    } else if (!savedLevel1Points.isEmpty()) {
                        // 如果没有上一级知识点，使用第一个一级知识点
                        KnowledgePoint firstLevel1Point = savedLevel1Points.get(0);
                        parent = firstLevel1Point;
                        System.out.println("使用第一个一级知识点作为父级：" + firstLevel1Point.getPointName());
                    } else {
                        System.out.println("无法为知识点 " + nonLevel1Point.getPointName() + " 设置父知识点，跳过该知识点");
                        continue;
                    }
                }
                
                // 设置父知识点和章节
                nonLevel1Point.setParent(parent);
                nonLevel1Point.setChapter(parent.getChapter());
                nonLevel1Point.setChapterId(parent.getChapterId());
                System.out.println("为知识点 " + nonLevel1Point.getPointName() + " 设置父知识点：" + parent.getPointName() + " 和章节：" + parent.getChapter().getChapterName());
                
                // 保存知识点
                KnowledgePoint savedNonLevel1Point = knowledgePointRepository.save(nonLevel1Point);
                savedNonLevel1Points.add(savedNonLevel1Point);
                
                // 添加到知识点映射，使用完整路径作为键，确保唯一性
                String[] pathParts = nonLevel1Point.getPointDescription().split(" > ");
                StringBuilder fullPath = new StringBuilder();
                for (int i = 0; i < nonLevel1Point.getLevel(); i++) {
                    if (i > 0) fullPath.append(".");
                    fullPath.append(pathParts[i]);
                }
                knowledgePointMap.put(fullPath.toString(), savedNonLevel1Point);
                // 同时添加知识点名称作为键，方便查找
                knowledgePointMap.put(savedNonLevel1Point.getPointName(), savedNonLevel1Point);
            }
            
            // 合并所有保存的知识点
            savedKnowledgePoints.addAll(savedLevel1Points);
            savedKnowledgePoints.addAll(savedNonLevel1Points);
            System.out.println("已保存总知识点数量：" + savedKnowledgePoints.size());
            
        } catch (Exception e) {
            System.err.println("保存数据时发生异常：" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("courses", savedCourses);
        result.put("chapters", savedChapters);
        result.put("knowledgePoints", savedKnowledgePoints);
        
        return result;
    }
}