package com.tg.ums.service.score;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.paper.ExamPaper;
import com.tg.ums.entity.paper.ExamPaperQuestion;
import com.tg.ums.entity.plan.TeachingPlan;
import com.tg.ums.entity.question.Question;
import com.tg.ums.entity.score.ExamScore;
import com.tg.ums.entity.score.KnowledgePointScoreStat;
import com.tg.ums.entity.score.StudentAnswerScore;
import com.tg.ums.repository.ExamPaperQuestionRepository;
import com.tg.ums.repository.ExamPaperRepository;
import com.tg.ums.repository.ExamScoreRepository;
import com.tg.ums.repository.KnowledgePointScoreStatRepository;
import com.tg.ums.repository.QuestionRepository;
import com.tg.ums.repository.StudentAnswerScoreRepository;
import com.tg.ums.repository.TeachingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KnowledgePointScoreStatService {

    @Autowired
    private KnowledgePointScoreStatRepository knowledgePointScoreStatRepository;

    @Autowired
    private ExamScoreRepository examScoreRepository;

    @Autowired
    private StudentAnswerScoreRepository studentAnswerScoreRepository;

    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamPaperRepository examPaperRepository;
    
    @Autowired
    private TeachingPlanRepository teachingPlanRepository;

    public List<KnowledgePointScoreStat> getAllKnowledgePointScoreStats() {
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return knowledgePointScoreStatRepository.findAll();
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
            // 教师只能查看自己参与教学的课程的知识点得分统计
            List<TeachingPlan> teacherPlans = teachingPlanRepository.findByTeachingGroupContaining(username);
            return teacherPlans.stream()
                    .map(TeachingPlan::getCourse)
                    .distinct()
                    .flatMap(course -> {
                        // 获取课程下的所有知识点得分统计
                        return knowledgePointScoreStatRepository.findByExamPaper_Course(course).stream();
                    })
                    .toList();
        } else if (RoleConstants.MAJOR_DIRECTOR.equals(role)) {
            // 专业主任可以查看本专业的所有知识点得分统计
            // 假设专业主任的用户名与专业相关，这里需要根据实际情况调整
            // 例如：专业主任的用户名可能包含专业代码
            return knowledgePointScoreStatRepository.findAll(); // 暂时返回所有，需要根据专业过滤
        } else {
            // 系统管理员可以查看所有知识点得分统计
            return knowledgePointScoreStatRepository.findAll();
        }
    }

    public Optional<KnowledgePointScoreStat> getKnowledgePointScoreStatById(Integer id) {
        return knowledgePointScoreStatRepository.findById(id);
    }

    public List<KnowledgePointScoreStat> getKnowledgePointScoreStatsByPaperId(Integer paperId) {
        return knowledgePointScoreStatRepository.findByExamPaper_PaperId(paperId);
    }

    public List<KnowledgePointScoreStat> getKnowledgePointScoreStatsByKnowledgePointId(Integer knowledgePointId) {
        return knowledgePointScoreStatRepository.findByKnowledgePoint_PointId(knowledgePointId);
    }

    public List<KnowledgePointScoreStat> getKnowledgePointScoreStatsByChapterId(Integer chapterId) {
        return knowledgePointScoreStatRepository.findByKnowledgePoint_Chapter_ChapterId(chapterId);
    }

    public KnowledgePointScoreStat saveKnowledgePointScoreStat(KnowledgePointScoreStat knowledgePointScoreStat) {
        return knowledgePointScoreStatRepository.save(knowledgePointScoreStat);
    }

    public void deleteKnowledgePointScoreStat(Integer id) {
        knowledgePointScoreStatRepository.deleteById(id);
    }

    /**
     * 计算并更新指定试卷的知识点得分统计
     * @param paperId 试卷ID
     */
    public List<KnowledgePointScoreStat> calculateKnowledgePointStats(Integer paperId) {
        // 验证试卷是否存在
        Optional<ExamPaper> paperOptional = examPaperRepository.findById(paperId);
        if (paperOptional.isEmpty()) {
            throw new RuntimeException("试卷不存在");
        }
        ExamPaper paper = paperOptional.get();

        // 获取试卷包含的知识点
        List<ExamPaperQuestion> paperQuestions = examPaperQuestionRepository.findByExamPaper_PaperId(paper.getPaperId());
        if (paperQuestions.isEmpty()) {
            throw new RuntimeException("试卷没有题目");
        }

        // 按知识点分组题目
        Map<KnowledgePoint, List<ExamPaperQuestion>> knowledgePointQuestionsMap = new HashMap<>();
        for (ExamPaperQuestion paperQuestion : paperQuestions) {
            Question question = paperQuestion.getQuestion();
            KnowledgePoint knowledgePoint = question.getKnowledgePoint();
            knowledgePointQuestionsMap.computeIfAbsent(knowledgePoint, k -> new ArrayList<>()).add(paperQuestion);
        }

        // 获取所有参加考试的学生成绩
        List<ExamScore> examScores = examScoreRepository.findByExamPaper_PaperId(paper.getPaperId());
        int participatingCount = examScores.size();

        // 计算每个知识点的得分统计
        List<KnowledgePointScoreStat> stats = new ArrayList<>();
        for (Map.Entry<KnowledgePoint, List<ExamPaperQuestion>> entry : knowledgePointQuestionsMap.entrySet()) {
            KnowledgePoint knowledgePoint = entry.getKey();
            List<ExamPaperQuestion> pointQuestions = entry.getValue();

            // 计算该知识点的总分（试卷中该知识点所有题目的总分）
            java.math.BigDecimal pointTotalScore = pointQuestions.stream()
                    .map(ExamPaperQuestion::getScore)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

            // 计算所有学生在该知识点上的得分总和
            java.math.BigDecimal totalObtainedScore = java.math.BigDecimal.ZERO;
            for (ExamScore examScore : examScores) {
                // 获取该学生在该知识点所有题目上的得分
            List<StudentAnswerScore> answerScores = studentAnswerScoreRepository.findByExamScore_ScoreId(examScore.getScoreId());
                for (StudentAnswerScore answerScore : answerScores) {
                    Question question = answerScore.getQuestion();
                    if (question.getKnowledgePoint().getPointId().equals(knowledgePoint.getPointId())) {
                        totalObtainedScore = totalObtainedScore.add(answerScore.getObtainedScore());
                    }
                }
            }

            // 计算平均分和得分率
            java.math.BigDecimal averageScore;
            if (participatingCount > 0) {
                averageScore = totalObtainedScore.divide(java.math.BigDecimal.valueOf(participatingCount), 2, java.math.BigDecimal.ROUND_HALF_UP);
            } else {
                averageScore = java.math.BigDecimal.ZERO;
            }

            java.math.BigDecimal scoreRate;
            if (pointTotalScore.compareTo(java.math.BigDecimal.ZERO) > 0 && participatingCount > 0) {
                java.math.BigDecimal denominator = pointTotalScore.multiply(java.math.BigDecimal.valueOf(participatingCount));
                scoreRate = totalObtainedScore.divide(denominator, 4, java.math.BigDecimal.ROUND_HALF_UP).multiply(java.math.BigDecimal.valueOf(100));
            } else {
                scoreRate = java.math.BigDecimal.ZERO;
            }

            // 创建或更新知识点得分统计
            Optional<KnowledgePointScoreStat> existingStatOptional = knowledgePointScoreStatRepository.findByExamPaper_PaperIdAndKnowledgePoint_PointId(paperId, knowledgePoint.getPointId());
            KnowledgePointScoreStat stat;
            if (existingStatOptional.isPresent()) {
                stat = existingStatOptional.get();
            } else {
                stat = new KnowledgePointScoreStat();
                stat.setExamPaper(paper);
                stat.setKnowledgePoint(knowledgePoint);
            }

            stat.setParticipatingCount(participatingCount);
            stat.setTotalScore(totalObtainedScore);
            stat.setAverageScore(averageScore);
            stat.setScoreRate(scoreRate);

            stats.add(knowledgePointScoreStatRepository.save(stat));
        }

        return stats;
    }

    /**
     * 当学生得分更新时，更新知识点得分统计
     * @param scoreId 考试成绩ID
     */
    public void updateStatsWhenScoreUpdated(Integer scoreId) {
        Optional<ExamScore> examScoreOptional = examScoreRepository.findById(scoreId);
        if (examScoreOptional.isPresent()) {
            ExamScore examScore = examScoreOptional.get();
            calculateKnowledgePointStats(examScore.getExamPaper().getPaperId());
        }
    }

    /**
     * 批量导入学生得分后更新知识点得分统计
     * @param paperId 试卷ID
     */
    public void updateStatsAfterBatchImport(Integer paperId) {
        calculateKnowledgePointStats(paperId);
    }
}
