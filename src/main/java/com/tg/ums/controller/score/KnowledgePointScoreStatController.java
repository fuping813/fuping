package com.tg.ums.controller.score;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.score.KnowledgePointScoreStat;
import com.tg.ums.service.score.KnowledgePointScoreStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-point-score-stats")
public class KnowledgePointScoreStatController {

    @Autowired
    private KnowledgePointScoreStatService knowledgePointScoreStatService;

    @GetMapping
    public List<KnowledgePointScoreStat> getAllKnowledgePointScoreStats() {
        return knowledgePointScoreStatService.getAllKnowledgePointScoreStats();
    }

    @GetMapping("/knowledge-point/{knowledgePointId}")
    public List<KnowledgePointScoreStat> getKnowledgePointScoreStatsByKnowledgePointId(@PathVariable Integer knowledgePointId) {
        return knowledgePointScoreStatService.getKnowledgePointScoreStatsByKnowledgePointId(knowledgePointId);
    }

    @GetMapping("/paper/{paperId}")
    public List<KnowledgePointScoreStat> getKnowledgePointScoreStatsByPaperId(@PathVariable Integer paperId) {
        return knowledgePointScoreStatService.getKnowledgePointScoreStatsByPaperId(paperId);
    }

    @GetMapping("/chapter/{chapterId}")
    public List<KnowledgePointScoreStat> getKnowledgePointScoreStatsByChapterId(@PathVariable Integer chapterId) {
        return knowledgePointScoreStatService.getKnowledgePointScoreStatsByChapterId(chapterId);
    }

    @GetMapping("/{id}")
    public KnowledgePointScoreStat getKnowledgePointScoreStatById(@PathVariable Integer id) {
        return knowledgePointScoreStatService.getKnowledgePointScoreStatById(id).orElseThrow();
    }

    @PostMapping
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public KnowledgePointScoreStat createKnowledgePointScoreStat(@RequestBody KnowledgePointScoreStat knowledgePointScoreStat) {
        return knowledgePointScoreStatService.saveKnowledgePointScoreStat(knowledgePointScoreStat);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public KnowledgePointScoreStat updateKnowledgePointScoreStat(@PathVariable Integer id, @RequestBody KnowledgePointScoreStat knowledgePointScoreStat) {
        knowledgePointScoreStat.setStatId(id);
        return knowledgePointScoreStatService.saveKnowledgePointScoreStat(knowledgePointScoreStat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public void deleteKnowledgePointScoreStat(@PathVariable Integer id) {
        knowledgePointScoreStatService.deleteKnowledgePointScoreStat(id);
    }

    @PostMapping("/calculate/{paperId}")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public List<KnowledgePointScoreStat> calculateKnowledgePointStats(@PathVariable Integer paperId) {
        return knowledgePointScoreStatService.calculateKnowledgePointStats(paperId);
    }

    @PostMapping("/update-stats/{scoreId}")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public void updateStatsWhenScoreUpdated(@PathVariable Integer scoreId) {
        knowledgePointScoreStatService.updateStatsWhenScoreUpdated(scoreId);
    }

    @PostMapping("/update-stats/batch/{paperId}")
    @PreAuthorize("hasRole(T(com.tg.ums.common.RoleConstants).SYSTEM_ADMIN)")
    public void updateStatsAfterBatchImport(@PathVariable Integer paperId) {
        knowledgePointScoreStatService.updateStatsAfterBatchImport(paperId);
    }
}
