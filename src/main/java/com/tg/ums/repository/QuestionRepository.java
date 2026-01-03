package com.tg.ums.repository;

import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByCourse(Course course);
    List<Question> findByKnowledgePoint(KnowledgePoint knowledgePoint);
    List<Question> findByKnowledgePointIn(Set<KnowledgePoint> knowledgePoints);
    List<Question> findByCourseAndKnowledgePoint(Course course, KnowledgePoint knowledgePoint);
    List<Question> findByIsQuizAvailable(Boolean isQuizAvailable);
    @org.springframework.data.jpa.repository.Query("SELECT q FROM Question q WHERE q.knowledgePoint.chapter = :chapter")
    List<Question> findByKnowledgePointChapter(com.tg.ums.entity.course.Chapter chapter);
    List<Question> findByKnowledgePointAndIsQuizAvailable(KnowledgePoint knowledgePoint, Boolean isQuizAvailable);
    List<Question> findByKnowledgePointInAndIsQuizAvailable(Set<KnowledgePoint> knowledgePoints, Boolean isQuizAvailable);
    List<Question> findByCourse_CourseId(Integer courseId);
    @org.springframework.data.jpa.repository.Query("SELECT q FROM Question q WHERE q.knowledgePoint.chapter.chapterId IN :chapterIds")
    List<Question> findByChapter_ChapterIdIn(List<Integer> chapterIds);
}
