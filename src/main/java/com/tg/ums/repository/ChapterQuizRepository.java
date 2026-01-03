package com.tg.ums.repository;

import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.question.ChapterQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterQuizRepository extends JpaRepository<ChapterQuiz, Integer> {
    List<ChapterQuiz> findByChapter(Chapter chapter);
}
