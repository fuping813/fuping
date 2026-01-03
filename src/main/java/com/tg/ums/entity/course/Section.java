package com.tg.ums.entity.course;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 节信息表（支持二级和三级结构）
 */
@Data
@Entity
@Table(name = "section", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"chapter_id", "parent_section_id", "section_order"})
})
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Integer sectionId;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @ManyToOne
    @JoinColumn(name = "parent_section_id")
    private Section parentSection;

    @OneToMany(mappedBy = "parentSection", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Section> subSections;

    @Column(name = "section_name", nullable = false, length = 100)
    private String sectionName;

    @Column(name = "section_order", nullable = false)
    private Integer sectionOrder;

    @Column(name = "section_level", nullable = false)
    private Integer sectionLevel; // 1: 节（二级）, 2: 小节（三级）

    @Column(name = "section_content", columnDefinition = "text")
    private String sectionContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time", columnDefinition = "datetime default current_timestamp")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    private Date updateTime;
}
