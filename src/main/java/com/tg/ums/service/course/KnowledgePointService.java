package com.tg.ums.service.course;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.plan.TeachingPlan;
import com.tg.ums.repository.ChapterRepository;
import com.tg.ums.repository.KnowledgePointRepository;
import com.tg.ums.repository.TeachingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KnowledgePointService {

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;
    
    @Autowired
    private TeachingPlanRepository teachingPlanRepository;
    
    @Autowired
    private ChapterRepository chapterRepository;

    public List<KnowledgePoint> getAllKnowledgePoints() {
        // 直接返回所有知识点，不进行角色过滤，方便调试
        return knowledgePointRepository.findAllWithAssociations();
    }
    
    /**
     * 获取带有层级关系的知识点树
     * 按课程ID查询
     */
    public List<KnowledgePoint> getKnowledgePointTreeByCourseId(Integer courseId) {
        // 获取课程下的所有章节
        List<Chapter> chapters = chapterRepository.findByCourse_CourseId(courseId);
        // 获取所有知识点
        List<KnowledgePoint> allKnowledgePoints = chapters.stream()
                .flatMap(chapter -> knowledgePointRepository.findByChapter(chapter).stream())
                .toList();
        
        // 构建知识点树
        return buildKnowledgePointTree(allKnowledgePoints);
    }
    
    /**
     * 构建知识点树结构
     */
    private List<KnowledgePoint> buildKnowledgePointTree(List<KnowledgePoint> allKnowledgePoints) {
        // 将所有知识点按父ID分组
        Map<Integer, List<KnowledgePoint>> childrenMap = new HashMap<>();
        List<KnowledgePoint> rootPoints = new ArrayList<>();
        
        for (KnowledgePoint point : allKnowledgePoints) {
            if (point.getParent() == null) {
                // 根节点（一级知识点）
                rootPoints.add(point);
            } else {
                // 子节点，按父ID分组
                Integer parentId = point.getParent().getPointId();
                childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>())
                        .add(point);
            }
        }
        
        // 递归构建树结构
        for (KnowledgePoint rootPoint : rootPoints) {
            buildChildren(rootPoint, childrenMap);
        }
        
        return rootPoints;
    }
    
    /**
     * 递归构建子节点
     */
    private void buildChildren(KnowledgePoint parentPoint, Map<Integer, List<KnowledgePoint>> childrenMap) {
        List<KnowledgePoint> children = childrenMap.get(parentPoint.getPointId());
        if (children != null && !children.isEmpty()) {
            parentPoint.setChildren(children);
            for (KnowledgePoint child : children) {
                buildChildren(child, childrenMap);
            }
        }
    }

    public Optional<KnowledgePoint> getKnowledgePointById(Integer knowledgePointId) {
        return knowledgePointRepository.findByIdWithAssociations(knowledgePointId);
    }

    public List<KnowledgePoint> getKnowledgePointsByChapterId(Integer chapterId) {
        // 直接通过章节ID查询知识点，提高效率
        return knowledgePointRepository.findByChapter_ChapterId(chapterId);
    }
    
    public List<KnowledgePoint> getKnowledgePointsByCourseId(Integer courseId) {
        // 先获取课程下的所有章节
        List<Chapter> chapters = chapterRepository.findByCourse_CourseId(courseId);
        // 然后获取章节下的所有知识点
        List<KnowledgePoint> allKnowledgePoints = chapters.stream()
                .flatMap(chapter -> knowledgePointRepository.findByChapter(chapter).stream())
                .toList();
        
        // 返回所有知识点，包括多级层级结构
        return allKnowledgePoints;
    }

    public KnowledgePoint saveKnowledgePoint(KnowledgePoint knowledgePoint) {
        // 如果只提供了chapterId，而没有提供chapter对象，从数据库中查询chapter对象
        if (knowledgePoint.getChapter() == null && knowledgePoint.getChapterId() != null) {
            Optional<Chapter> chapterOptional = chapterRepository.findById(knowledgePoint.getChapterId());
            if (chapterOptional.isPresent()) {
                knowledgePoint.setChapter(chapterOptional.get());
            }
        }
        
        // 处理父知识点关系
        if (knowledgePoint.getParentId() != null) {
            // 如果level为1，重置父知识点
            if (knowledgePoint.getLevel() == 1) {
                knowledgePoint.setParent(null);
                knowledgePoint.setParentId(null);
            } else {
                // 查询父知识点对象，使用fetch加载关联数据
                Optional<KnowledgePoint> parentOptional = knowledgePointRepository.findByIdWithAssociations(knowledgePoint.getParentId());
                if (parentOptional.isPresent()) {
                    knowledgePoint.setParent(parentOptional.get());
                } else {
                    // 如果父知识点不存在，重置为null
                    knowledgePoint.setParent(null);
                    knowledgePoint.setParentId(null);
                }
            }
        } else {
            // 如果parentId为null，确保parent对象也为null
            knowledgePoint.setParent(null);
        }
        
        return knowledgePointRepository.save(knowledgePoint);
    }

    /**
     * 删除知识点，并级联删除相关题目和子知识点
     * 使用事务确保数据一致性
     */
    @Transactional
    public void deleteKnowledgePoint(Integer knowledgePointId) {
        // 1. 获取要删除的知识点及其关联数据
        Optional<KnowledgePoint> knowledgePointOptional = knowledgePointRepository.findByIdWithAssociations(knowledgePointId);
        if (knowledgePointOptional.isPresent()) {
            KnowledgePoint knowledgePoint = knowledgePointOptional.get();
            
            // 2. 直接删除知识点，依赖JPA的级联删除机制
            // JPA会自动处理以下级联删除：
            // - 子知识点（通过@OneToMany(cascade = CascadeType.REMOVE)）
            // - 关联的题目（通过@OneToMany(cascade = CascadeType.REMOVE)）
            knowledgePointRepository.delete(knowledgePoint);
        }
    }
}
