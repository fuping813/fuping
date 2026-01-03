package com.tg.ums.controller.base;

import com.tg.ums.entity.base.User;
import com.tg.ums.service.base.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取所有用户 - 仅管理员可访问
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(required = false) String username, @RequestParam(required = false) String role) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<User> users = userService.getUsersByUsernameAndRole(username, role);
            response.put("success", true);
            response.put("message", "获取用户列表成功");
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 根据ID获取用户 - 仅管理员可访问
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                response.put("success", true);
                response.put("message", "获取用户成功");
                response.put("data", user.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取用户失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 创建用户 - 仅管理员可访问
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 密码加密
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userService.saveUser(user);
            response.put("success", true);
            response.put("message", "用户创建成功");
            response.put("data", savedUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "用户创建失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 更新用户 - 仅管理员可访问
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> existingUser = userService.getUserById(id);
            if (existingUser.isPresent()) {
                User updatedUser = existingUser.get();
                updatedUser.setUsername(user.getUsername());
                updatedUser.setName(user.getName());
                updatedUser.setEmail(user.getEmail());
                updatedUser.setPhone(user.getPhone());
                updatedUser.setRole(user.getRole());
                updatedUser.setStatus(user.getStatus());
                // 如果密码不为空，则更新密码
                if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                    updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                User savedUser = userService.saveUser(updatedUser);
                response.put("success", true);
                response.put("message", "用户更新成功");
                response.put("data", savedUser);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "用户更新失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 删除用户 - 仅管理员可访问
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                userService.deleteUser(id);
                response.put("success", true);
                response.put("message", "用户删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "用户不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "用户删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 获取当前用户信息和权限 - 用于调试
     */
    @GetMapping("/current-user-details")
    public ResponseEntity<Map<String, Object>> getCurrentUserDetails(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (principal instanceof Authentication) {
                Authentication auth = (Authentication) principal;
                response.put("username", auth.getName());
                response.put("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
                response.put("authenticated", auth.isAuthenticated());
            }
            response.put("success", true);
            response.put("message", "获取当前用户信息成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取当前用户信息失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}