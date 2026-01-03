package com.tg.ums.service.score;

import com.tg.ums.entity.score.StudentAnswerScore;
import com.tg.ums.repository.StudentAnswerScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentAnswerScoreService {

    @Autowired
    private StudentAnswerScoreRepository studentAnswerScoreRepository;

    @Autowired
    private KnowledgePointScoreStatService knowledgePointScoreStatService;

    public List<StudentAnswerScore> getAllStudentAnswerScores() {
        return studentAnswerScoreRepository.findAll();
    }

    public Optional<StudentAnswerScore> getStudentAnswerScoreById(Integer id) {
        return studentAnswerScoreRepository.findById(id);
    }

    public List<StudentAnswerScore> getStudentAnswerScoresByScoreId(Integer scoreId) {
        return studentAnswerScoreRepository.findByExamScore_ScoreId(scoreId);
    }

    public List<StudentAnswerScore> getStudentAnswerScoresByQuestionId(Integer questionId) {
        return studentAnswerScoreRepository.findByQuestion_QuestionId(questionId);
    }

    public List<StudentAnswerScore> getStudentAnswerScoresByKnowledgePointId(Integer knowledgePointId) {
        return studentAnswerScoreRepository.findByQuestion_KnowledgePoint_PointId(knowledgePointId);
    }

    public StudentAnswerScore saveStudentAnswerScore(StudentAnswerScore studentAnswerScore) {
        StudentAnswerScore savedScore = studentAnswerScoreRepository.save(studentAnswerScore);
        // 自动更新知识点得分统计
        knowledgePointScoreStatService.updateStatsWhenScoreUpdated(savedScore.getExamScore().getScoreId());
        return savedScore;
    }

    public void deleteStudentAnswerScore(Integer id) {
        studentAnswerScoreRepository.deleteById(id);
    }
}
