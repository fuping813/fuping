package com.tg.ums.service.paper;

import com.tg.ums.entity.paper.ExamPaperQuestion;
import com.tg.ums.repository.ExamPaperQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamPaperQuestionService {

    @Autowired
    private ExamPaperQuestionRepository examPaperQuestionRepository;

    public List<ExamPaperQuestion> getAllExamPaperQuestions() {
        return examPaperQuestionRepository.findAll();
    }

    public Optional<ExamPaperQuestion> getExamPaperQuestionById(Integer id) {
        return examPaperQuestionRepository.findById(id);
    }

    public List<ExamPaperQuestion> getExamPaperQuestionsByPaperId(Integer paperId) {
        return examPaperQuestionRepository.findByExamPaper_PaperId(paperId);
    }

    public ExamPaperQuestion saveExamPaperQuestion(ExamPaperQuestion examPaperQuestion) {
        return examPaperQuestionRepository.save(examPaperQuestion);
    }

    public void deleteExamPaperQuestion(Integer id) {
        examPaperQuestionRepository.deleteById(id);
    }
}
