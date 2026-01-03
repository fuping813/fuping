package com.tg.ums.config;

import com.tg.ums.entity.base.User;
import com.tg.ums.service.base.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * 密码初始化类，为指定角色的用户设置默认密码
 */
@Configuration
public class PasswordInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 为专业主任、授课教师和学生用户初始化密码为123456
        System.out.println("开始初始化用户密码...");
        
        List<User> users = userService.getAllUsers();
        int updatedCount = 0;
        
        for (User user : users) {
            String role = user.getRole();
            // 检查角色是否为专业主任、授课教师或学生
            if ("majorDirector".equals(role) || "teacher".equals(role) || "student".equals(role)) {
                // 设置密码为123456
                user.setPassword(passwordEncoder.encode("123456"));
                userService.saveUser(user);
                updatedCount++;
                System.out.println("已更新用户 "+ user.getUsername() +" 的密码");
            }
        }
        
        System.out.println("用户密码初始化完成，共更新了 " + updatedCount + " 个用户的密码");
    }
}
