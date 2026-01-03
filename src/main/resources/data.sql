-- 插入初始管理员用户 (密码: admin123)
INSERT INTO user (username, password, role, name, email, phone, status, create_time, update_time) VALUES
('admin', '$2a$10$mGfO52XOBhzaGneHFE9.3ujbXQaC4PEAk/hwjQppcgLCz/a/lAHRO', 'systemAdmin', '系统管理员', 'admin@example.com', '13800138000', 'enabled', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
