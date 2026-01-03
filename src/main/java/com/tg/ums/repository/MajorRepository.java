package com.tg.ums.repository;

import com.tg.ums.entity.base.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, Integer> {
    Major findByMajorCode(String majorCode);
    Major findByMajorName(String majorName);
}
