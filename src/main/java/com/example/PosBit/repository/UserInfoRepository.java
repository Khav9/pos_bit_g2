package com.example.PosBit.repository;

import com.example.PosBit.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByUsername(String username);
    Optional<UserInfo> findByEmail(String email);
    boolean existsByEmail(String email);
}
