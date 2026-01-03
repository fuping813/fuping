package com.tg.ums.service.course;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.plan.TeachingPlan;
import com.tg.ums.entity.question.ChapterQuiz;
import com.tg.ums.entity.question.ChapterQuizQuestion;
import com.tg.ums.entity.question.Question;
import com.tg.ums.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ChapterService {

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Autowired
    private ChapterQuizRepository chapterQuizRepository;

    @Autowired
    private ChapterQuizQuestionRepository chapterQuizQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private TeachingPlanRepository teachingPlanRepository;

    public List<Chapter> getAllChapters() {
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return chapterRepository.findAll();
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
            // 教师只能查看自己参与教学的课程的章节
            List<TeachingPlan> teacherPlans = teachingPlanRepository.findByTeachingGroupContaining(username);
            return teacherPlans.stream()
                    .map(TeachingPlan::getCourse)
                    .distinct()
                    .flatMap(course -> chapterRepository.findByCourse(course).stream())
                    .toList();
        } else {
            // 系统管理员、专业主任可以查看所有章节
            return chapterRepository.findAll();
        }
    }

    public Optional<Chapter> getChapterById(Integer chapterId) {
        return chapterRepository.findById(chapterId);
    }

    public List<Chapter> getChaptersByCourseId(Integer courseId) {
        // 直接通过课程ID查询章节，提高效率
        return chapterRepository.findByCourse_CourseId(courseId);
    }

    public Chapter saveChapter(Chapter chapter) {
        // 如果只提供了courseId，而没有提供course对象，从数据库中查询course对象
        if (chapter.getCourse() != null && chapter.getCourse().getCourseId() != null) {
            // 这里假设Course对象只有courseId被设置，其他字段为空
            // 我们需要从数据库中获取完整的Course对象
            Optional<Course> courseOptional = courseRepository.findById(chapter.getCourse().getCourseId());
            if (courseOptional.isPresent()) {
                chapter.setCourse(courseOptional.get());
            }
        }
        return chapterRepository.save(chapter);
    }

    /**
     * 删除章节，并级联删除相关知识点、题目和小测数据
     * 使用事务确保数据一致性
     */
    @Transactional
    public void deleteChapter(Integer chapterId) {
        // 1. 获取章节对象
        Optional<Chapter> chapterOpt = chapterRepository.findById(chapterId);
        if (chapterOpt.isPresent()) {
            Chapter chapter = chapterOpt.get();
            
            // 2. 获取章节下的所有知识点
            List<KnowledgePoint> knowledgePoints = knowledgePointRepository.findByChapter(chapter);
            
            // 3. 获取章节下的所有小测
            List<ChapterQuiz> chapterQuizzes = chapterQuizRepository.findByChapter(chapter);
            
            // 4. 处理小测题目关联（避免外键约束）
            for (ChapterQuiz quiz : chapterQuizzes) {
                List<ChapterQuizQuestion> quizQuestions = chapterQuizQuestionRepository.findByChapterQuiz(quiz);
                chapterQuizQuestionRepository.deleteAll(quizQuestions);
            }
            
            // 5. 删除章节小测
            chapterQuizRepository.deleteAll(chapterQuizzes);
            
            // 6. 处理知识点下的题目（避免外键约束）
            for (KnowledgePoint point : knowledgePoints) {
                // 注意：这里需要QuestionRepository提供按知识点查询的方法
                // 由于当前Question实体没有直接关联Chapter，而是关联KnowledgePoint
                // 所以需要先获取知识点下的所有题目并删除
                // 这里假设已经有findByKnowledgePoint方法
                List<Question> questions = questionRepository.findByKnowledgePoint(point);
                questionRepository.deleteAll(questions);
            }
            
            // 7. 删除知识点
            knowledgePointRepository.deleteAll(knowledgePoints);
            
            // 8. 删除章节
            chapterRepository.delete(chapter);
        }
    }
    
    /**
     * 批量更新章节顺序，使用事务确保数据一致性
     */
    @Transactional
    public void updateChapterOrders(List<Map<String, Integer>> chapterOrders) {
        for (Map<String, Integer> orderMap : chapterOrders) {
            Integer chapterId = orderMap.get("chapterId");
            Integer chapterOrder = orderMap.get("chapterOrder");
            if (chapterId != null && chapterOrder != null) {
                Optional<Chapter> chapterOpt = chapterRepository.findById(chapterId);
                if (chapterOpt.isPresent()) {
                    Chapter chapter = chapterOpt.get();
                    chapter.setChapterOrder(chapterOrder);
                    chapterRepository.save(chapter);
                }
            }
        }
    }
}
