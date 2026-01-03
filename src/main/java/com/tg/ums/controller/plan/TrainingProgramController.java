package com.tg.ums.controller.plan;

import com.tg.ums.common.RoleConstants;
import com.tg.ums.entity.plan.TrainingProgram;
import com.tg.ums.service.plan.TrainingProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-programs")
public class TrainingProgramController {

    @Autowired
    private TrainingProgramService trainingProgramService;

    /**
     * 获取所有培养方案
     */
    @GetMapping
    public List<TrainingProgram> getAllTrainingPrograms() {
        return trainingProgramService.getAllTrainingPrograms();
    }

    /**
     * 根据条件查询培养方案
     */
    @GetMapping("/query")
    public List<TrainingProgram> getTrainingProgramsByConditions(
            @RequestParam(required = false) Integer majorId,
            @RequestParam(required = false) Integer batchId) {
        return trainingProgramService.getTrainingProgramsByConditions(majorId, batchId);
    }

    /**
     * 根据ID获取培养方案
     */
    @GetMapping("/{id}")
    public TrainingProgram getTrainingProgramById(@PathVariable Integer id) {
        return trainingProgramService.getTrainingProgramById(id).orElseThrow();
    }

    /**
     * 创建培养方案
     */
    @PostMapping
    // @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public ResponseEntity<TrainingProgram> createTrainingProgram(@RequestBody TrainingProgram trainingProgram) {
        TrainingProgram savedProgram = trainingProgramService.saveTrainingProgram(trainingProgram);
        return ResponseEntity.ok(savedProgram);
    }

    /**
     * 更新培养方案
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public TrainingProgram updateTrainingProgram(@PathVariable Integer id, @RequestBody TrainingProgram trainingProgram) {
        trainingProgram.setProgramId(id);
        return trainingProgramService.saveTrainingProgram(trainingProgram);
    }

    /**
     * 删除培养方案
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public void deleteTrainingProgram(@PathVariable Integer id) {
        trainingProgramService.deleteTrainingProgram(id);
    }

    /**
     * 从URL导入单个培养方案
     */
    @PostMapping("/import-from-url")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public TrainingProgram importTrainingProgramFromUrl(@RequestBody ImportUrlRequest request) {
        return trainingProgramService.importTrainingProgramFromUrl(request.getUrl());
    }
    
    /**
     * 从URL导入多个培养方案
     */
    @PostMapping("/import-multiple-from-url")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public List<TrainingProgram> importMultipleTrainingProgramsFromUrl(@RequestBody ImportUrlRequest request) {
        return trainingProgramService.importTrainingProgramsFromUrl(request.getUrl());
    }

    /**
     * 导入URL请求体
     */
    static class ImportUrlRequest {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}