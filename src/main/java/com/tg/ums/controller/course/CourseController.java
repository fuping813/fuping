package com.tg.ums.controller.course;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Course;
import com.tg.ums.service.course.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Course> courses = courseService.getAllCourses();
            response.put("success", true);
            response.put("message", "获取课程列表成功");
            response.put("data", courses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Course course = courseService.getCourseById(id).orElseThrow(() -> new RuntimeException("课程不存在"));
            response.put("success", true);
            response.put("message", "获取课程成功");
            response.put("data", course);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Map<String, Object>> getCourseByCode(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        try {
            Course course = courseService.getCourseByCode(code).orElse(null);
            response.put("success", true);
            response.put("message", "获取课程成功");
            response.put("data", course);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();
        try {
            Course savedCourse = courseService.saveCourse(course);
            response.put("success", true);
            response.put("message", "课程添加成功");
            response.put("data", savedCourse);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "课程添加失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        Map<String, Object> response = new HashMap<>();
        try {
            course.setCourseId(id);
            Course updatedCourse = courseService.saveCourse(course);
            response.put("success", true);
            response.put("message", "课程更新成功");
            response.put("data", updatedCourse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "课程更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            courseService.deleteCourse(id);
            response.put("success", true);
            response.put("message", "课程删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "课程删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
