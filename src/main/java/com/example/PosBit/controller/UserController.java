package com.example.PosBit.controller;

import com.example.PosBit.dto.user.ChangePasswordRequest;
import com.example.PosBit.dto.user.UpdateProfileRequest;
import com.example.PosBit.dto.user.UserProfileResponse;
import com.example.PosBit.model.UserInfo;
import com.example.PosBit.service.JWTService;
import com.example.PosBit.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
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

        String token = jwtService.generateToken(authentication.getName(), authentication.getAuthorities());
        return ResponseEntity.ok(token);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public java.util.List<UserProfileResponse> getAllUsers() {
        return userInfoService.getAllUsers();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@PathVariable Integer id, @RequestBody com.example.PosBit.model.UserInfo userInfo) {
        userInfo.setId(id);
        return ResponseEntity.ok(userInfoService.updateUser(userInfo));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userInfoService.deleteUser(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userInfoService.getUserInfo(id));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userInfoService.getMyProfile(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(Authentication authentication,
                                                               @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userInfoService.updateMyProfile(authentication.getName(), request));
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changeMyPassword(Authentication authentication,
                                                   @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(userInfoService.changeMyPassword(authentication.getName(), request));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to the API!";
    }
}
