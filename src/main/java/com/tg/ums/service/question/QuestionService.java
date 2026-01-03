package com.tg.ums.service.question;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.plan.TeachingPlan;
import com.tg.ums.entity.question.Question;
import com.tg.ums.repository.QuestionRepository;
import com.tg.ums.repository.TeachingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private TeachingPlanRepository teachingPlanRepository;

    public List<Question> getAllQuestions() {
        System.out.println("调用getAllQuestions方法");
        
        // 先尝试直接返回所有题目，不考虑权限，用于调试
        List<Question> allQuestions = questionRepository.findAll();
        System.out.println("数据库中共有题目：" + allQuestions.size() + "个");
        
        return allQuestions;
        
        /*
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return questionRepository.findAll();
        }
        
        String username = authentication.getName();
        String role = "";
        
        // 获取用户角色
        if (authentication.getAuthorities() != null) {
            for (var authority : authentication.getAuthorities()) {
                if (authority.getAuthority().startsWith("ROLE_")) {
                    role = authority.getAuthority().substring(5); // 去掉ROLE_前缀
                    break;
                }
            }
        }
        
        // 根据角色返回不同的数据
        if (RoleConstants.TEACHER.equals(role)) {
            // 教师只能查看自己参与教学的课程的题目
            List<TeachingPlan> teacherPlans = teachingPlanRepository.findByTeachingGroupContaining(username);
            return teacherPlans.stream()
                    .map(TeachingPlan::getCourse)
                    .distinct()
                    .flatMap(course -> {
                        // 获取课程下的所有题目
                        return questionRepository.findByCourse(course).stream();
                    })
                    .toList();
        } else {
            // 系统管理员、专业主任可以查看所有题目
            return questionRepository.findAll();
        }
        */
    }

    public Optional<Question> getQuestionById(Integer questionId) {
        return questionRepository.findById(questionId);
    }

    public List<Question> getQuestionsByCourseId(Integer courseId) {
        // 先获取课程对象，然后查询题目
        com.tg.ums.entity.course.Course course = new com.tg.ums.entity.course.Course();
        course.setCourseId(courseId);
        return questionRepository.findByCourse(course);
    }

    public List<Question> getQuestionsByKnowledgePointId(Integer knowledgePointId) {
        // 先获取知识点对象，然后查询题目
        com.tg.ums.entity.course.KnowledgePoint knowledgePoint = new com.tg.ums.entity.course.KnowledgePoint();
        knowledgePoint.setPointId(knowledgePointId);
        return questionRepository.findByKnowledgePoint(knowledgePoint);
    }

    public List<Question> getQuestionsByChapterId(Integer chapterId) {
        // 先获取章节对象，然后查询题目
        com.tg.ums.entity.course.Chapter chapter = new com.tg.ums.entity.course.Chapter();
        chapter.setChapterId(chapterId);
        return questionRepository.findByKnowledgePointChapter(chapter);
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public void deleteQuestion(Integer questionId) {
        questionRepository.deleteById(questionId);
    }
}
