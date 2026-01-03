package com.tg.ums.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * 教师信息表
 */
@Data
@Entity
@Table(name = "teacher")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private Integer teacherId;

    @Column(name = "teacher_code", unique = true, nullable = false, length = 20)
    private String teacherCode;

    @Column(name = "teacher_name", nullable = false, length = 50)
    private String teacherName;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
