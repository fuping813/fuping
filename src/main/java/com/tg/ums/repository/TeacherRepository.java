package com.tg.ums.repository;

import com.tg.ums.entity.base.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Teacher findByTeacherCode(String teacherCode);
    Teacher findByTeacherName(String teacherName);
}
