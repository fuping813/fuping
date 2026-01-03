package com.tg.ums.controller.base;

import com.tg.ums.entity.base.Major;
import com.tg.ums.service.base.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/majors")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMajors() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Major> majors = majorService.getAllMajors();
            response.put("success", true);
            response.put("message", "获取专业列表成功");
            response.put("data", majors);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取专业列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMajorById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Major major = majorService.getMajorById(id).orElseThrow(() -> new RuntimeException("专业不存在"));
            response.put("success", true);
            response.put("message", "获取专业成功");
            response.put("data", major);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取专业失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createMajor(@RequestBody Major major) {
        Map<String, Object> response = new HashMap<>();
        try {
            Major savedMajor = majorService.saveMajor(major);
            response.put("success", true);
            response.put("message", "创建专业成功");
            response.put("data", savedMajor);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建专业失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMajor(@PathVariable Integer id, @RequestBody Major major) {
        Map<String, Object> response = new HashMap<>();
        try {
            major.setMajorId(id);
            Major updatedMajor = majorService.saveMajor(major);
            response.put("success", true);
            response.put("message", "更新专业成功");
            response.put("data", updatedMajor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新专业失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMajor(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            majorService.deleteMajor(id);
            response.put("success", true);
            response.put("message", "删除专业成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除专业失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
