package com.tg.ums.entity.score;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.question.Question;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 学生答题得分表
 */
@Data
@Entity
@Table(name = "student_answer_score", uniqueConstraints = @UniqueConstraint(columnNames = {"score_id", "question_id"}))
public class StudentAnswerScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_score_id")
    private Integer answerScoreId;

    @ManyToOne
    @JoinColumn(name = "score_id", nullable = false)
    private ExamScore examScore;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "student_answer", columnDefinition = "text")
    private String studentAnswer;

    @Column(name = "obtained_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal obtainedScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;
}
