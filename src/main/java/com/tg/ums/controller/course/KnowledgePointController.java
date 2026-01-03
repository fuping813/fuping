package com.tg.ums.controller.course;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.repository.ChapterRepository;
import com.tg.ums.service.course.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/knowledge-points")
public class KnowledgePointController {

    @Autowired
    private KnowledgePointService knowledgePointService;
    
    @Autowired
    private ChapterRepository chapterRepository;

    @GetMapping
    public List<KnowledgePoint> getAllKnowledgePoints(@RequestParam(required = false) Integer courseId, @RequestParam(required = false) Integer chapterId) {
        if (courseId != null && chapterId != null) {
            // 同时有courseId和chapterId，先检查chapter是否属于该course
            Optional<Chapter> chapterOptional = chapterRepository.findByIdWithCourse(chapterId);
            if (chapterOptional.isPresent() && chapterOptional.get().getCourse().getCourseId().equals(courseId)) {
                return knowledgePointService.getKnowledgePointsByChapterId(chapterId);
            } else {
                return new ArrayList<>();
            }
        }
        if (courseId != null) {
            return knowledgePointService.getKnowledgePointsByCourseId(courseId);
        }
        if (chapterId != null) {
            return knowledgePointService.getKnowledgePointsByChapterId(chapterId);
        }
        return knowledgePointService.getAllKnowledgePoints();
    }
    
    /**
     * 获取带有层级关系的知识点树
     */
    @GetMapping("/tree")
    public List<KnowledgePoint> getKnowledgePointTree(@RequestParam Integer courseId) {
        return knowledgePointService.getKnowledgePointTreeByCourseId(courseId);
    }

    @GetMapping("/chapter/{chapterId}")
    public List<KnowledgePoint> getKnowledgePointsByChapterId(@PathVariable Integer chapterId) {
        return knowledgePointService.getKnowledgePointsByChapterId(chapterId);
    }

    @GetMapping("/{id}")
    public KnowledgePoint getKnowledgePointById(@PathVariable Integer id) {
        return knowledgePointService.getKnowledgePointById(id).orElseThrow();
    }

    @PostMapping
    public KnowledgePoint createKnowledgePoint(@RequestBody KnowledgePoint knowledgePoint) {
        return knowledgePointService.saveKnowledgePoint(knowledgePoint);
    }

    @PutMapping("/{id}")
    public KnowledgePoint updateKnowledgePoint(@PathVariable Integer id, @RequestBody KnowledgePoint knowledgePoint) {
        knowledgePoint.setPointId(id);
        return knowledgePointService.saveKnowledgePoint(knowledgePoint);
    }

    @DeleteMapping("/{id}")
    public void deleteKnowledgePoint(@PathVariable Integer id) {
        knowledgePointService.deleteKnowledgePoint(id);
    }
}
