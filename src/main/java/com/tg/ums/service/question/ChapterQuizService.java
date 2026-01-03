package com.tg.ums.service.question;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.plan.TeachingPlan;
import com.tg.ums.entity.question.ChapterQuiz;
import com.tg.ums.entity.question.ChapterQuizQuestion;
import com.tg.ums.entity.question.Question;
import com.tg.ums.repository.ChapterQuizQuestionRepository;
import com.tg.ums.repository.ChapterQuizRepository;
import com.tg.ums.repository.ChapterRepository;
import com.tg.ums.repository.KnowledgePointRepository;
import com.tg.ums.repository.QuestionRepository;
import com.tg.ums.repository.TeachingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ChapterQuizService {

    @Autowired
    private ChapterQuizRepository chapterQuizRepository;

    @Autowired
    private ChapterQuizQuestionRepository chapterQuizQuestionRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private TeachingPlanRepository teachingPlanRepository;

    private final Random random = new Random();

    public List<ChapterQuiz> getAllChapterQuizzes() {
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return chapterQuizRepository.findAll();
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
            // 教师只能查看自己参与教学的课程的章节小测
            List<TeachingPlan> teacherPlans = teachingPlanRepository.findByTeachingGroupContaining(username);
            return teacherPlans.stream()
                    .map(TeachingPlan::getCourse)
                    .distinct()
                    .flatMap(course -> {
                        // 获取课程下的所有章节
                        List<Chapter> chapters = chapterRepository.findByCourse(course);
                        // 获取章节下的所有小测
                        return chapters.stream()
                                .flatMap(chapter -> chapterQuizRepository.findByChapter(chapter).stream());
                    })
                    .toList();
        } else {
            // 系统管理员、专业主任可以查看所有章节小测
            return chapterQuizRepository.findAll();
        }
    }

    public Optional<ChapterQuiz> getChapterQuizById(Integer quizId) {
        return chapterQuizRepository.findById(quizId);
    }

    public List<ChapterQuiz> getChapterQuizzesByChapterId(Integer chapterId) {
        // 先获取章节对象，然后查询章节小测
        Chapter chapter = new Chapter();
        chapter.setChapterId(chapterId);
        return chapterQuizRepository.findByChapter(chapter);
    }

    public ChapterQuiz saveChapterQuiz(ChapterQuiz chapterQuiz) {
        return chapterQuizRepository.save(chapterQuiz);
    }

    public void deleteChapterQuiz(Integer quizId) {
        chapterQuizRepository.deleteById(quizId);
    }

    /**
     * 从章节知识点抽取题目生成小测
     * @param chapterId 章节ID
     * @param quizName 小测名称
     * @param questionsPerPoint 每个知识点抽取的题目数量
     * @return 生成的章节小测
     */
    @Transactional
    public ChapterQuiz generateQuizFromChapterKnowledgePoints(Integer chapterId, String quizName, int questionsPerPoint) {
        Optional<Chapter> optionalChapter = chapterRepository.findById(chapterId);
        if (!optionalChapter.isPresent()) {
            throw new RuntimeException("章节不存在");
        }
        Chapter chapter = optionalChapter.get();

        // 获取章节的所有知识点
        List<KnowledgePoint> knowledgePoints = knowledgePointRepository.findByChapter(chapter);
        if (knowledgePoints.isEmpty()) {
            throw new RuntimeException("该章节没有知识点");
        }

        List<Question> selectedQuestions = new ArrayList<>();

        // 从每个知识点中抽取题目
        for (KnowledgePoint point : knowledgePoints) {
            List<Question> pointQuestions = questionRepository.findByKnowledgePointAndIsQuizAvailable(point, true);
            if (!pointQuestions.isEmpty()) {
                // 随机抽取指定数量的题目
                List<Question> shuffledQuestions = new ArrayList<>(pointQuestions);
                shuffledQuestions.sort((q1, q2) -> random.nextInt(3) - 1); // 简单随机排序
                
                int takeCount = Math.min(questionsPerPoint, shuffledQuestions.size());
                selectedQuestions.addAll(shuffledQuestions.subList(0, takeCount));
            }
        }

        if (selectedQuestions.isEmpty()) {
            throw new RuntimeException("没有找到可用的题目");
        }

        // 创建新的章节小测
        ChapterQuiz chapterQuiz = new ChapterQuiz();
        chapterQuiz.setChapter(chapter);
        chapterQuiz.setQuizName(quizName);
        chapterQuiz.setTotalQuestions(selectedQuestions.size());
        
        // 计算总分
        BigDecimal totalScore = selectedQuestions.stream()
                .map(Question::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        chapterQuiz.setTotalScore(totalScore);
        
        // 保存小测
        chapterQuiz = chapterQuizRepository.save(chapterQuiz);

        // 保存小测题目关联
        List<ChapterQuizQuestion> quizQuestions = new ArrayList<>();
        for (Question question : selectedQuestions) {
            ChapterQuizQuestion quizQuestion = new ChapterQuizQuestion();
            quizQuestion.setChapterQuiz(chapterQuiz);
            quizQuestion.setQuestion(question);
            quizQuestions.add(quizQuestion);
        }
        chapterQuizQuestionRepository.saveAll(quizQuestions);

        return chapterQuiz;
    }
}
