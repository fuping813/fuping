package com.tg.ums.controller.question;

import com.tg.ums.entity.question.Question;
import com.tg.ums.service.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public Map<String, Object> getAllQuestions(
            @RequestParam(value = "courseId", required = false) Integer courseId,
            @RequestParam(value = "chapterId", required = false) Integer chapterId,
            @RequestParam(value = "questionType", required = false) String questionType) {
        System.out.println("收到题目列表查询请求：courseId=" + courseId + ", chapterId=" + chapterId + ", questionType=" + questionType);
        
        List<Question> questions;
        
        // 根据不同的筛选条件调用不同的服务方法
        if (courseId != null && chapterId != null) {
            // 同时根据课程和章节筛选
            System.out.println("根据课程和章节筛选：courseId=" + courseId + ", chapterId=" + chapterId);
            questions = questionService.getQuestionsByChapterId(chapterId);
        } else if (courseId != null) {
            // 只根据课程筛选
            System.out.println("只根据课程筛选：courseId=" + courseId);
            questions = questionService.getQuestionsByCourseId(courseId);
        } else if (chapterId != null) {
            // 只根据章节筛选
            System.out.println("只根据章节筛选：chapterId=" + chapterId);
            questions = questionService.getQuestionsByChapterId(chapterId);
        } else {
            // 没有筛选条件，返回所有题目
            System.out.println("没有筛选条件，返回所有题目");
            questions = questionService.getAllQuestions();
        }
        
        System.out.println("筛选后题目数量：" + questions.size());
        
        // 如果有题型筛选，进一步过滤
        if (questionType != null && !questionType.isEmpty()) {
            System.out.println("根据题型筛选：questionType=" + questionType);
            questions = questions.stream()
                    .filter(q -> questionType.equals(q.getQuestionType()))
                    .toList();
            System.out.println("题型筛选后题目数量：" + questions.size());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questions);
        System.out.println("返回题目列表：" + questions.size() + "个题目");
        return response;
    }

    @GetMapping("/knowledge-point/{knowledgePointId}")
    public Map<String, Object> getQuestionsByKnowledgePointId(@PathVariable Integer knowledgePointId) {
        List<Question> questions = questionService.getQuestionsByKnowledgePointId(knowledgePointId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questions);
        return response;
    }

    @GetMapping("/chapter/{chapterId}")
    public Map<String, Object> getQuestionsByChapterId(@PathVariable Integer chapterId) {
        List<Question> questions = questionService.getQuestionsByChapterId(chapterId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questions);
        return response;
    }

    @GetMapping("/course/{courseId}")
    public Map<String, Object> getQuestionsByCourseId(@PathVariable Integer courseId) {
        List<Question> questions = questionService.getQuestionsByCourseId(courseId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", questions);
        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getQuestionById(@PathVariable Integer id) {
        Question question = questionService.getQuestionById(id).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", question);
        return response;
    }

    @PostMapping
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector') or hasRole('teacher')")
    public Map<String, Object> createQuestion(@RequestBody Question question) {
        Question savedQuestion = questionService.saveQuestion(question);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", savedQuestion);
        return response;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector') or hasRole('teacher')")
    public Map<String, Object> updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
        question.setQuestionId(id);
        Question updatedQuestion = questionService.saveQuestion(question);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", updatedQuestion);
        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector') or hasRole('teacher')")
    public Map<String, Object> deleteQuestion(@PathVariable Integer id) {
        questionService.deleteQuestion(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }
}
