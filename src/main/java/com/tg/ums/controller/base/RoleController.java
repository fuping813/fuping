package com.tg.ums.controller.base;

import com.tg.ums.entity.base.Role;
import com.tg.ums.service.base.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取所有角色 - 仅管理员可访问
     */
    @GetMapping
    @PreAuthorize("hasRole('systemAdmin')")
    public ResponseEntity<Map<String, Object>> getAllRoles() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Role> roles = roleService.getAllRoles();
            response.put("success", true);
            response.put("message", "获取角色列表成功");
            response.put("data", roles);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取角色列表失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
