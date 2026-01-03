package com.tg.ums.controller.score;

import com.tg.ums.dto.ScoreStatisticsDTO;
import com.tg.ums.service.score.ScoreStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/score-statistics")
public class ScoreStatisticsController {

    @Autowired
    private ScoreStatisticsService scoreStatisticsService;

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> getScoreStatistics(@RequestBody Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取课程ID，默认为2（大数据技术）
            Integer courseId = filters.containsKey("courseId") ? Integer.valueOf(filters.get("courseId").toString()) : 2;
            
            // 计算成绩统计
            ScoreStatisticsDTO statistics = scoreStatisticsService.getScoreStatisticsByCourseId(courseId);
            
            response.put("success", true);
            response.put("message", "获取成绩统计成功");
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取成绩统计失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
