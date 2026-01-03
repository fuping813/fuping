package com.tg.ums.controller.question;

import com.tg.ums.entity.question.ChapterQuiz;
import com.tg.ums.service.question.ChapterQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapter-quizzes")
public class ChapterQuizController {

    @Autowired
    private ChapterQuizService chapterQuizService;

    @GetMapping
    public List<ChapterQuiz> getAllChapterQuizzes() {
        return chapterQuizService.getAllChapterQuizzes();
    }

    @GetMapping("/chapter/{chapterId}")
    public List<ChapterQuiz> getChapterQuizzesByChapterId(@PathVariable Integer chapterId) {
        return chapterQuizService.getChapterQuizzesByChapterId(chapterId);
    }

    @GetMapping("/{id}")
    public ChapterQuiz getChapterQuizById(@PathVariable Integer id) {
        return chapterQuizService.getChapterQuizById(id).orElseThrow();
    }

    @PostMapping
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector') or hasRole('teacher')")
    public ChapterQuiz createChapterQuiz(@RequestBody ChapterQuiz chapterQuiz) {
        return chapterQuizService.saveChapterQuiz(chapterQuiz);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public ChapterQuiz updateChapterQuiz(@PathVariable Integer id, @RequestBody ChapterQuiz chapterQuiz) {
        chapterQuiz.setQuizId(id);
        return chapterQuizService.saveChapterQuiz(chapterQuiz);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector')")
    public void deleteChapterQuiz(@PathVariable Integer id) {
        chapterQuizService.deleteChapterQuiz(id);
    }

    /**
     * 从章节知识点抽取题目生成小测
     * @param chapterId 章节ID
     * @param quizName 小测名称
     * @param questionsPerPoint 每个知识点抽取的题目数量
     * @return 生成的章节小测
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('systemAdmin') or hasRole('majorDirector') or hasRole('teacher')")
    public ChapterQuiz generateQuizFromChapterKnowledgePoints(
            @RequestParam Integer chapterId,
            @RequestParam String quizName,
            @RequestParam(defaultValue = "2") int questionsPerPoint) {
        return chapterQuizService.generateQuizFromChapterKnowledgePoints(chapterId, quizName, questionsPerPoint);
    }
}
