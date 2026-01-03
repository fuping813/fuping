package com.tg.ums.repository;

import com.tg.ums.entity.score.StudentAnswerScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerScoreRepository extends JpaRepository<StudentAnswerScore, Integer> {
    List<StudentAnswerScore> findByExamScore_ScoreId(Integer examScoreId);
    List<StudentAnswerScore> findByQuestion_QuestionId(Integer questionId);
    List<StudentAnswerScore> findByQuestion_KnowledgePoint_PointId(Integer knowledgePointId);
}
