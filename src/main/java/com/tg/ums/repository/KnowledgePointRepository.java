package com.tg.ums.repository;

import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.KnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgePointRepository extends JpaRepository<KnowledgePoint, Integer> {
    @Query("SELECT kp FROM KnowledgePoint kp LEFT JOIN FETCH kp.chapter LEFT JOIN FETCH kp.parent WHERE kp.chapter = :chapter")
    List<KnowledgePoint> findByChapter(Chapter chapter);
    
    @Query("SELECT kp FROM KnowledgePoint kp LEFT JOIN FETCH kp.chapter LEFT JOIN FETCH kp.parent WHERE kp.chapter.chapterId = :chapterId")
    List<KnowledgePoint> findByChapter_ChapterId(Integer chapterId);
    
    @Query("SELECT kp FROM KnowledgePoint kp LEFT JOIN FETCH kp.chapter LEFT JOIN FETCH kp.parent")
    List<KnowledgePoint> findAllWithAssociations();
    
    @Query("SELECT kp FROM KnowledgePoint kp LEFT JOIN FETCH kp.chapter LEFT JOIN FETCH kp.parent WHERE kp.pointId = :pointId")
    java.util.Optional<KnowledgePoint> findByIdWithAssociations(Integer pointId);
    
    java.util.Optional<KnowledgePoint> findByPointName(String pointName);
}
