package com.tg.ums.entity.course;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 章节信息表
 */
@Data
@Entity
@Table(name = "chapter", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "chapter_order"}))
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Integer chapterId;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course;

    @Column(name = "chapter_name", nullable = false, length = 100)
    private String chapterName;

    @Column(name = "chapter_order", nullable = false)
    private Integer chapterOrder;

    @Column(name = "chapter_description", columnDefinition = "text")
    private String chapterDescription;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;

    // 添加与知识点的双向关联，并设置级联删除
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<KnowledgePoint> knowledgePoints = new ArrayList<>();
}
