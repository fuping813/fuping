package com.tg.ums.service.auth;

import com.tg.ums.entity.base.User;
import com.tg.ums.service.base.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 密码加密方法
    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // 登录验证方法
    public User login(String username, String password) {
        // 使用Spring Security的AuthenticationManager进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // 如果认证成功，将认证信息存入SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 查找用户并返回
        return userService.getUserByUsername(username);
    }

    // 检查用户是否拥有指定角色
    public boolean hasRole(User user, String roleCode) {
        if (user == null) {
            return false;
        }
        return user.getRole().equals(roleCode);
    }
    
    // 测试密码是否匹配
    public boolean testPasswordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    // 生成密码哈希
    public String generatePasswordHash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    // 退出登录方法
    public void logout() {
        // 清除Spring Security上下文
        SecurityContextHolder.clearContext();
    }
}