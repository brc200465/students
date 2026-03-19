package com.example.studentms.service;

import com.example.studentms.entity.User;

public interface UserService{
    User findByUsername(String username);
    int register(User user);
    User login(String username,String password);
}
