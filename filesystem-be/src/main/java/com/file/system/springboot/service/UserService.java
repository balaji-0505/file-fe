package com.file.system.springboot.service;

import com.file.system.springboot.model.User;
import com.file.system.springboot.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setPasswordHash(hash);
        return userRepository.save(user);
    }
}


