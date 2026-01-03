package com.tg.ums.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * 学生信息表
 */
@Data
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "student_code", unique = true, nullable = false, length = 20)
    private String studentCode;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @ManyToOne
    @JoinColumn(name = "major_id", nullable = false)
    private Major major;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

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
