package com.file.system.springboot.service;

import com.file.system.springboot.model.User;
import com.file.system.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final UserRepository userRepository;
    private final Map<String, Long> tokenToUserId = new ConcurrentHashMap<>();

    public TokenService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String issueToken(User user) {
        String token = UUID.randomUUID().toString();
        tokenToUserId.put(token, user.getId());
        return token;
    }

    public Optional<User> validate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        Long userId = tokenToUserId.get(token);
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    public void revoke(String token) {
        tokenToUserId.remove(token);
    }
}


