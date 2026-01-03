package com.tg.ums.repository;

import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.paper.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, Integer> {
    List<ExamPaper> findByCourse(Course course);
    List<ExamPaper> findByChapter(Chapter chapter);
    List<ExamPaper> findByPaperType(String paperType);
}
