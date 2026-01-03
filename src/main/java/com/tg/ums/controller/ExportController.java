package com.tg.ums.controller;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.service.course.ChapterService;
import com.tg.ums.service.course.CourseService;
import com.tg.ums.service.course.KnowledgePointService;
import com.tg.ums.service.score.ScoreStatisticsService;
import com.tg.ums.utils.ExportUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private KnowledgePointService knowledgePointService;

    @Autowired
    private ScoreStatisticsService scoreStatisticsService;

    /**
     * 导出课程目录为Excel文件
     */
    @GetMapping("/course-catalog")
    public void exportCourseCatalog(HttpServletResponse response) throws Exception {
        // 获取所有课程
        List<Course> courses = courseService.getAllCourses();
        // 获取所有章节
        List<Chapter> chapters = chapterService.getAllChapters();
        // 获取所有知识点
        List<KnowledgePoint> knowledgePoints = knowledgePointService.getAllKnowledgePoints();
        
        byte[] excelBytes = ExportUtils.exportCourseCatalogToExcel(courses, chapters, knowledgePoints);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=course_catalog.xlsx");
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }

    /**
     * 导出成绩统计为Excel文件
     */
    @GetMapping("/score-statistics")
    public void exportScoreStatistics(HttpServletResponse response) throws Exception {
        // 获取成绩统计数据
        List<Map<String, Object>> statistics = scoreStatisticsService.getScoreStatistics();
        
        byte[] excelBytes = ExportUtils.exportScoreStatisticsToExcel(statistics);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=score_statistics.xlsx");
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }
}
