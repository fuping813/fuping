package com.tg.ums.repository;

import com.tg.ums.entity.question.ChapterQuiz;
import com.tg.ums.entity.question.ChapterQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterQuizQuestionRepository extends JpaRepository<ChapterQuizQuestion, Integer> {
    List<ChapterQuizQuestion> findByChapterQuiz(ChapterQuiz chapterQuiz);
}
