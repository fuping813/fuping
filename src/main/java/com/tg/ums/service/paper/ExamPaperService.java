package com.tg.ums.service.paper;

import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.paper.ExamPaper;
import com.tg.ums.entity.paper.ExamPaperQuestion;
import com.tg.ums.entity.question.Question;
import com.tg.ums.repository.ExamPaperQuestionRepository;
import com.tg.ums.repository.ExamPaperRepository;
import com.tg.ums.repository.KnowledgePointRepository;
import com.tg.ums.repository.QuestionRepository;
import com.tg.ums.service.course.KnowledgePointService;
import com.tg.ums.service.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamPaperService {

    @Autowired
    private ExamPaperRepository examPaperRepository;

    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;

    @Autowired
    private KnowledgePointService knowledgePointService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public List<ExamPaper> getAllExamPapers() {
        return examPaperRepository.findAll();
    }

    public Optional<ExamPaper> getExamPaperById(Integer paperId) {
        return examPaperRepository.findById(paperId);
    }

    public List<ExamPaper> getExamPapersByCourseId(Integer courseId) {
        // 先获取课程对象，然后查询试卷
        Course course = new Course();
        course.setCourseId(courseId);
        return examPaperRepository.findByCourse(course);
    }

    public List<ExamPaper> getExamPapersByChapterId(Integer chapterId) {
        // 先获取章节对象，然后查询试卷
        Chapter chapter = new Chapter();
        chapter.setChapterId(chapterId);
        return examPaperRepository.findByChapter(chapter);
    }

    public List<ExamPaper> getExamPapersByType(String paperType) {
        return examPaperRepository.findByPaperType(paperType);
    }

    public ExamPaper saveExamPaper(ExamPaper examPaper) {
        return examPaperRepository.save(examPaper);
    }

    public void deleteExamPaper(Integer paperId) {
        examPaperRepository.deleteById(paperId);
    }

    /**
     * 生成章节小测试卷
     * @param chapterId 章节ID
     * @param paperName 试卷名称
     * @param totalScore 总分
     * @param questionTypeDistribution 题型分布
     * @return 生成的试卷
     */
    public ExamPaper generateChapterQuizPaper(Integer chapterId, String paperName, Double totalScore, Map<String, Integer> questionTypeDistribution) {
        // 获取章节信息
        Chapter chapter = new Chapter();
        chapter.setChapterId(chapterId);
        
        // 获取章节所属课程
        List<KnowledgePoint> knowledgePoints = knowledgePointService.getKnowledgePointsByChapterId(chapterId);
        if (knowledgePoints.isEmpty()) {
            throw new RuntimeException("该章节下没有知识点，无法生成试卷");
        }
        
        Course course = knowledgePoints.get(0).getChapter().getCourse();
        
        // 收集所有知识点对应的题目 - 使用批量查询优化
        Set<KnowledgePoint> knowledgePointSet = new HashSet<>(knowledgePoints);
        List<Question> allQuizQuestions = questionRepository.findByKnowledgePointInAndIsQuizAvailable(knowledgePointSet, true);
        
        // 按知识点分组
        Map<KnowledgePoint, List<Question>> knowledgePointQuestionsMap = allQuizQuestions.stream()
                .collect(Collectors.groupingBy(Question::getKnowledgePoint));
        
        // 确保所有知识点都在映射中，即使没有题目
        for (KnowledgePoint point : knowledgePoints) {
            knowledgePointQuestionsMap.putIfAbsent(point, new ArrayList<>());
        }
        
        if (knowledgePointQuestionsMap.isEmpty()) {
            throw new RuntimeException("该章节下没有可用于小测的题目，无法生成试卷");
        }
        
        // 检查每个知识点是否至少有2种题型
        for (Map.Entry<KnowledgePoint, List<Question>> entry : knowledgePointQuestionsMap.entrySet()) {
            Set<String> questionTypes = entry.getValue().stream()
                    .map(Question::getQuestionType)
                    .collect(Collectors.toSet());
            if (questionTypes.size() < 2) {
                throw new RuntimeException("知识点\"" + entry.getKey().getPointName() + "\"的题目类型不足2种，无法生成试卷");
            }
        }
        
        // 计算每种题型需要的题目数量
        int totalQuestionsNeeded = questionTypeDistribution.values().stream().mapToInt(Integer::intValue).sum();
        
        // 从每个知识点中随机抽取题目，确保题型多样性
        List<Question> selectedQuestions = new ArrayList<>();
        Map<String, Integer> remainingQuestionsByType = new HashMap<>(questionTypeDistribution);
        
        // 对每个知识点，尝试获取每种题型的题目
        for (Map.Entry<KnowledgePoint, List<Question>> entry : knowledgePointQuestionsMap.entrySet()) {
            KnowledgePoint point = entry.getKey();
            List<Question> pointQuestions = entry.getValue();
            
            // 按题型分组
            Map<String, List<Question>> questionsByType = pointQuestions.stream()
                    .collect(Collectors.groupingBy(Question::getQuestionType));
            
            // 对每种需要的题型，尝试获取题目
            for (Map.Entry<String, Integer> typeEntry : remainingQuestionsByType.entrySet()) {
                String questionType = typeEntry.getKey();
                int remainingCount = typeEntry.getValue();
                
                if (remainingCount > 0 && questionsByType.containsKey(questionType)) {
                    List<Question> typeQuestions = questionsByType.get(questionType);
                    // 随机打乱题目顺序
                    Collections.shuffle(typeQuestions);
                    
                    // 获取需要的数量
                    int takeCount = Math.min(remainingCount, typeQuestions.size());
                    List<Question> takenQuestions = typeQuestions.subList(0, takeCount);
                    
                    selectedQuestions.addAll(takenQuestions);
                    remainingCount -= takeCount;
                    remainingQuestionsByType.put(questionType, remainingCount);
                }
            }
        }
        
        // 检查是否满足题目数量要求
        int selectedCount = selectedQuestions.size();
        if (selectedCount < totalQuestionsNeeded) {
            throw new RuntimeException("可用题目数量不足，无法生成符合要求的试卷");
        }
        
        // 创建试卷
        ExamPaper examPaper = new ExamPaper();
        examPaper.setPaperName(paperName);
        examPaper.setCourse(course);
        examPaper.setPaperType("章节小测");
        examPaper.setChapter(chapter);
        examPaper.setTotalQuestions(selectedQuestions.size());
        examPaper.setTotalScore(BigDecimal.valueOf(totalScore));
        
        // 保存试卷
        ExamPaper savedPaper = examPaperRepository.save(examPaper);
        
        // 计算每题分值
        BigDecimal questionScore = BigDecimal.valueOf(totalScore / selectedQuestions.size());
        
        // 添加试卷题目关联
        for (int i = 0; i < selectedQuestions.size(); i++) {
            ExamPaperQuestion paperQuestion = new ExamPaperQuestion();
            paperQuestion.setExamPaper(savedPaper);
            paperQuestion.setQuestion(selectedQuestions.get(i));
            paperQuestion.setQuestionOrder(i + 1);
            paperQuestion.setScore(questionScore);
            
            examPaperQuestionRepository.save(paperQuestion);
        }
        
        return savedPaper;
    }

    /**
     * 生成通用考试试卷
     * @param courseId 课程ID
     * @param paperName 试卷名称
     * @param totalScore 总分
     * @param knowledgePointScoreDistribution 知识点分值分布
     * @return 生成的试卷
     */
    public ExamPaper generateGeneralExamPaper(Integer courseId, String paperName, Double totalScore, Map<Integer, Double> knowledgePointScoreDistribution) {
        // 获取课程信息
        Course course = new Course();
        course.setCourseId(courseId);
        
        // 验证知识点是否存在且属于该课程
        List<KnowledgePoint> validKnowledgePoints = new ArrayList<>();
        for (Integer pointId : knowledgePointScoreDistribution.keySet()) {
            Optional<KnowledgePoint> pointOptional = knowledgePointService.getKnowledgePointById(pointId);
            if (pointOptional.isPresent()) {
                KnowledgePoint point = pointOptional.get();
                if (point.getChapter().getCourse().getCourseId().equals(courseId)) {
                    validKnowledgePoints.add(point);
                } else {
                    throw new RuntimeException("知识点ID " + pointId + " 不属于指定课程");
                }
            } else {
                throw new RuntimeException("知识点ID " + pointId + " 不存在");
            }
        }
        
        if (validKnowledgePoints.isEmpty()) {
            throw new RuntimeException("没有有效的知识点，无法生成试卷");
        }
        
        // 收集所有知识点对应的题目 - 使用批量查询优化
        Set<KnowledgePoint> validKnowledgePointSet = new HashSet<>(validKnowledgePoints);
        List<Question> allQuestions = questionRepository.findByKnowledgePointIn(validKnowledgePointSet);
        
        // 按知识点分组
        Map<KnowledgePoint, List<Question>> knowledgePointQuestionsMap = allQuestions.stream()
                .collect(Collectors.groupingBy(Question::getKnowledgePoint));
        
        // 验证每个知识点是否都有题目
        for (KnowledgePoint point : validKnowledgePoints) {
            if (!knowledgePointQuestionsMap.containsKey(point) || knowledgePointQuestionsMap.get(point).isEmpty()) {
                throw new RuntimeException("知识点\"" + point.getPointName() + "\"下没有题目，无法生成试卷");
            }
        }
        
        // 从每个知识点中随机抽取题目，根据分值分配
        List<Question> selectedQuestions = new ArrayList<>();
        Map<KnowledgePoint, Double> pointScoreMap = new HashMap<>();
        
        // 创建知识点ID到知识点的映射，避免重复查询
        Map<Integer, KnowledgePoint> knowledgePointIdMap = validKnowledgePoints.stream()
                .collect(Collectors.toMap(KnowledgePoint::getPointId, point -> point));
        
        for (Map.Entry<Integer, Double> entry : knowledgePointScoreDistribution.entrySet()) {
            Integer pointId = entry.getKey();
            Double pointScore = entry.getValue();
            
            KnowledgePoint point = knowledgePointIdMap.get(pointId);
            if (point != null) {
                List<Question> pointQuestions = knowledgePointQuestionsMap.get(point);
                
                if (pointQuestions != null && !pointQuestions.isEmpty()) {
                    // 随机打乱题目顺序
                    Collections.shuffle(pointQuestions);
                    
                    // 计算需要的题目数量
                    double avgQuestionScore = pointQuestions.stream()
                            .map(Question::getScore)
                            .mapToDouble(BigDecimal::doubleValue)
                            .average()
                            .orElse(1.0);
                    
                    int neededCount = (int) Math.round(pointScore / avgQuestionScore);
                    neededCount = Math.max(1, neededCount); // 每个知识点至少1题
                    
                    // 获取需要的数量
                    int takeCount = Math.min(neededCount, pointQuestions.size());
                    List<Question> takenQuestions = pointQuestions.subList(0, takeCount);
                    
                    selectedQuestions.addAll(takenQuestions);
                    pointScoreMap.put(point, pointScore);
                }
            }
        }
        
        // 创建试卷
        ExamPaper examPaper = new ExamPaper();
        examPaper.setPaperName(paperName);
        examPaper.setCourse(course);
        examPaper.setPaperType("通用考试");
        examPaper.setChapter(null);
        examPaper.setTotalQuestions(selectedQuestions.size());
        examPaper.setTotalScore(BigDecimal.valueOf(totalScore));
        
        // 保存试卷
        ExamPaper savedPaper = examPaperRepository.save(examPaper);
        
        // 计算每题分值，按知识点分配
        int questionOrder = 1;
        for (KnowledgePoint point : pointScoreMap.keySet()) {
            double pointScore = pointScoreMap.get(point);
            
            // 找到该知识点的题目
            List<Question> pointQuestions = selectedQuestions.stream()
                    .filter(q -> q.getKnowledgePoint().getPointId().equals(point.getPointId()))
                    .collect(Collectors.toList());
            
            if (!pointQuestions.isEmpty()) {
                BigDecimal questionScore = BigDecimal.valueOf(pointScore / pointQuestions.size());
                
                for (Question question : pointQuestions) {
                    ExamPaperQuestion paperQuestion = new ExamPaperQuestion();
                    paperQuestion.setExamPaper(savedPaper);
                    paperQuestion.setQuestion(question);
                    paperQuestion.setQuestionOrder(questionOrder++);
                    paperQuestion.setScore(questionScore);
                    
                    examPaperQuestionRepository.save(paperQuestion);
                }
            }
        }
        
        return savedPaper;
    }
}
