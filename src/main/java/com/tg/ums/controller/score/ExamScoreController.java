package com.tg.ums.controller.score;

import com.tg.ums.entity.score.ExamScore;
import com.tg.ums.service.score.ExamScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-scores")
public class ExamScoreController {

    @Autowired
    private ExamScoreService examScoreService;

    @GetMapping
    public List<ExamScore> getAllExamScores() {
        return examScoreService.getAllExamScores();
    }

    @GetMapping("/paper/{paperId}")
    public List<ExamScore> getExamScoresByPaperId(@PathVariable Integer paperId) {
        return examScoreService.getExamScoresByPaperId(paperId);
    }

    @GetMapping("/student/{studentId}")
    public List<ExamScore> getExamScoresByStudentId(@PathVariable Integer studentId) {
        return examScoreService.getExamScoresByStudentId(studentId);
    }

    @GetMapping("/{id}")
    public ExamScore getExamScoreById(@PathVariable Integer id) {
        return examScoreService.getExamScoreById(id).orElseThrow();
    }

    @PostMapping
    public ExamScore createExamScore(@RequestBody ExamScore examScore) {
        return examScoreService.saveExamScore(examScore);
    }

    @PutMapping("/{id}")
    public ExamScore updateExamScore(@PathVariable Integer id, @RequestBody ExamScore examScore) {
        examScore.setScoreId(id);
        return examScoreService.saveExamScore(examScore);
    }

    @DeleteMapping("/{id}")
    public void deleteExamScore(@PathVariable Integer id) {
        examScoreService.deleteExamScore(id);
    }
}
