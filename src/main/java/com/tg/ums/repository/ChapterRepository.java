package com.tg.ums.repository;

import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByCourse(Course course);
    Optional<Chapter> findByChapterNameAndCourse_CourseId(String chapterName, Integer courseId);
    List<Chapter> findByCourse_CourseId(Integer courseId);
    
    @Query("SELECT c FROM Chapter c LEFT JOIN FETCH c.course WHERE c.chapterId = :chapterId")
    Optional<Chapter> findByIdWithCourse(Integer chapterId);
}
