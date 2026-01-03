package com.tg.ums.repository;

import com.tg.ums.entity.base.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    Semester findBySemesterCode(String semesterCode);
}
