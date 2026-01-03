package com.tg.ums.entity.score;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.course.KnowledgePoint;
import com.tg.ums.entity.paper.ExamPaper;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 知识点得分统计表
 */
@Data
@Entity
@Table(name = "knowledge_point_score_stat", uniqueConstraints = @UniqueConstraint(columnNames = {"paper_id", "point_id"}))
public class KnowledgePointScoreStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Integer statId;

    @ManyToOne
    @JoinColumn(name = "paper_id", nullable = false)
    private ExamPaper examPaper;

    @ManyToOne
    @JoinColumn(name = "point_id", nullable = false, foreignKey = @ForeignKey(foreignKeyDefinition = "FOREIGN KEY (point_id) REFERENCES knowledge_point(point_id) ON DELETE CASCADE"))
    private KnowledgePoint knowledgePoint;

    @Column(name = "participating_count", nullable = false)
    private Integer participatingCount;

    @Column(name = "total_score", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "average_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal averageScore;

    @Column(name = "score_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal scoreRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
