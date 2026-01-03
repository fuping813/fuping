package com.tg.ums.service.question;

import com.tg.ums.entity.question.ChapterQuizQuestion;
import com.tg.ums.repository.ChapterQuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChapterQuizQuestionService {

    @Autowired
    private ChapterQuizQuestionRepository chapterQuizQuestionRepository;

    public List<ChapterQuizQuestion> getAllChapterQuizQuestions() {
        return chapterQuizQuestionRepository.findAll();
    }

    public Optional<ChapterQuizQuestion> getChapterQuizQuestionById(Integer id) {
        return chapterQuizQuestionRepository.findById(id);
    }

    public List<ChapterQuizQuestion> getChapterQuizQuestionsByQuizId(Integer quizId) {
        // 先获取章节小测对象，然后查询题目
        com.tg.ums.entity.question.ChapterQuiz chapterQuiz = new com.tg.ums.entity.question.ChapterQuiz();
        chapterQuiz.setQuizId(quizId);
        return chapterQuizQuestionRepository.findByChapterQuiz(chapterQuiz);
    }

    public ChapterQuizQuestion saveChapterQuizQuestion(ChapterQuizQuestion chapterQuizQuestion) {
        return chapterQuizQuestionRepository.save(chapterQuizQuestion);
    }

    public void deleteChapterQuizQuestion(Integer id) {
        chapterQuizQuestionRepository.deleteById(id);
    }
}
