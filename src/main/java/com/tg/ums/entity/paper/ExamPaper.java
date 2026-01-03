package com.tg.ums.entity.paper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.course.Chapter;
import com.tg.ums.entity.course.Course;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 试卷信息表
 */
@Data
@Entity
@Table(name = "exam_paper")
public class ExamPaper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paper_id")
    private Integer paperId;

    @Column(name = "paper_name", nullable = false, length = 100)
    private String paperName;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "paper_type", nullable = false, length = 20)
    private String paperType;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(name = "total_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "knowledge_point_distribution", columnDefinition = "text")
    private String knowledgePointDistribution;

    @Column(name = "question_type_distribution", columnDefinition = "text")
    private String questionTypeDistribution;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
