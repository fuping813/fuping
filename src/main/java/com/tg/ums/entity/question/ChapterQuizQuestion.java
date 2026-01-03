package com.tg.ums.entity.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * 章节小测题目关联表
 */
@Data
@Entity
@Table(name = "chapter_quiz_question", uniqueConstraints = @UniqueConstraint(columnNames = {"quiz_id", "question_id"}))
public class ChapterQuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_question_id")
    private Integer quizQuestionId;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private ChapterQuiz chapterQuiz;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;
}
