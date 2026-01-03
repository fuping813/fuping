package com.tg.ums.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * 学期信息表
 */
@Data
@Entity
@Table(name = "semester")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "semester_id")
    private Integer semesterId;

    @Column(name = "semester_code", unique = true, nullable = false, length = 20)
    private String semesterCode;

    @Column(name = "semester_name", nullable = false, length = 20)
    private String semesterName;

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "semester_type", nullable = false, length = 10)
    private String semesterType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
