package com.tg.ums.entity.paper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.question.Question;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 试卷题目关联表
 */
@Data
@Entity
@Table(name = "exam_paper_question", uniqueConstraints = @UniqueConstraint(columnNames = {"paper_id", "question_id"}))
public class ExamPaperQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_question_id")
    private Integer paperQuestionId;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ExamPaper examPaper;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE"))
    private Question question;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;
}
