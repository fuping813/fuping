package com.tg.ums.service.base;

import com.tg.ums.entity.base.Role;
import com.tg.ums.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Integer roleId) {
        return roleRepository.findById(roleId);
    }

    public Role getRoleByRoleCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Integer roleId) {
        roleRepository.deleteById(roleId);
    }
}