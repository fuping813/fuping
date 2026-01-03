package com.tg.ums.repository;

import com.tg.ums.entity.score.ExamScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamScoreRepository extends JpaRepository<ExamScore, Integer> {
    List<ExamScore> findByExamPaper_PaperId(Integer examPaperId);
    List<ExamScore> findByStudent_StudentId(Integer studentId);
}
