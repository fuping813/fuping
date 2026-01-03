package com.tg.ums.repository;

import com.tg.ums.entity.score.KnowledgePointScoreStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgePointScoreStatRepository extends JpaRepository<KnowledgePointScoreStat, Integer> {
    List<KnowledgePointScoreStat> findByExamPaper_PaperId(Integer examPaperId);
    List<KnowledgePointScoreStat> findByKnowledgePoint_PointId(Integer knowledgePointId);
    List<KnowledgePointScoreStat> findByKnowledgePoint_Chapter_ChapterId(Integer chapterId);
    Optional<KnowledgePointScoreStat> findByExamPaper_PaperIdAndKnowledgePoint_PointId(Integer paperId, Integer knowledgePointId);
    List<KnowledgePointScoreStat> findByExamPaper_Course(com.tg.ums.entity.course.Course course);
}
