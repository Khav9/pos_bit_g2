package com.example.PosBit.controller;

import com.example.PosBit.model.UserInfo;
import com.example.PosBit.service.JWTService;
import com.example.PosBit.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserInfoService userInfoService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserController(UserInfoService userInfoService,
                          AuthenticationManager authenticationManager,
                          JWTService jwtService) {
        this.userInfoService = userInfoService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserInfo userInfo) {
        return ResponseEntity.ok(userInfoService.createUser(userInfo));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserInfo loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String token = jwtService.generateToken(authentication.getName());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userInfoService.getUserInfo(id));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the API!";
    }
}