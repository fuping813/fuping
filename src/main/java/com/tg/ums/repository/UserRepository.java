package com.tg.ums.repository;

import com.tg.ums.entity.base.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    List<User> findByUsernameContainingAndRole(String username, String role);
    List<User> findByUsernameContaining(String username);
    List<User> findByRole(String role);
}