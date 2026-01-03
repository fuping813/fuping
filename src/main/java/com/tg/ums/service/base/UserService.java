package com.tg.ums.service.base;

import com.tg.ums.entity.base.User;
import com.tg.ums.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByUsernameAndRole(String username, String role) {
        if (username != null && !username.isEmpty() && role != null && !role.equals("所有角色")) {
            return userRepository.findByUsernameContainingAndRole(username, role);
        } else if (username != null && !username.isEmpty()) {
            return userRepository.findByUsernameContaining(username);
        } else if (role != null && !role.equals("所有角色")) {
            return userRepository.findByRole(role);
        } else {
            return userRepository.findAll();
        }
    }

    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }
}