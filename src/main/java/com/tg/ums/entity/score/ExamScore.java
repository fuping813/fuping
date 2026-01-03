package com.tg.ums.entity.score;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.base.Student;
import com.tg.ums.entity.paper.ExamPaper;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 考试成绩表
 */
@Data
@Entity
@Table(name = "exam_score", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "paper_id"}))
public class ExamScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "score_id")
    private Integer scoreId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ExamPaper examPaper;

    @Column(name = "total_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "exam_date", nullable = false)
    private Date examDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
