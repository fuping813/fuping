package com.tg.ums.repository;

import com.tg.ums.entity.base.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findByStudentCode(String studentCode);
}
