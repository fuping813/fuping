package com.tg.ums.entity.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tg.ums.entity.base.Batch;
import com.tg.ums.entity.base.Major;
import com.tg.ums.entity.base.Semester;
import com.tg.ums.entity.course.Course;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * 教学计划表
 */
@Data
@Entity
@Table(name = "teaching_plan", uniqueConstraints = @UniqueConstraint(columnNames = {"major_id", "batch_id", "semester_id", "course_id"}))
public class TeachingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    @ManyToOne
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    // 临时字段，用于导入时关联处理
    @Transient
    private Integer majorId;
    
    @Transient
    private Integer batchId;
    
    @Transient
    private Integer semesterId;
    
    @Transient
    private Integer courseId;

    @Column(name = "teaching_group", nullable = false, length = 200)
    private String teachingGroup;

    @Column(name = "plan_status", nullable = false, length = 20)
    private String planStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
    
    @Column(name = "version", nullable = false, columnDefinition = "int default 1")
    private Integer version;
    
    @Column(name = "is_archived", nullable = false, columnDefinition = "tinyint(1) default 0")
    private Boolean isArchived;
    
    @Column(name = "archive_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date archiveTime;
    
    @Column(name = "remark", length = 200)
    private String remark;
}
