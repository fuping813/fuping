package com.tg.ums.service.score;

import com.tg.ums.entity.score.ExamScore;
import com.tg.ums.repository.ExamScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamScoreService {

    @Autowired
    private ExamScoreRepository examScoreRepository;

    @Autowired
    private KnowledgePointScoreStatService knowledgePointScoreStatService;

    public List<ExamScore> getAllExamScores() {
        return examScoreRepository.findAll();
    }

    public Optional<ExamScore> getExamScoreById(Integer scoreId) {
        return examScoreRepository.findById(scoreId);
    }

    public List<ExamScore> getExamScoresByPaperId(Integer paperId) {
        return examScoreRepository.findByExamPaper_PaperId(paperId);
    }

    public List<ExamScore> getExamScoresByStudentId(Integer studentId) {
        return examScoreRepository.findByStudent_StudentId(studentId);
    }

    public ExamScore saveExamScore(ExamScore examScore) {
        ExamScore savedScore = examScoreRepository.save(examScore);
        // 自动更新知识点得分统计
        knowledgePointScoreStatService.updateStatsWhenScoreUpdated(savedScore.getScoreId());
        return savedScore;
    }

    public void deleteExamScore(Integer scoreId) {
        examScoreRepository.deleteById(scoreId);
    }
}
