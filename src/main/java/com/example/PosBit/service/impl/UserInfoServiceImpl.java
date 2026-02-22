package com.example.PosBit.service.impl;

import com.example.PosBit.dto.user.ChangePasswordRequest;
import com.example.PosBit.dto.user.UpdateProfileRequest;
import com.example.PosBit.dto.user.UserProfileResponse;
import com.example.PosBit.model.UserInfo;
import com.example.PosBit.repository.UserInfoRepository;
import com.example.PosBit.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserInfoRepository userInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final String defaultAdminUsername;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository,
                               BCryptPasswordEncoder passwordEncoder,
                               @Value("${app.default-admin.username:admin}") String defaultAdminUsername) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultAdminUsername = defaultAdminUsername;
    }

    @Override
    public String createUser(UserInfo userInfo) {
        if (userInfoRepository.findByUsername(userInfo.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userInfo.getEmail() != null && !userInfo.getEmail().isBlank()
                && userInfoRepository.existsByEmail(userInfo.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        // Public registration is always standard user access.
        userInfo.setRoles(ROLE_USER);
        if (userInfo.getFullName() == null || userInfo.getFullName().isBlank()) {
            userInfo.setFullName(userInfo.getUsername());
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

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userInfoRepository.findAll()
                .stream()
                .map(this::toUserProfileResponse)
                .toList();
    }

    @Override
    public String updateUser(UserInfo userInfo) {
        UserInfo existingUser = userInfoRepository.findById(userInfo.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userInfo.getRoles() != null) {
            existingUser.setRoles(normalizeRoles(userInfo.getRoles(), existingUser.getUsername()));
        }

        userInfoRepository.save(existingUser);
        return "User updated successfully";
    }

    @Override
    public String deleteUser(Integer id) {
        UserInfo existingUser = userInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (defaultAdminUsername.equals(existingUser.getUsername())) {
            throw new RuntimeException("Default admin user cannot be deleted");
        }
        userInfoRepository.deleteById(id);
        return "User deleted successfully";
    }

    @Override
    public UserProfileResponse getMyProfile(String username) {
        UserInfo user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return toUserProfileResponse(user);
    }

    @Override
    public UserProfileResponse updateMyProfile(String username, UpdateProfileRequest request) {
        UserInfo user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String requestedEmail = request.getEmail() == null ? null : request.getEmail().trim();
        if (requestedEmail != null && !requestedEmail.isBlank()) {
            userInfoRepository.findByEmail(requestedEmail)
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new RuntimeException("Email already exists");
                    });
            user.setEmail(requestedEmail);
        } else {
            user.setEmail(null);
        }

        user.setFullName(nullSafeTrim(request.getFullName()));
        user.setPhone(nullSafeTrim(request.getPhone()));
        user.setAddress(nullSafeTrim(request.getAddress()));
        userInfoRepository.save(user);
        return toUserProfileResponse(user);
    }

    @Override
    public String changeMyPassword(String username, ChangePasswordRequest request) {
        UserInfo user = userInfoRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new RuntimeException("Current password is required");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userInfoRepository.save(user);
        return "Password updated successfully";
    }

    private String normalizeRoles(String rawRoles, String username) {
        if (defaultAdminUsername.equals(username)) {
            return ROLE_USER + "," + ROLE_ADMIN;
        }
        if (rawRoles == null || rawRoles.isBlank()) {
            return ROLE_USER;
        }

        boolean hasAdmin = false;
        for (String role : rawRoles.split(",")) {
            String trimmedRole = role.trim();
            if (ROLE_ADMIN.equals(trimmedRole)) {
                hasAdmin = true;
                break;
            }
        }

        if (hasAdmin) {
            throw new RuntimeException("Only the default admin account can have admin role");
        }
        return ROLE_USER;
    }

    private UserProfileResponse toUserProfileResponse(UserInfo user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setRoles(user.getRoles());
        return response;
    }

    private String nullSafeTrim(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
