package com.tg.ums.controller;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.question.Question;
import com.tg.ums.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    /**
     * 导入知识点数据
     */
    @PostMapping("/knowledge-points")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public ResponseEntity<?> importKnowledgePoints(@RequestParam("file") MultipartFile file) {
        try {
            List<KnowledgePoint> importedKnowledgePoints = importService.importKnowledgePoints(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(importedKnowledgePoints);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("导入失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败：" + e.getMessage());
        }
    }

    /**
     * 导入试题信息
     */
    @PostMapping("/questions")
    // @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public ResponseEntity<?> importQuestions(@RequestParam("file") MultipartFile file) {
        try {
            List<Question> importedQuestions = importService.importQuestions(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(importedQuestions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("导入失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败：" + e.getMessage());
        }
    }

    /**
     * 导入学生信息
     */
    @PostMapping("/students")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public ResponseEntity<?> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            List<com.tg.ums.entity.base.Student> importedStudents = importService.importStudents(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(importedStudents);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("导入失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败：" + e.getMessage());
        }
    }

    /**
     * 导入成绩信息
     */
    @PostMapping("/scores")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public ResponseEntity<?> importScores(@RequestParam("file") MultipartFile file) {
        try {
            // TODO: 实现成绩信息导入功能
            return ResponseEntity.status(HttpStatus.CREATED).body("成绩信息导入功能待实现");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("导入失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败：" + e.getMessage());
        }
    }
    
    /**
     * 导入课程数据
     */
    @PostMapping("/courses")
    // @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public ResponseEntity<?> importCourses(@RequestParam("file") MultipartFile file) {
        try {
            List<com.tg.ums.entity.course.Course> importedCourses = importService.importCourses(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(importedCourses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("导入失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败：" + e.getMessage());
        }
    }
    
    /**
     * 导入数据库课程和知识点数据
     */
    @PostMapping("/database-course")
    // @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public ResponseEntity<?> importDatabaseCourseAndKnowledgePoints(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> importResult = importService.importDatabaseCourseAndKnowledgePoints(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(importResult);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("导入失败：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入失败：" + e.getMessage());
        }
    }
}