package com.example.PosBit.service;

import com.example.PosBit.model.UserInfo;

public interface UserInfoService {
    String createUser(UserInfo userInfo);
    String getUserInfo(Integer userId);
}