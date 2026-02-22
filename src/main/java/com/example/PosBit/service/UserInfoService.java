package com.example.PosBit.service;

import com.example.PosBit.dto.user.ChangePasswordRequest;
import com.example.PosBit.dto.user.UpdateProfileRequest;
import com.example.PosBit.dto.user.UserProfileResponse;
import com.example.PosBit.model.UserInfo;

import java.util.List;

public interface UserInfoService {
    String createUser(UserInfo userInfo);
    String getUserInfo(Integer userId);
    List<UserProfileResponse> getAllUsers();
    String updateUser(UserInfo userInfo);
    String deleteUser(Integer id);
    UserProfileResponse getMyProfile(String username);
    UserProfileResponse updateMyProfile(String username, UpdateProfileRequest request);
    String changeMyPassword(String username, ChangePasswordRequest request);
}
