package com.tg.ums.repository;

import com.tg.ums.entity.plan.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Integer> {

    /**
     * 根据专业ID和届次ID查找培养方案
     */
    Optional<TrainingProgram> findByMajor_MajorIdAndBatch_BatchId(Integer majorId, Integer batchId);

    /**
     * 根据专业ID查找培养方案列表
     */
    List<TrainingProgram> findByMajor_MajorId(Integer majorId);

    /**
     * 根据届次ID查找培养方案列表
     */
    List<TrainingProgram> findByBatch_BatchId(Integer batchId);

    /**
     * 根据状态查找培养方案列表
     */
    List<TrainingProgram> findByProgramStatus(String programStatus);

    /**
     * 根据专业ID和状态查找培养方案列表
     */
    List<TrainingProgram> findByMajor_MajorIdAndProgramStatus(Integer majorId, String programStatus);
}