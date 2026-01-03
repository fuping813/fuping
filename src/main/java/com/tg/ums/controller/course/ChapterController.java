package com.tg.ums.controller.course;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.service.course.ChapterService;
import com.tg.ums.service.course.KnowledgePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private KnowledgePointService knowledgePointService;

    @GetMapping
    public List<Chapter> getAllChapters() {
        return chapterService.getAllChapters();
    }

    @GetMapping("/course/{courseId}")
    public List<Chapter> getChaptersByCourseId(@PathVariable Integer courseId) {
        return chapterService.getChaptersByCourseId(courseId);
    }

    @GetMapping("/{id}")
    public Chapter getChapterById(@PathVariable Integer id) {
        return chapterService.getChapterById(id).orElseThrow();
    }

    @PostMapping
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public Chapter createChapter(@RequestBody Chapter chapter) {
        return chapterService.saveChapter(chapter);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public Chapter updateChapter(@PathVariable Integer id, @RequestBody Chapter chapter) {
        chapter.setChapterId(id);
        return chapterService.saveChapter(chapter);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public void deleteChapter(@PathVariable Integer id) {
        chapterService.deleteChapter(id);
    }

    /**
     * 调整章节顺序
     */
    @PutMapping("/adjust-order")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public void adjustChapterOrder(@RequestBody List<Map<String, Integer>> chapterOrders) {
        chapterService.updateChapterOrders(chapterOrders);
    }

    /**
     * 按课程ID获取章节树形结构（包含知识点）
     */
    @GetMapping("/tree/course/{courseId}")
    public List<Map<String, Object>> getChapterTreeByCourseId(@PathVariable Integer courseId) {
        // 获取课程下的所有章节
        List<Chapter> chapters = chapterService.getChaptersByCourseId(courseId);
        
        // 构建章节树形结构
        List<Map<String, Object>> tree = new ArrayList<>();
        for (Chapter chapter : chapters) {
            Map<String, Object> chapterNode = new HashMap<>();
            chapterNode.put("id", chapter.getChapterId());
            chapterNode.put("name", chapter.getChapterName());
            chapterNode.put("type", "chapter");
            chapterNode.put("description", chapter.getChapterDescription());
            chapterNode.put("order", chapter.getChapterOrder());
            
            // 获取章节下的所有知识点
            List<KnowledgePoint> knowledgePoints = knowledgePointService.getKnowledgePointsByChapterId(chapter.getChapterId());
            List<Map<String, Object>> pointNodes = new ArrayList<>();
            for (KnowledgePoint point : knowledgePoints) {
                Map<String, Object> pointNode = new HashMap<>();
                pointNode.put("id", point.getPointId());
                pointNode.put("name", point.getPointName());
                pointNode.put("type", "knowledgePoint");
                pointNode.put("description", point.getPointDescription());
                pointNode.put("keyPoints", point.getKeyPoints());
                pointNodes.add(pointNode);
            }
            
            chapterNode.put("children", pointNodes);
            tree.add(chapterNode);
        }
        
        // 按章节顺序排序
        tree.sort(Comparator.comparingInt(node -> (Integer) node.get("order")));
        
        return tree;
    }
}
