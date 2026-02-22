package com.example.PosBit.config;

import com.example.PosBit.model.UserInfo;
import com.example.PosBit.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserInfoRepository userInfoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.default-admin.username:admin}")
    private String defaultAdminUsername;

    @Value("${app.default-admin.password:admin12345}")
    private String defaultAdminPassword;

    public DefaultAdminInitializer(UserInfoRepository userInfoRepository,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        UserInfo defaultAdmin = userInfoRepository.findByUsername(defaultAdminUsername)
                .orElseGet(() -> createDefaultAdmin(defaultAdminUsername, defaultAdminPassword));

        boolean adminUpdated = false;
        if (!passwordEncoder.matches(defaultAdminPassword, defaultAdmin.getPassword())) {
            defaultAdmin.setPassword(passwordEncoder.encode(defaultAdminPassword));
            adminUpdated = true;
        }
        if (!hasAdminRole(defaultAdmin.getRoles())) {
            defaultAdmin.setRoles(ROLE_USER + "," + ROLE_ADMIN);
            adminUpdated = true;
        }
        if (adminUpdated) {
            userInfoRepository.save(defaultAdmin);
        }

        // Enforce single-admin policy by demoting any other account with admin role.
        List<UserInfo> allUsers = userInfoRepository.findAll();
        for (UserInfo user : allUsers) {
            if (!defaultAdminUsername.equals(user.getUsername()) && hasAdminRole(user.getRoles())) {
                user.setRoles(ROLE_USER);
                userInfoRepository.save(user);
            }
        }
    }

    private UserInfo createDefaultAdmin(String username, String rawPassword) {
        UserInfo admin = new UserInfo();
        admin.setUsername(username);
        admin.setFullName("System Administrator");
        admin.setEmail("admin@posbit.local");
        admin.setPhone("+1-202-555-0100");
        admin.setAddress("Head Office");
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setRoles(ROLE_USER + "," + ROLE_ADMIN);
        return userInfoRepository.save(admin);
    }

    private boolean hasAdminRole(String rawRoles) {
        if (rawRoles == null || rawRoles.isBlank()) {
            return false;
        }
        for (String role : rawRoles.split(",")) {
            if (ROLE_ADMIN.equals(role.trim())) {
                return true;
            }
        }
        return false;
    }
}
