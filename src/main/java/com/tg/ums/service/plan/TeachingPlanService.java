package com.tg.ums.service.plan;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.base.Batch;
import com.tg.ums.entity.base.Major;
import com.tg.ums.entity.base.Semester;
import com.tg.ums.entity.base.User;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.plan.TeachingPlan;
import com.tg.ums.repository.TeachingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TeachingPlanService {

    @Autowired
    private TeachingPlanRepository teachingPlanRepository;

    public List<TeachingPlan> getAllTeachingPlans() {
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return teachingPlanRepository.findAllWithAssociations();
        }
        
        String username = authentication.getName();
        String role = "";
        
        // 获取用户角色
        if (authentication.getAuthorities() != null) {
            for (var authority : authentication.getAuthorities()) {
                if (authority.getAuthority().startsWith("ROLE_")) {
                    role = authority.getAuthority().substring(5); // 去掉ROLE_前缀
                    break;
                }
            }
        }
        
        // 根据角色返回不同的数据
        if (RoleConstants.TEACHER.equals(role)) {
            // 教师只能查看自己参与教学的计划
            // 假设teachingGroup字段包含教师的用户名或姓名
            return teachingPlanRepository.findByTeachingGroupContaining(username);
        } else if (RoleConstants.MAJOR_DIRECTOR.equals(role)) {
            // 专业主任可以查看所有教学计划
            // TODO: 未来可以根据专业主任所在专业过滤
            return teachingPlanRepository.findAllWithAssociations();
        } else {
            // 系统管理员或其他角色可以查看所有教学计划
            return teachingPlanRepository.findAllWithAssociations();
        }
    }

    public Optional<TeachingPlan> getTeachingPlanById(Integer planId) {
        return teachingPlanRepository.findByIdWithAssociations(planId);
    }

    public List<TeachingPlan> getTeachingPlansByMajorAndBatchAndSemester(
            Integer majorId, Integer batchId, Integer semesterId) {
        // 先获取相关对象
        Major major = new Major();
        major.setMajorId(majorId);
        Batch batch = new Batch();
        batch.setBatchId(batchId);
        Semester semester = new Semester();
        semester.setSemesterId(semesterId);
        
        return teachingPlanRepository.findByMajorAndBatchAndSemester(major, batch, semester);
    }

    public TeachingPlan saveTeachingPlan(TeachingPlan teachingPlan) {
        if (teachingPlan.getPlanId() == null) {
            // 新增教学计划
            teachingPlan.setVersion(1);
            teachingPlan.setIsArchived(false);
        } else {
            // 更新教学计划，版本号+1
            Optional<TeachingPlan> existingPlan = teachingPlanRepository.findById(teachingPlan.getPlanId());
            if (existingPlan.isPresent()) {
                teachingPlan.setVersion(existingPlan.get().getVersion() + 1);
            }
        }
        return teachingPlanRepository.save(teachingPlan);
    }

    public void deleteTeachingPlan(Integer planId) {
        teachingPlanRepository.deleteById(planId);
    }

    public List<TeachingPlan> getTeachingPlansByConditions(Integer majorId, Integer batchId, Integer semesterId, Integer courseId) {
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = "";
        String username;
        
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
            // 获取用户角色
            if (authentication.getAuthorities() != null) {
                for (var authority : authentication.getAuthorities()) {
                    if (authority.getAuthority().startsWith("ROLE_")) {
                        role = authority.getAuthority().substring(5); // 去掉ROLE_前缀
                        break;
                    }
                }
            }
        } else {
            username = "";
        }
        
        List<TeachingPlan> result;
        
        // 根据条件查询
        if (majorId != null && batchId != null && semesterId != null && courseId != null) {
            Major major = new Major();
            major.setMajorId(majorId);
            Batch batch = new Batch();
            batch.setBatchId(batchId);
            Semester semester = new Semester();
            semester.setSemesterId(semesterId);
            Course course = new Course();
            course.setCourseId(courseId);
            result = teachingPlanRepository.findByMajorAndBatchAndSemesterAndCourse(major, batch, semester, course);
        } else if (majorId != null && batchId != null && semesterId != null) {
            Major major = new Major();
            major.setMajorId(majorId);
            Batch batch = new Batch();
            batch.setBatchId(batchId);
            Semester semester = new Semester();
            semester.setSemesterId(semesterId);
            result = teachingPlanRepository.findByMajorAndBatchAndSemester(major, batch, semester);
        } else if (majorId != null && batchId != null) {
            Major major = new Major();
            major.setMajorId(majorId);
            Batch batch = new Batch();
            batch.setBatchId(batchId);
            result = teachingPlanRepository.findByMajorAndBatch(major, batch);
        } else if (majorId != null) {
            Major major = new Major();
            major.setMajorId(majorId);
            result = teachingPlanRepository.findByMajor(major);
        } else if (batchId != null) {
            Batch batch = new Batch();
            batch.setBatchId(batchId);
            result = teachingPlanRepository.findByBatch(batch);
        } else if (semesterId != null) {
            Semester semester = new Semester();
            semester.setSemesterId(semesterId);
            result = teachingPlanRepository.findBySemester(semester);
        } else if (courseId != null) {
            Course course = new Course();
            course.setCourseId(courseId);
            result = teachingPlanRepository.findByCourse(course);
        } else {
            result = teachingPlanRepository.findAllWithAssociations();
        }
        
        // 如果是教师角色，需要进一步过滤数据
        if (RoleConstants.TEACHER.equals(role)) {
            return result.stream()
                    .filter(plan -> plan.getTeachingGroup() != null && plan.getTeachingGroup().contains(username))
                    .toList();
        }
        
        return result;
    }
    
    /**
     * 归档教学计划
     */
    public TeachingPlan archiveTeachingPlan(Integer planId) {
        Optional<TeachingPlan> existingPlan = teachingPlanRepository.findById(planId);
        if (existingPlan.isPresent()) {
            TeachingPlan teachingPlan = existingPlan.get();
            teachingPlan.setIsArchived(true);
            teachingPlan.setArchiveTime(new Date());
            teachingPlan.setPlanStatus("已归档");
            return teachingPlanRepository.save(teachingPlan);
        }
        return null;
    }
    
    /**
     * 作废教学计划
     */
    public TeachingPlan invalidateTeachingPlan(Integer planId) {
        Optional<TeachingPlan> existingPlan = teachingPlanRepository.findById(planId);
        if (existingPlan.isPresent()) {
            TeachingPlan teachingPlan = existingPlan.get();
            teachingPlan.setPlanStatus("已作废");
            return teachingPlanRepository.save(teachingPlan);
        }
        return null;
    }
    
    /**
     * 获取教学计划统计信息
     */
    public Map<String, Object> getTeachingPlanStatistics(Integer majorId, Integer batchId, Integer semesterId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取指定条件的教学计划
        List<TeachingPlan> teachingPlans = getTeachingPlansByConditions(majorId, batchId, semesterId, null);
        
        // 计算总学分
        BigDecimal totalCredits = BigDecimal.ZERO;
        // 计算总学时
        int totalHours = 0;
        // 计算必修课数量
        int requiredCourseCount = 0;
        // 计算选修课数量
        int electiveCourseCount = 0;
        
        for (TeachingPlan plan : teachingPlans) {
            Course course = plan.getCourse();
            totalCredits = totalCredits.add(course.getCredits());
            totalHours += course.getTotalHours();
            
            if ("必修".equals(course.getCourseType())) {
                requiredCourseCount++;
            } else if ("选修".equals(course.getCourseType())) {
                electiveCourseCount++;
            }
        }
        
        // 计算必修课占比
        double requiredCourseRatio = 0.0;
        if (teachingPlans.size() > 0) {
            requiredCourseRatio = (double) requiredCourseCount / teachingPlans.size() * 100;
        }
        
        // 构建统计结果
        statistics.put("totalCourses", teachingPlans.size());
        statistics.put("totalCredits", totalCredits);
        statistics.put("totalHours", totalHours);
        statistics.put("requiredCourseCount", requiredCourseCount);
        statistics.put("electiveCourseCount", electiveCourseCount);
        statistics.put("requiredCourseRatio", Math.round(requiredCourseRatio * 100) / 100.0);
        
        return statistics;
    }
}
