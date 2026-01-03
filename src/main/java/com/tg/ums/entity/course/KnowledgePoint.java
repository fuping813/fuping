package com.tg.ums.entity.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tg.ums.entity.question.Question;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 知识点信息表
 */
@Data
@Entity
@Table(name = "knowledge_point")
public class KnowledgePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Integer pointId;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private KnowledgePoint parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<KnowledgePoint> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "knowledgePoint", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Question> questions = new ArrayList<>();

    @Column(name = "point_name", nullable = false, length = 100)
    private String pointName;

    // 临时字段，用于导入时关联章节ID
    @Transient
    private Integer chapterId;

    // 临时字段，用于导入时关联父知识点ID
    @Transient
    private Integer parentId;

    @Column(name = "point_description", columnDefinition = "text")
    private String pointDescription;

    @Column(name = "key_points", columnDefinition = "text")
    private String keyPoints;

    @Column(name = "point_level", nullable = false)
    private Integer level;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
