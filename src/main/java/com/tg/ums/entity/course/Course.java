package com.tg.ums.entity.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tg.ums.entity.base.Major;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 课程信息表
 */
@Data
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "course_code", unique = true, nullable = false, length = 20)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "major_id")
    private Integer majorId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "major_id", referencedColumnName = "major_id", insertable = false, updatable = false)
    private Major major;

    @Column(name = "credits", nullable = false, precision = 3, scale = 1)
    private BigDecimal credits;

    @Column(name = "total_hours", nullable = false)
    private Integer totalHours;

    @Column(name = "theory_hours", nullable = false)
    private Integer theoryHours;

    @Column(name = "practice_hours", nullable = false)
    private Integer practiceHours;

    @Column(name = "course_type", nullable = false, length = 20)
    private String courseType;

    @Column(name = "course_nature", nullable = false, length = 20)
    private String courseNature;

    @Column(name = "course_status", nullable = false, length = 20)
    private String courseStatus = "enabled";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
    
    // 添加与章节的双向关联，并设置级联删除
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Chapter> chapters = new ArrayList<>();
}
