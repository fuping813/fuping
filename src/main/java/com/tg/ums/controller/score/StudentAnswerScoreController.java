package com.tg.ums.controller.score;

import com.tg.ums.entity.score.StudentAnswerScore;
import com.tg.ums.service.score.StudentAnswerScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student-answer-scores")
public class StudentAnswerScoreController {

    @Autowired
    private StudentAnswerScoreService studentAnswerScoreService;

    @GetMapping
    public List<StudentAnswerScore> getAllStudentAnswerScores() {
        return studentAnswerScoreService.getAllStudentAnswerScores();
    }

    @GetMapping("/exam-score/{examScoreId}")
    public List<StudentAnswerScore> getStudentAnswerScoresByExamScoreId(@PathVariable Integer examScoreId) {
        return studentAnswerScoreService.getStudentAnswerScoresByScoreId(examScoreId);
    }

    @GetMapping("/{id}")
    public StudentAnswerScore getStudentAnswerScoreById(@PathVariable Integer id) {
        return studentAnswerScoreService.getStudentAnswerScoreById(id).orElseThrow();
    }

    @PostMapping
    public StudentAnswerScore createStudentAnswerScore(@RequestBody StudentAnswerScore studentAnswerScore) {
        return studentAnswerScoreService.saveStudentAnswerScore(studentAnswerScore);
    }

    @PutMapping("/{id}")
    public StudentAnswerScore updateStudentAnswerScore(@PathVariable Integer id, @RequestBody StudentAnswerScore studentAnswerScore) {
        studentAnswerScore.setAnswerScoreId(id);
        return studentAnswerScoreService.saveStudentAnswerScore(studentAnswerScore);
    }

    @DeleteMapping("/{id}")
    public void deleteStudentAnswerScore(@PathVariable Integer id) {
        studentAnswerScoreService.deleteStudentAnswerScore(id);
    }
}
