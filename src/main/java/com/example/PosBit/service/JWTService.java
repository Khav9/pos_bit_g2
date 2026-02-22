package com.example.PosBit.service;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public interface JWTService {
    String generateToken(String username, Collection<? extends GrantedAuthority> authorities);
    String extractUsername(String jwtToken);
    boolean validateToken(String jwtToken, UserDetails userDetails);
}