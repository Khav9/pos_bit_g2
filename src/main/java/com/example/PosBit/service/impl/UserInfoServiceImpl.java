package com.example.PosBit.service.impl;

import com.example.PosBit.model.UserInfo;
import com.example.PosBit.repository.UserInfoRepository;
import com.example.PosBit.service.UserInfoService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String createUser(UserInfo userInfo) {
        if (userInfoRepository.findByUsername(userInfo.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        if (userInfo.getRoles() == null || userInfo.getRoles().isBlank()) {
            userInfo.setRoles("ROLE_USER");
        }

        userInfoRepository.save(userInfo);
        return "User created successfully";
    }

    @Override
    public String getUserInfo(Integer userId) {
        return userInfoRepository.findById(userId)
                .map(user -> "ID: " + user.getId() +
                        ", Username: " + user.getUsername() +
                        ", Roles: " + user.getRoles())
                .orElse("User not found with id: " + userId);
    }
}