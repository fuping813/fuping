package com.tg.ums.service.score;

import com.tg.ums.dto.ScoreStatisticsDTO;
import com.tg.ums.entity.score.ExamScore;
import com.tg.ums.repository.ExamScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScoreStatisticsService {

    @Autowired
    private ExamScoreService examScoreService;

    @Autowired
    private ExamScoreRepository examScoreRepository;

    /**
     * 获取成绩统计数据
     */
    public List<Map<String, Object>> getScoreStatistics() {
        List<Map<String, Object>> statistics = new ArrayList<>();
        
        // 获取所有考试成绩
        List<ExamScore> allScores = examScoreService.getAllExamScores();
        
        // 按课程分组统计
        Map<Integer, List<ExamScore>> courseScoresMap = new HashMap<>();
        for (ExamScore score : allScores) {
            Integer courseId = score.getExamPaper().getCourse().getCourseId();
            courseScoresMap.computeIfAbsent(courseId, k -> new ArrayList<>()).add(score);
        }
        
        // 计算每门课程的统计数据
        for (Map.Entry<Integer, List<ExamScore>> entry : courseScoresMap.entrySet()) {
            List<ExamScore> courseScores = entry.getValue();
            if (courseScores.isEmpty()) {
                continue;
            }
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("courseName", courseScores.get(0).getExamPaper().getCourse().getCourseName());
            stat.put("totalStudents", courseScores.size());
            
            double totalScore = 0;
            double highestScore = Double.MIN_VALUE;
            double lowestScore = Double.MAX_VALUE;
            int passCount = 0;
            int excellentCount = 0;
            int failCount = 0;
            
            for (ExamScore score : courseScores) {
                double scoreValue = score.getTotalScore().doubleValue();
                totalScore += scoreValue;
                highestScore = Math.max(highestScore, scoreValue);
                lowestScore = Math.min(lowestScore, scoreValue);
                
                if (scoreValue >= 60) {
                    passCount++;
                } else {
                    failCount++;
                }
                
                if (scoreValue >= 90) {
                    excellentCount++;
                }
            }
            
            double averageScore = totalScore / courseScores.size();
            double passRate = (double) passCount / courseScores.size() * 100;
            double excellentRate = (double) excellentCount / courseScores.size() * 100;
            
            stat.put("averageScore", String.format("%.2f", averageScore));
            stat.put("highestScore", String.format("%.2f", highestScore));
            stat.put("lowestScore", String.format("%.2f", lowestScore));
            stat.put("passRate", String.format("%.1f%%", passRate));
            stat.put("excellentRate", String.format("%.1f%%", excellentRate));
            stat.put("failRate", String.format("%.1f%%", 100 - passRate));
            
            statistics.add(stat);
        }
        
        return statistics;
    }

    /**
     * 根据课程ID获取成绩统计数据，用于图表展示
     */
    public ScoreStatisticsDTO getScoreStatisticsByCourseId(Integer courseId) {
        ScoreStatisticsDTO result = new ScoreStatisticsDTO();

        // 1. 计算成绩分布
        List<Integer> scoreDistribution = calculateScoreDistribution(courseId);
        result.setScoreDistribution(scoreDistribution);

        // 2. 生成知识点掌握情况数据（模拟数据）
        List<Map<String, Object>> knowledgePointScores = generateKnowledgePointScores();
        result.setKnowledgePointScores(knowledgePointScores);

        return result;
    }

    /**
     * 计算成绩分布
     */
    private List<Integer> calculateScoreDistribution(Integer courseId) {
        List<Integer> distribution = new ArrayList<>();

        // 初始化分布数组，分别代表：60分以下、60-70分、70-80分、80-90分、90-100分、100分以上
        for (int i = 0; i < 6; i++) {
            distribution.add(0);
        }

        // 获取所有考试成绩
        List<ExamScore> allScores = examScoreService.getAllExamScores();
        
        // 过滤出指定课程的成绩
        List<ExamScore> courseScores = new ArrayList<>();
        for (ExamScore score : allScores) {
            if (score.getExamPaper().getCourse().getCourseId().equals(courseId)) {
                courseScores.add(score);
            }
        }

        // 统计成绩分布
        for (ExamScore score : courseScores) {
            double scoreValue = score.getTotalScore().doubleValue();

            if (scoreValue < 60) {
                distribution.set(0, distribution.get(0) + 1);
            } else if (scoreValue >= 60 && scoreValue < 70) {
                distribution.set(1, distribution.get(1) + 1);
            } else if (scoreValue >= 70 && scoreValue < 80) {
                distribution.set(2, distribution.get(2) + 1);
            } else if (scoreValue >= 80 && scoreValue < 90) {
                distribution.set(3, distribution.get(3) + 1);
            } else if (scoreValue >= 90 && scoreValue < 100) {
                distribution.set(4, distribution.get(4) + 1);
            } else if (scoreValue == 100) {
                distribution.set(5, distribution.get(5) + 1);
            }
        }

        return distribution;
    }

    /**
     * 生成知识点掌握情况数据
     */
    private List<Map<String, Object>> generateKnowledgePointScores() {
        List<Map<String, Object>> knowledgePointScores = new ArrayList<>();

        // 模拟大数据技术课程的知识点数据
        List<String> knowledgePoints = new ArrayList<>();
        knowledgePoints.add("大数据概述");
        knowledgePoints.add("Hadoop生态系统");
        knowledgePoints.add("MapReduce编程");
        knowledgePoints.add("HDFS分布式文件系统");
        knowledgePoints.add("YARN资源管理");
        knowledgePoints.add("Spark核心编程");
        knowledgePoints.add("Spark SQL");
        knowledgePoints.add("数据仓库设计");
        knowledgePoints.add("数据可视化");
        knowledgePoints.add("大数据项目实战");

        // 为每个知识点生成随机平均得分（60-95之间）
        for (String point : knowledgePoints) {
            Map<String, Object> pointScore = new HashMap<>();
            pointScore.put("name", point);
            // 生成60-95之间的随机数
            double score = 60 + Math.random() * 35;
            // 保留一位小数
            score = Math.round(score * 10) / 10.0;
            pointScore.put("score", score);
            knowledgePointScores.add(pointScore);
        }

        return knowledgePointScores;
    }
}