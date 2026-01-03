package com.tg.ums.controller.paper;

import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.paper.ExamPaper;
import com.tg.ums.entity.paper.ExamPaperQuestion;
import com.tg.ums.entity.question.Question;
import com.tg.ums.repository.ChapterRepository;
import com.tg.ums.repository.ExamPaperQuestionRepository;
import com.tg.ums.repository.QuestionRepository;
import com.tg.ums.service.paper.ExamPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exam-papers")
public class ExamPaperController {

    @Autowired
    private ExamPaperService examPaperService;
    
    @Autowired
    private ChapterRepository chapterRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;

    @GetMapping
    public List<ExamPaper> getAllExamPapers() {
        return examPaperService.getAllExamPapers();
    }

    @GetMapping("/chapter/{chapterId}")
    public List<ExamPaper> getExamPapersByChapterId(@PathVariable Integer chapterId) {
        return examPaperService.getExamPapersByChapterId(chapterId);
    }

    @GetMapping("/course/{courseId}")
    public List<ExamPaper> getExamPapersByCourseId(@PathVariable Integer courseId) {
        return examPaperService.getExamPapersByCourseId(courseId);
    }

    @GetMapping("/{id}")
    public ExamPaper getExamPaperById(@PathVariable Integer id) {
        return examPaperService.getExamPaperById(id).orElseThrow();
    }

    @PostMapping
    public ExamPaper createExamPaper(@RequestBody ExamPaper examPaper) {
        return examPaperService.saveExamPaper(examPaper);
    }

    @PutMapping("/{id}")
    public ExamPaper updateExamPaper(@PathVariable Integer id, @RequestBody ExamPaper examPaper) {
        examPaper.setPaperId(id);
        return examPaperService.saveExamPaper(examPaper);
    }

    @DeleteMapping("/{id}")
    public void deleteExamPaper(@PathVariable Integer id) {
        examPaperService.deleteExamPaper(id);
    }
    
    /**
     * 生成试卷API
     */
    @PostMapping("/generate")
    public Map<String, Object> generateExamPaper(@RequestBody Map<String, Object> paperData) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 从请求中获取参数
            String paperName = (String) paperData.get("paperName");
            Integer courseId = (Integer) paperData.get("courseId");
            List<Integer> chapterIds = (List<Integer>) paperData.get("chapterIds");
            Integer questionCount = (Integer) paperData.get("questionCount");
            
            // 验证参数
            if (courseId == null) {
                throw new IllegalArgumentException("课程ID不能为空");
            }
            if (questionCount == null || questionCount <= 0) {
                throw new IllegalArgumentException("题目数量必须大于0");
            }
            
            // 直接查询所有题目，不进行严格验证
            List<Question> allQuestions;
            if (chapterIds != null && !chapterIds.isEmpty()) {
                // 如果有章节ID，查询指定章节的题目
                allQuestions = questionRepository.findByChapter_ChapterIdIn(chapterIds);
            } else {
                // 如果没有章节ID，查询指定课程的题目
                allQuestions = questionRepository.findByCourse_CourseId(courseId);
            }
            
            if (allQuestions.isEmpty()) {
                response.put("success", false);
                response.put("message", "没有找到符合条件的题目，无法生成试卷");
                return response;
            }
            
            // 验证题目数量是否足够
            if (allQuestions.size() < questionCount) {
                response.put("success", false);
                response.put("message", "可用题目数量不足，只有" + allQuestions.size() + "题，需要" + questionCount + "题");
                return response;
            }
            
            // 随机打乱题目顺序
            Collections.shuffle(allQuestions);
            
            // 选择指定数量的题目
            List<Question> selectedQuestions = allQuestions.subList(0, questionCount);
            
            // 创建试卷
            ExamPaper examPaper = new ExamPaper();
            examPaper.setPaperName(paperName);
            
            // 设置课程信息
            Course course = new Course();
            course.setCourseId(courseId);
            examPaper.setCourse(course);
            examPaper.setPaperType("通用考试");
            examPaper.setTotalQuestions(selectedQuestions.size());
            examPaper.setTotalScore(BigDecimal.valueOf(100.0));
            
            // 保存试卷
            ExamPaper savedPaper = examPaperService.saveExamPaper(examPaper);
            
            // 计算每题分值
            BigDecimal questionScore = BigDecimal.valueOf(100.0 / selectedQuestions.size());
            
            // 添加试卷题目关联
            for (int i = 0; i < selectedQuestions.size(); i++) {
                ExamPaperQuestion paperQuestion = new ExamPaperQuestion();
                paperQuestion.setExamPaper(savedPaper);
                paperQuestion.setQuestion(selectedQuestions.get(i));
                paperQuestion.setQuestionOrder(i + 1);
                paperQuestion.setScore(questionScore);
                
                examPaperQuestionRepository.save(paperQuestion);
            }
            
            // 构建成功响应，包括关联的题目信息
            Map<String, Object> responsePaperData = new HashMap<>();
            responsePaperData.put("paperId", savedPaper.getPaperId());
            responsePaperData.put("paperName", savedPaper.getPaperName());
            responsePaperData.put("course", savedPaper.getCourse());
            responsePaperData.put("paperType", savedPaper.getPaperType());
            responsePaperData.put("totalQuestions", savedPaper.getTotalQuestions());
            responsePaperData.put("totalScore", savedPaper.getTotalScore());
            responsePaperData.put("createTime", savedPaper.getCreateTime());
            
            // 添加题目列表
            List<Map<String, Object>> questionsList = new ArrayList<>();
            for (int i = 0; i < selectedQuestions.size(); i++) {
                Question question = selectedQuestions.get(i);
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("questionId", question.getQuestionId());
                questionData.put("questionContent", question.getQuestionContent());
                questionData.put("questionType", question.getQuestionType());
                questionData.put("options", question.getOptions());
                questionData.put("correctAnswer", question.getCorrectAnswer());
                questionData.put("difficultyLevel", question.getDifficultyLevel());
                questionData.put("analysis", question.getAnalysis());
                questionsList.add(questionData);
            }
            responsePaperData.put("questions", questionsList);
            
            response.put("success", true);
            response.put("message", "试卷生成成功");
            response.put("data", responsePaperData);
            return response;
        } catch (Exception e) {
            // 构建失败响应
            response.put("success", false);
            response.put("message", "试卷生成失败: " + e.getMessage());
            e.printStackTrace(); // 添加日志以便调试
            return response;
        }
    }
}
