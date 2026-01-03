package com.tg.ums.entity.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.course.Chapter;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 章节小测表
 */
@Data
@Entity
@Table(name = "chapter_quiz")
public class ChapterQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Integer quizId;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "quiz_name", nullable = false, length = 100)
    private String quizName;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "total_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "question_type_distribution", columnDefinition = "text")
    private String questionTypeDistribution;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
