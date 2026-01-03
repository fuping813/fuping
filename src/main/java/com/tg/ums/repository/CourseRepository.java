package com.tg.ums.repository;

import com.tg.ums.entity.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByCourseCode(String courseCode);
    Optional<Course> findByCourseName(String courseName);
    
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Course c LEFT JOIN FETCH c.major")
    List<Course> findAllWithAssociations();
}
