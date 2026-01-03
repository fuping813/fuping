package com.tg.ums.entity.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.base.Batch;
import com.tg.ums.entity.base.Major;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 培养方案表
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "training_program", uniqueConstraints = @UniqueConstraint(columnNames = {"major_id", "batch_id"}))
public class TrainingProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Integer programId;

    @ManyToOne
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(name = "program_name", nullable = false, length = 200)
    private String programName;

    @Column(name = "total_credits", nullable = false)
    private Double totalCredits;

    @Column(name = "total_courses", nullable = false)
    private Integer totalCourses;

    @Column(name = "program_status", nullable = false, length = 20)
    private String programStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;

    // 临时字段，用于导入时关联处理
    @Transient
    private Integer majorId;
    
    @Transient
    private Integer batchId;
}