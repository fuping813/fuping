package com.tg.ums.repository;

import com.tg.ums.entity.paper.ExamPaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperQuestionRepository extends JpaRepository<ExamPaperQuestion, Integer> {
    List<ExamPaperQuestion> findByExamPaper_PaperId(Integer examPaperId);
}
