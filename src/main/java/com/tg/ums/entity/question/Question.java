package com.tg.ums.entity.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.course.Course;
import com.tg.ums.entity.course.KnowledgePoint;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 题目信息表
 */
@Data
@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (point_id) REFERENCES knowledge_point(point_id) ON DELETE CASCADE"))
    private KnowledgePoint knowledgePoint;

    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType;

    // 临时字段，用于导入时关联课程ID
    @Transient
    private Integer courseId;

    // 临时字段，用于导入时关联知识点ID
    @Transient
    private Integer knowledgePointId;

    @Column(name = "question_content", nullable = false, columnDefinition = "text")
    private String questionContent;

    @Column(name = "options", columnDefinition = "text")
    private String options;

    @Column(name = "correct_answer", nullable = false, columnDefinition = "text")
    private String correctAnswer;

    @Column(name = "score", nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "difficulty_level", nullable = false, length = 10)
    private String difficultyLevel;

    @Column(name = "is_quiz_available", columnDefinition = "tinyint(1) default 1")
    private Boolean isQuizAvailable;

    @Column(name = "analysis", columnDefinition = "text")
    private String analysis;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
