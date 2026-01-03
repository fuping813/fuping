-- 添加专业主任用户
INSERT INTO user (username, password, role, name, email, phone, status, create_time, update_time)
VALUES ('major_director', '$2a$10$eWjwK5QV9mzF6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q', 'majorDirector', '专业主任', 'major_director@example.com', '13800138001', 'enabled', NOW(), NOW());

-- 添加授课教师用户
INSERT INTO user (username, password, role, name, email, phone, status, create_time, update_time)
VALUES ('teacher', '$2a$10$eWjwK5QV9mzF6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q5F6y6Q', 'teacher', '授课教师', 'teacher@example.com', '13800138002', 'enabled', NOW(), NOW());
