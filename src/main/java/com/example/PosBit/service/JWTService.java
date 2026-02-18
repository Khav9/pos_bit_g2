package com.example.PosBit.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    String generateToken(String username);
    String extractUsername(String jwtToken);
    boolean validateToken(String jwtToken, UserDetails userDetails);
}