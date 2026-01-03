package com.tg.ums.service.course;

import com.tg.ums.entity.course.Course;
import com.tg.ums.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAllWithAssociations();
    }

    public Optional<Course> getCourseById(Integer courseId) {
        return courseRepository.findById(courseId);
    }

    public Optional<Course> getCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Integer courseId) {
        courseRepository.deleteById(courseId);
    }
}
