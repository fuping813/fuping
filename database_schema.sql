-- 本科专业培养管理系统数据库设计
-- 数据库名: tg_undergraduate_major_management

-- 创建数据库
CREATE DATABASE IF NOT EXISTS tg_undergraduate_major_management;
USE tg_undergraduate_major_management;

-- 1. 基础数据模块

-- 专业表
CREATE TABLE major (
    major_id INT PRIMARY KEY AUTO_INCREMENT,
    major_code VARCHAR(20) NOT NULL UNIQUE COMMENT '专业编码',
    major_name VARCHAR(50) NOT NULL COMMENT '专业名称',
    major_category VARCHAR(20) NOT NULL COMMENT '专业类别',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专业信息表';

-- 届次表
CREATE TABLE batch (
    batch_id INT PRIMARY KEY AUTO_INCREMENT,
    batch_year INT NOT NULL UNIQUE COMMENT '届次年份(如2024)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生届次表';

-- 学期表
CREATE TABLE semester (
    semester_id INT PRIMARY KEY AUTO_INCREMENT,
    semester_code VARCHAR(20) NOT NULL UNIQUE COMMENT '学期编码(如2024-2025-1)',
    semester_name VARCHAR(20) NOT NULL COMMENT '学期名称(如2024-2025学年第一学期)',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年(如2024-2025)',
    semester_type VARCHAR(10) NOT NULL COMMENT '学期类型(春季/秋季/夏季)',
    start_date DATE NOT NULL COMMENT '学期开始日期',
    end_date DATE NOT NULL COMMENT '学期结束日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学期信息表';

-- 教师表
CREATE TABLE teacher (
    teacher_id INT PRIMARY KEY AUTO_INCREMENT,
    teacher_code VARCHAR(20) NOT NULL UNIQUE COMMENT '教师工号',
    teacher_name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    title VARCHAR(20) NOT NULL COMMENT '职称',
    department VARCHAR(50) NOT NULL COMMENT '所属部门',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    email VARCHAR(50) NOT NULL COMMENT '电子邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- 学生表
CREATE TABLE student (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    student_code VARCHAR(20) NOT NULL UNIQUE COMMENT '学生学号',
    student_name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    gender VARCHAR(10) NOT NULL COMMENT '性别',
    major_id INT NOT NULL COMMENT '所属专业ID',
    batch_id INT NOT NULL COMMENT '届次ID',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    email VARCHAR(50) NOT NULL COMMENT '电子邮箱',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (major_id) REFERENCES major(major_id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batch(batch_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 2. 培养方案与课程管理模块

-- 课程表
CREATE TABLE course (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编码',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credits DECIMAL(3,1) NOT NULL COMMENT '学分',
    total_hours INT NOT NULL COMMENT '总学时',
    theory_hours INT NOT NULL COMMENT '理论学时',
    practice_hours INT NOT NULL COMMENT '实践学时',
    course_type VARCHAR(20) NOT NULL COMMENT '课程类型(必修课/选修课)',
    course_nature VARCHAR(20) NOT NULL COMMENT '课程性质(公共课/基础课/专业课)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- 教学计划表
CREATE TABLE teaching_plan (
    plan_id INT PRIMARY KEY AUTO_INCREMENT,
    major_id INT NOT NULL COMMENT '专业ID',
    batch_id INT NOT NULL COMMENT '届次ID',
    semester_id INT NOT NULL COMMENT '学期ID',
    course_id INT NOT NULL COMMENT '课程ID',
    teaching_group VARCHAR(200) NOT NULL COMMENT '授课教师组(多个教师用逗号分隔)',
    plan_status VARCHAR(20) NOT NULL DEFAULT '生效' COMMENT '计划状态(生效/作废/归档)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (major_id) REFERENCES major(major_id) ON DELETE CASCADE,
    FOREIGN KEY (batch_id) REFERENCES batch(batch_id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semester(semester_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    UNIQUE KEY uk_plan_unique (major_id, batch_id, semester_id, course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学计划表';

-- 3. 课程目录与章节管理模块

-- 章节表
CREATE TABLE chapter (
    chapter_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    chapter_name VARCHAR(100) NOT NULL COMMENT '章节名称',
    chapter_order INT NOT NULL COMMENT '章节排序',
    chapter_description TEXT COMMENT '章节描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    UNIQUE KEY uk_chapter_unique (course_id, chapter_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节信息表';

-- 节表
CREATE TABLE section (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    chapter_id INT NOT NULL COMMENT '章节ID',
    section_name VARCHAR(100) NOT NULL COMMENT '节名称',
    section_order INT NOT NULL COMMENT '节排序',
    section_content TEXT COMMENT '节内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES chapter(chapter_id) ON DELETE CASCADE,
    UNIQUE KEY uk_section_unique (chapter_id, section_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节信息表';

-- 4. 课程知识点管理模块

-- 知识点表
CREATE TABLE knowledge_point (
    point_id INT PRIMARY KEY AUTO_INCREMENT,
    chapter_id INT NOT NULL COMMENT '章节ID',
    point_name VARCHAR(100) NOT NULL COMMENT '知识点名称',
    point_description TEXT COMMENT '知识点描述',
    key_points TEXT COMMENT '核心考点说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES chapter(chapter_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点信息表';

-- 5. 题库与章节小测建设模块

-- 题目表
CREATE TABLE question (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL COMMENT '课程ID',
    point_id INT NOT NULL COMMENT '知识点ID',
    question_type VARCHAR(20) NOT NULL COMMENT '题目类型(选择题/判断题/简答题/填空题)',
    question_content TEXT NOT NULL COMMENT '题干',
    options TEXT COMMENT '选项(客观题，JSON格式存储)',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    score DECIMAL(5,2) NOT NULL COMMENT '分值',
    difficulty_level VARCHAR(10) NOT NULL COMMENT '难度等级(简单/中等/困难)',
    is_quiz_available TINYINT(1) DEFAULT 1 COMMENT '是否可用于章节小测(1:是，0:否)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (point_id) REFERENCES knowledge_point(point_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目信息表';

-- 章节小测表
CREATE TABLE chapter_quiz (
    quiz_id INT PRIMARY KEY AUTO_INCREMENT,
    chapter_id INT NOT NULL COMMENT '章节ID',
    quiz_name VARCHAR(100) NOT NULL COMMENT '小测名称',
    total_questions INT NOT NULL COMMENT '题目数量',
    total_score DECIMAL(5,2) NOT NULL COMMENT '总分',
    question_type_distribution TEXT COMMENT '题型分布(JSON格式)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (chapter_id) REFERENCES chapter(chapter_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节小测表';

-- 章节小测题目关联表
CREATE TABLE chapter_quiz_question (
    quiz_question_id INT PRIMARY KEY AUTO_INCREMENT,
    quiz_id INT NOT NULL COMMENT '章节小测ID',
    question_id INT NOT NULL COMMENT '题目ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES chapter_quiz(quiz_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
    UNIQUE KEY uk_quiz_question_unique (quiz_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节小测题目关联表';

-- 6. 试题组卷模块

-- 试卷表
CREATE TABLE exam_paper (
    paper_id INT PRIMARY KEY AUTO_INCREMENT,
    paper_name VARCHAR(100) NOT NULL COMMENT '试卷名称',
    course_id INT NOT NULL COMMENT '课程ID',
    paper_type VARCHAR(20) NOT NULL COMMENT '试卷类型(章节小测/综合考试)',
    chapter_id INT NULL COMMENT '章节ID(仅章节小测时填写)',
    total_questions INT NOT NULL COMMENT '题目数量',
    total_score DECIMAL(5,2) NOT NULL COMMENT '总分',
    knowledge_point_distribution TEXT COMMENT '知识点分值分布(JSON格式)',
    question_type_distribution TEXT COMMENT '题型分布(JSON格式)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (chapter_id) REFERENCES chapter(chapter_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷信息表';

-- 试卷题目关联表
CREATE TABLE exam_paper_question (
    paper_question_id INT PRIMARY KEY AUTO_INCREMENT,
    paper_id INT NOT NULL COMMENT '试卷ID',
    question_id INT NOT NULL COMMENT '题目ID',
    question_order INT NOT NULL COMMENT '题目顺序',
    score DECIMAL(5,2) NOT NULL COMMENT '本题分值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (paper_id) REFERENCES exam_paper(paper_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
    UNIQUE KEY uk_paper_question_unique (paper_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷题目关联表';

-- 7. 考试成绩与知识点得分统计模块

-- 考试成绩表
CREATE TABLE exam_score (
    score_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL COMMENT '学生ID',
    paper_id INT NOT NULL COMMENT '试卷ID',
    total_score DECIMAL(5,2) NOT NULL COMMENT '总分',
    exam_date DATETIME NOT NULL COMMENT '考试日期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (paper_id) REFERENCES exam_paper(paper_id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_paper_unique (student_id, paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试成绩表';

-- 学生答题得分表
CREATE TABLE student_answer_score (
    answer_score_id INT PRIMARY KEY AUTO_INCREMENT,
    score_id INT NOT NULL COMMENT '考试成绩ID',
    question_id INT NOT NULL COMMENT '题目ID',
    student_answer TEXT COMMENT '学生答案',
    obtained_score DECIMAL(5,2) NOT NULL COMMENT '实得分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (score_id) REFERENCES exam_score(score_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
    UNIQUE KEY uk_score_question_unique (score_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生答题得分表';

-- 知识点得分统计表
CREATE TABLE knowledge_point_score_stat (
    stat_id INT PRIMARY KEY AUTO_INCREMENT,
    paper_id INT NOT NULL COMMENT '试卷ID',
    point_id INT NOT NULL COMMENT '知识点ID',
    participating_count INT NOT NULL COMMENT '参与人数',
    total_score DECIMAL(10,2) NOT NULL COMMENT '总得分',
    average_score DECIMAL(5,2) NOT NULL COMMENT '平均分',
    score_rate DECIMAL(5,2) NOT NULL COMMENT '得分率(%)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (paper_id) REFERENCES exam_paper(paper_id) ON DELETE CASCADE,
    FOREIGN KEY (point_id) REFERENCES knowledge_point(point_id) ON DELETE CASCADE,
    UNIQUE KEY uk_paper_point_unique (paper_id, point_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点得分统计表';

-- 创建索引以提高查询性能
CREATE INDEX idx_teaching_plan_major_batch_semester ON teaching_plan(major_id, batch_id, semester_id);
CREATE INDEX idx_question_course_point ON question(course_id, point_id);
CREATE INDEX idx_exam_paper_course_chapter ON exam_paper(course_id, chapter_id);
CREATE INDEX idx_exam_score_student_paper ON exam_score(student_id, paper_id);
CREATE INDEX idx_student_answer_score_score_question ON student_answer_score(score_id, question_id);
CREATE INDEX idx_knowledge_point_score_stat_paper_point ON knowledge_point_score_stat(paper_id, point_id);

-- 插入基础数据
-- 专业数据
INSERT INTO major (major_code, major_name, major_category) VALUES
('080901', '计算机科学与技术', '工学'),
('080903', '网络工程', '工学'),
('080905', '物联网工程', '工学'),
('080910', '数据科学与大数据技术', '工学');

-- 届次数据
INSERT INTO batch (batch_year) VALUES
(2024),
(2025),
(2026);

-- 学期数据
INSERT INTO semester (semester_code, semester_name, academic_year, semester_type, start_date, end_date) VALUES
('2024-2025-1', '2024-2025学年第一学期', '2024-2025', '秋季', '2024-09-01', '2025-01-15'),
('2024-2025-2', '2024-2025学年第二学期', '2024-2025', '春季', '2025-02-20', '2025-06-30'),
('2025-2026-1', '2025-2026学年第一学期', '2025-2026', '秋季', '2025-09-01', '2026-01-15'),
('2025-2026-2', '2025-2026学年第二学期', '2025-2026', '春季', '2026-02-20', '2026-06-30');

-- 课程类型数据
INSERT INTO course (course_code, course_name, credits, total_hours, theory_hours, practice_hours, course_type, course_nature) VALUES
('CS101', '计算机导论', 3.0, 48, 40, 8, '必修课', '基础课'),
('CS102', '高等数学', 4.0, 64, 64, 0, '必修课', '公共课'),
('CS201', '数据结构', 4.0, 64, 48, 16, '必修课', '专业课'),
('CS202', '数据库原理', 3.5, 56, 40, 16, '必修课', '专业课'),
('IoT101', '物联网概论', 3.0, 48, 40, 8, '必修课', '专业课'),
('DS101', '大数据技术基础', 3.0, 48, 32, 16, '必修课', '专业课');

SELECT '数据库表创建完成！' AS message;