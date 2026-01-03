package com.tg.ums.repository;

import com.tg.ums.entity.base.Batch;
import com.tg.ums.entity.base.Major;
import com.tg.ums.entity.base.Semester;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.plan.TeachingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeachingPlanRepository extends JpaRepository<TeachingPlan, Integer> {
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.major = :major AND tp.batch = :batch AND tp.semester = :semester")
    List<TeachingPlan> findByMajorAndBatchAndSemester(Major major, Batch batch, Semester semester);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.major = :major AND tp.batch = :batch")
    List<TeachingPlan> findByMajorAndBatch(Major major, Batch batch);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.major = :major")
    List<TeachingPlan> findByMajor(Major major);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.batch = :batch")
    List<TeachingPlan> findByBatch(Batch batch);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.semester = :semester")
    List<TeachingPlan> findBySemester(Semester semester);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.major = :major AND tp.batch = :batch AND tp.semester = :semester AND tp.course = :course")
    List<TeachingPlan> findByMajorAndBatchAndSemesterAndCourse(Major major, Batch batch, Semester semester, Course course);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.course = :course")
    List<TeachingPlan> findByCourse(Course course);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.teachingGroup LIKE %:keyword%")
    List<TeachingPlan> findByTeachingGroupContaining(String keyword);
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course")
    List<TeachingPlan> findAllWithAssociations();
    
    @Query("SELECT tp FROM TeachingPlan tp LEFT JOIN FETCH tp.major LEFT JOIN FETCH tp.batch LEFT JOIN FETCH tp.semester LEFT JOIN FETCH tp.course WHERE tp.planId = :planId")
    Optional<TeachingPlan> findByIdWithAssociations(Integer planId);
}
