package com.tg.ums.controller.auth;

import com.tg.ums.entity.base.User;
import com.tg.ums.service.auth.AuthService;
import com.tg.ums.service.base.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;

    // 登录接口
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        try {
            // 验证登录
            User user = authService.login(username, password);
            
            if (user != null) {
                // 登录成功
                response.put("success", true);
                response.put("message", "登录成功");
                response.put("user", user);
            } else {
                // 登录失败
                response.put("success", false);
                response.put("message", "用户名或密码错误");
            }
        } catch (Exception e) {
            // 处理认证异常
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        
        return response;
    }

    // 退出登录接口
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        // 调用service层的logout方法清除Spring Security上下文
        authService.logout();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "退出登录成功");
        return response;
    }

    // 获取当前用户信息接口
    @GetMapping("/current-user")
    public User getCurrentUser() {
        // 从Spring Security上下文中获取当前登录用户
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null || "anonymousUser".equals(auth.getPrincipal())) {
            // 用户未登录或匿名用户，返回null
            return null;
        }
        
        String username;
        Object principal = auth.getPrincipal();
        
        // 检查principal类型，可能是String或User对象
        if (principal instanceof String) {
            username = (String) principal;
        } else {
            username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        }
        
        // 根据用户名从数据库获取完整用户信息
        User user = userService.getUserByUsername(username);
        return user;
    }
    
    // 测试密码匹配接口
    @PostMapping("/test-password")
    public Map<String, Object> testPassword(@RequestBody Map<String, String> passwordData) {
        Map<String, Object> response = new HashMap<>();
        String rawPassword = passwordData.get("rawPassword");
        String encodedPassword = passwordData.get("encodedPassword");
        
        boolean match = authService.testPasswordMatch(rawPassword, encodedPassword);
        response.put("match", match);
        return response;
    }
    
    // 生成密码哈希接口
    @PostMapping("/generate-password")
    public Map<String, Object> generatePassword(@RequestBody Map<String, String> passwordData) {
        Map<String, Object> response = new HashMap<>();
        String rawPassword = passwordData.get("rawPassword");
        
        String encodedPassword = authService.generatePasswordHash(rawPassword);
        response.put("encodedPassword", encodedPassword);
        return response;
    }
}