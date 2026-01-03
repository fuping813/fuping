package com.tg.ums.repository;

import com.tg.ums.entity.base.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Integer> {
    Batch findByBatchYear(Integer batchYear);
}
