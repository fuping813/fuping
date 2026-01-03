package com.tg.ums.repository;

import com.tg.ums.entity.base.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRoleCode(String roleCode);
}