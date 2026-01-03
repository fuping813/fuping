package com.tg.ums.controller.plan;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.plan.TeachingPlan;
import com.tg.ums.service.plan.TeachingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teaching-plans")
public class TeachingPlanController {

    @Autowired
    private TeachingPlanService teachingPlanService;

    @GetMapping
    public List<TeachingPlan> getAllTeachingPlans() {
        return teachingPlanService.getAllTeachingPlans();
    }

    @GetMapping("/query")
    public List<TeachingPlan> getTeachingPlansByConditions(
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer batchId,
            @RequestParam(required = false) Integer semesterId,
            @RequestParam(required = false) Integer courseId) {
        return teachingPlanService.getTeachingPlansByConditions(majorId, batchId, semesterId, courseId);
    }

    @GetMapping("/{id}")
    public TeachingPlan getTeachingPlanById(@PathVariable Integer id) {
        return teachingPlanService.getTeachingPlanById(id).orElseThrow();
    }

    @PostMapping
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public TeachingPlan createTeachingPlan(@RequestBody TeachingPlan teachingPlan) {
        return teachingPlanService.saveTeachingPlan(teachingPlan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public TeachingPlan updateTeachingPlan(@PathVariable Integer id, @RequestBody TeachingPlan teachingPlan) {
        teachingPlan.setPlanId(id);
        return teachingPlanService.saveTeachingPlan(teachingPlan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public void deleteTeachingPlan(@PathVariable Integer id) {
        teachingPlanService.deleteTeachingPlan(id);
    }

    /**
     * 获取教学计划统计信息
     */
    @GetMapping("/statistics")
    public Map<String, Object> getTeachingPlanStatistics(
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer batchId,
            @RequestParam(required = false) Integer semesterId) {
        return teachingPlanService.getTeachingPlanStatistics(majorId, batchId, semesterId);
    }

    /**
     * 归档教学计划
     */
    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public TeachingPlan archiveTeachingPlan(@PathVariable Integer id) {
        return teachingPlanService.archiveTeachingPlan(id);
    }

    /**
     * 作废教学计划
     */
    @PutMapping("/{id}/invalidate")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public TeachingPlan invalidateTeachingPlan(@PathVariable Integer id) {
        return teachingPlanService.invalidateTeachingPlan(id);
    }

    /**
     * 导出教学计划为Excel文件
     */
    @GetMapping("/export/excel")
    public void exportTeachingPlanToExcel(
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer batchId,
            @RequestParam(required = false) Integer semesterId,
            @RequestParam(required = false) Integer courseId,
            HttpServletResponse response) throws Exception {
        List<TeachingPlan> teachingPlans = teachingPlanService.getTeachingPlansByConditions(majorId, batchId, semesterId, courseId);
        byte[] excelBytes = com.tg.ums.utils.ExportUtils.exportTeachingPlanToExcel(teachingPlans);
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=teaching_plan.xlsx");
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
    }

    /**
     * 导出教学计划为PDF文件
     */
    @GetMapping("/export/pdf")
    public void exportTeachingPlanToPDF(
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer batchId,
            @RequestParam(required = false) Integer semesterId,
            @RequestParam(required = false) Integer courseId,
            HttpServletResponse response) throws Exception {
        List<TeachingPlan> teachingPlans = teachingPlanService.getTeachingPlansByConditions(majorId, batchId, semesterId, courseId);
        byte[] pdfBytes = com.tg.ums.utils.ExportUtils.exportTeachingPlanToPDF(teachingPlans);
        
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=teaching_plan.pdf");
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
